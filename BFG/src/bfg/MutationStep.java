package bfg;

public class MutationStep {
    private String childString;
    private double fitnessScore;
    private String parentString;
    private String convergingTo;

    public MutationStep() {

    }

    public MutationStep(MutationStep step) {
	parentString = step.getChildString();
    }

    public String getChildString() {
	return childString;
    }

    public double getFitnessScore() {
	return fitnessScore;
    }

    public String getParentString() {
	return parentString;
    }

    public void setChildString(String childString) {
	this.childString = childString;
    }

    public void setFitnessScore(double fitnessScore) {
	this.fitnessScore = fitnessScore;
    }

    public void setParentString(String parentString) {
	this.parentString = parentString;
    }

    public String getConvergingTo() {
	return convergingTo;
    }

    public void setConvergingTo(String convergingTo) {
	this.convergingTo = convergingTo;
    }
}
