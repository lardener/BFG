package bfg;

import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.MoreObjects;

public class StatCollector {
    private static final Logger log = LoggerFactory.getLogger(StatCollector.class);

    private boolean equivalent(double d1, double d2, double delta) {
	double diff = Math.abs(d1 - d2);
	return diff <= delta;
    }

    public void logStatistics(List<MutationStep> generation) {
	logStatisticsParallel(generation);
    }

    public void logStatisticsParallel(List<MutationStep> generation) {
	DoubleSummaryStatistics stats = generation.parallelStream()
		.collect(Collectors.summarizingDouble(MutationStep::getFitnessScore));
	log.info("Total elements: {}; Fitness stats: [{}, {}, {}]", stats.getCount(), stats.getMin(),
		stats.getAverage(), stats.getMax());
	double maxVal = stats.getMax();
	double minVal = stats.getMin();
	MutationStep maxElem = null;
	MutationStep minElem = null;
	for (MutationStep element : generation) {
	    double thisScore = element.getFitnessScore();
	    if (equivalent(maxVal, thisScore, 0.001)) {
		maxElem = element;
	    } else if (equivalent(minVal, thisScore, 0.001)) {
		minElem = element;
	    }
	}
	minElem = MoreObjects.firstNonNull(minElem, new MutationStep());
	maxElem = MoreObjects.firstNonNull(maxElem, new MutationStep());
	log.info("Lowest fitness : {} ==> {}", minElem.getChildString(), minElem.getConvergingTo());
	log.info("Highest fitness: {} ==> {}", maxElem.getChildString(), maxElem.getConvergingTo());
    }

    public void logStatisticsSerial(List<MutationStep> generation) {
	int total = generation.size();
	double highFitness = Double.MIN_VALUE;
	double lowFitness = Double.MAX_VALUE;
	double fitnessSum = 0.0;
	double fitnessAvg;

	for (MutationStep step : generation) {
	    double currentFitness = step.getFitnessScore();
	    highFitness = Double.max(currentFitness, highFitness);
	    lowFitness = Double.min(currentFitness, lowFitness);
	    fitnessSum += currentFitness;
	}
	fitnessAvg = fitnessSum / total;
	log.info("Total elements: {}; Fitness stats: [{}, {}, {}]", total, lowFitness, fitnessAvg, highFitness);
    }
}
