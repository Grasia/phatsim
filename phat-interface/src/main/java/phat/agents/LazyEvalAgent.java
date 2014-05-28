package phat.agents;

import java.util.ArrayList;

import phat.PHATInterface;
import phat.agents.automaton.Automaton;

/**
 * A lazy evaluation of an agent to be determined in runtime. It is useful to parameterize
 * operations over an agent which is not known yet
 * 
 * @author escalope
 *
 */
public abstract class LazyEvalAgent extends Agent {

	public LazyEvalAgent() {
		super(null);	
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public abstract Agent getAgent();
	@Override
	public void update(PHATInterface phatInterface) {
		getAgent().update(phatInterface);
	}

	@Override
	public String getId() {
		return getAgent().getId();
	}
	
	@Override
	public String getCurrentAction() {
		return getAgent().getCurrentAction();
	}

	@Override
	public Automaton getAutomaton() {
		return getAgent().getAutomaton();
	}

	@Override
	public void setAutomaton(Automaton automaton) {
		getAgent().setAutomaton(automaton);
	}
}
