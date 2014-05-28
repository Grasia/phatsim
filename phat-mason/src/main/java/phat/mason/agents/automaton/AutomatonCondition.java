package phat.mason.agents.automaton;
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
	boolean evaluate();
}
