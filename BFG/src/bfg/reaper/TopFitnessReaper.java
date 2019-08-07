package bfg.reaper;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import bfg.MutationStep;

public class TopFitnessReaper implements Reaper {
    private class FitnessScoreComparator implements Comparator<MutationStep> {
	@Override
	public int compare(MutationStep o1, MutationStep o2) {
	    return -Double.compare(o1.getFitnessScore(), o2.getFitnessScore());
	}
    }

    private final int maxSize;

    public TopFitnessReaper(int maxSize) {
	this.maxSize = maxSize;
    }

    @Override
    public List<MutationStep> reap(List<MutationStep> steps) {
	return reapAsStream(steps);
    }

    private List<MutationStep> reapAsStream(List<MutationStep> steps) {
	return steps.parallelStream().sorted(new FitnessScoreComparator()).limit(maxSize).collect(Collectors.toList());
    }

    @Override
    public String description() {
	return "Top scores reaper keeping " + maxSize + " elements";
    }

}
