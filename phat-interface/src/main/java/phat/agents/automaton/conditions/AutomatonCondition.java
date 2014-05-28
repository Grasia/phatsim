package phat.agents.automaton.conditions;

import phat.agents.Agent;

/**
 * Interface for evaluating transitions
 * 
 * @author escalope
 *
 */
public interface AutomatonCondition {

	/**
	 * It tells if the condition is met
	 * @return
	 */
	boolean evaluate(Agent agent);
}
