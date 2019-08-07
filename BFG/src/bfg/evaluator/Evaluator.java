package bfg.evaluator;

import java.util.List;

import bfg.Describable;
import bfg.MutationStep;

public interface Evaluator extends Describable {

    List<MutationStep> evaluate(List<MutationStep> generationList);

}
