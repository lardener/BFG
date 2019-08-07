package bfg.reaper;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReaperFactory {
    private static final Logger log = LoggerFactory.getLogger(ReaperFactory.class);

    private static final String PROP_TYPE = "reaper";

    private static final String PROP_TF_MAXELEMENTS = "reaper.topfitness.maxelements";

    private static final String PROP_RAND_MAXELEMENTS = "reaper.random.maxelements";

    private static final String PROP_DISTRIB_MAXELEMENTS = "reaper.fitnessdistribution.maxelements";

    public static Reaper getInstance(Properties props) {
	Reaper reaper;
	String reaperType = props.getProperty(PROP_TYPE);
	if ("TopFitness".equals(reaperType)) {
	    reaper = new TopFitnessReaper(Integer.parseInt(props.getProperty(PROP_TF_MAXELEMENTS), 10));
	} else if ("Random".equals(reaperType)) {
	    reaper = new RandomReaper(Integer.parseInt(props.getProperty(PROP_RAND_MAXELEMENTS), 10));
	} else if ("FitnessDistribution".equals(reaperType)) {
	    reaper = new FitnessDistributionReaper(Integer.parseInt(props.getProperty(PROP_DISTRIB_MAXELEMENTS), 10));
	} else {
	    throw new RuntimeException("Unknown reaper type " + reaperType);
	}
	return reaper;
    }
}
