package bfg.corrector;

import java.nio.file.Paths;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CorrectorFactory {
    private static final Logger log = LoggerFactory.getLogger(CorrectorFactory.class);

    private static final String PROP_TYPENAME = "corrector";

    private static final String PROP_SC_DICTIONARY_FILE = "corrector.spellcheck.dictionaryFile";
    private static final String PROP_SC_DICTIONARY_NDX = "corrector.spellcheck.dictionaryIndex";
    private static final String PROP_SC_SUGGESTIONS = "corrector.spellcheck.suggestions";
    private static final String PROP_SC_SUGGEST_SIMILARITY = "corrector.spellcheck.suggestion.similarity";

    public static Corrector getInstance(Properties props) {
	String type = props.getProperty(PROP_TYPENAME);
	Corrector corrector;
	if ("Spellcheck".equals(type)) {
	    corrector = new SpellcheckCorrector(Integer.parseInt(props.getProperty(PROP_SC_SUGGESTIONS), 10),
		    Float.parseFloat(props.getProperty(PROP_SC_SUGGEST_SIMILARITY)),
		    Paths.get(props.getProperty(PROP_SC_DICTIONARY_FILE)),
		    Paths.get(props.getProperty(PROP_SC_DICTIONARY_NDX)));
	} else if ("Null".equals(type)) {
	    corrector = new NullCorrector();
	} else {
	    throw new RuntimeException("Unknown evaluator type");
	}
	return corrector;
    }
}
