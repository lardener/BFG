package bfg.evaluator;

import java.io.IOException;
import java.nio.file.Path;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MultiPhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.MMapDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;

import bfg.MutationStep;

public class MultiPhraseEvaluator implements Evaluator {
    private static final String CONTENTS = "contents";
    private static final Logger log = LoggerFactory.getLogger(MultiPhraseEvaluator.class);

    private int maxDistance;
    private Path ndxDir;
    private int phraseSlop;
    private ScoringCriteria scoringCriteria;

    public MultiPhraseEvaluator(ScoringCriteria scoringCriteria, Path ndxDir, int maxDistance, int phraseSlop) {
	log.debug("Creating");
	this.ndxDir = ndxDir;
	this.maxDistance = maxDistance;
	this.phraseSlop = phraseSlop;
	this.scoringCriteria = scoringCriteria;
    }

    @Override
    public List<MutationStep> evaluate(List<MutationStep> generationList) {
	return Arrays.asList(evaluate(generationList.toArray(new MutationStep[0])));
    }

    private MutationStep evaluate(MutationStep step, IndexSearcher searcher) {
	step.setFitnessScore(evaluate_multiphrase(step.getChildString(), searcher));
	return step;
    }

    public MutationStep[] evaluate(MutationStep[] generation) {
	long start = System.currentTimeMillis();
	// try (IndexReader reader =
	// DirectoryReader.open(FSDirectory.open(ndxDir));) {
	try (IndexReader reader = DirectoryReader.open(new MMapDirectory(ndxDir));) {
	    IndexSearcher searcher = new IndexSearcher(reader);
	    Arrays.stream(generation).parallel().forEach(x -> evaluate(x, searcher));
	} catch (IOException e) {
	    log.error("Unable to evaluate", e);
	}
	long stop = System.currentTimeMillis();
	log.debug("Evaluation time: {}", stop - start);
	return generation;
    }

    private double evaluate_multiphrase(String str, IndexSearcher searcher) {
	double score = 0.0;

	List<String> wordList = getWordList(str);
	if (log.isTraceEnabled()) {
	    log.trace("Phrase {} has words {}", str, Joiner.on(',').join(wordList));
	}

	MultiPhraseQuery.Builder queryBuilder = new MultiPhraseQuery.Builder();
	for (String word : wordList) {
	    word = word.toLowerCase();
	    queryBuilder.add(new Term[] { new Term(CONTENTS, word) });
	}
	queryBuilder.setSlop(phraseSlop);
	try {
	    Query query = queryBuilder.build();
	    log.info("Query: {}", query);
	    log.info(query.rewrite(searcher.getIndexReader()).toString());
	    if (log.isTraceEnabled()) {
		log.trace(query.rewrite(searcher.getIndexReader()).toString());
	    }
	    TopDocs results = searcher.search(query, 5);
	    if (ScoringCriteria.BEST_SCORE.equals(scoringCriteria)) {
		score = results.getMaxScore();
	    } else {
		score = results.totalHits;
	    }
	} catch (IOException e) {
	    log.error("Could not read index {}", str, e);
	    throw new RuntimeException(e);
	}
	return score;
    }

    private List<String> getWordList(String str) {
	ArrayList<String> wordList = new ArrayList<>();
	BreakIterator iter = BreakIterator.getWordInstance(Locale.US);
	iter.setText(str);
	int startPos = iter.first();
	int endPos = iter.next();
	do {
	    wordList.add(str.substring(startPos, endPos));
	    startPos = endPos;
	    endPos = iter.next();
	} while (endPos != BreakIterator.DONE);
	return wordList.stream().filter(s -> !Strings.nullToEmpty(s).trim().isEmpty()).collect(Collectors.toList());
    }

    @Override
    public String description() {
	return "MultiPhraseEvaluator [maxDistance=" + maxDistance + ", ndxDir=" + ndxDir + ", phraseSlop=" + phraseSlop
		+ ", scoringCriteria=" + scoringCriteria + "]";
    }

}
