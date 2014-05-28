package phat.agents.automaton.conditions;

import phat.agents.Agent;


public class EmptyCondition implements AutomatonCondition {
	@Override
	public boolean evaluate(Agent agent) {
		return true;
	}

}
