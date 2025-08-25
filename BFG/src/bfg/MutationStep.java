package bfg;

import java.util.Comparator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeMap;

public class MutationStep {
	private String childString;
	private Map<String, Double> fitnessScores = new TreeMap<>();
	private String parentString;

    public MutationStep() {

    }

	public MutationStep(MutationStep step) {
		parentString = step.getChildString();
	}

	public String getChildString() {
		return childString;
	}

	public double getFitnessScore(String targetString) {
		return fitnessScores.getOrDefault(targetString, Double.valueOf(0.0d));
	}

	public String getParentString() {
		return parentString;
	}

	public void setChildString(String childString) {
		this.childString = childString;
	}

	public void setFitnessScore(String targetString, double fitnessScore) {
		this.fitnessScores.put(targetString, Double.valueOf(fitnessScore));
	}

	public void setParentString(String parentString) {
		this.parentString = parentString;
	}

	public String getConvergingToString() {
		 return fitnessScores.entrySet().stream().max(
				Map.Entry.comparingByValue(Double::compare)
				).get().getKey();
	}
	
	public double getConvergingToFitness() {
		return getFitnessScore(getConvergingToString());
	}

}
