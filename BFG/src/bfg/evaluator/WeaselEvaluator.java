package bfg.evaluator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.ImmutableList;

import bfg.MutationStep;
import bfg.misc.StringSanitizer;

public class WeaselEvaluator implements Evaluator {
    private final StringSimilarityScorer scorer;
    private final List<String> targetStrings;

    public WeaselEvaluator(StringSimilarityScorer similarityScorer, List<String> targetStringList) {
	this.scorer = similarityScorer;
	StringSanitizer sanitizer = StringSanitizer.getInstance();
	this.targetStrings = targetStringList.parallelStream().map(sanitizer::sanitize)
		.collect(ImmutableList.toImmutableList());
    }

    @Override
    public List<MutationStep> evaluate(List<MutationStep> generationList) {
	generationList.parallelStream().forEach(x -> evaluate(x, this.scorer, this.targetStrings));
	return generationList;
    }

    private void evaluate(MutationStep step, StringSimilarityScorer scorer, List<String> targets) {
	class TargetScore {
	    double score;
	    String target;

	    TargetScore() {
	    }

	    TargetScore(String target) {
		this();
		this.target = target;
	    }
	}
	final String toEvaluate = step.getChildString();
	List<TargetScore> targetScores = new ArrayList<>(targets.size());
	for (String t : targets) {
	    targetScores.add(new TargetScore(t));
	}
	targetScores.parallelStream().forEach(ts -> ts.score = scorer.similarity(ts.target, toEvaluate));
	Optional<TargetScore> bestTargetScoreOpt = targetScores.parallelStream()
		.max((a, b) -> Double.compare(a.score, b.score));
	TargetScore bestTargetScore = bestTargetScoreOpt.orElseThrow(IllegalStateException::new);
	step.setFitnessScore(bestTargetScore.score);
	step.setConvergingTo(bestTargetScore.target);
    }

    @Override
    public String description() {
	return "Weasel evaluator; " + targetStrings.size() + " targets using " + scorer.description();
    }

    public List<String> getTargets() {
	return ImmutableList.copyOf(this.targetStrings);
    }
}
