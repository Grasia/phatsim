package phat.agents.automaton.conditions;

import phat.agents.Agent;

public class NegateCondition implements AutomatonCondition {
	AutomatonCondition condition;
	
	public NegateCondition(AutomatonCondition ac) {
		this.condition = ac;
	}
	
	@Override
	public boolean evaluate(Agent agent) {
		return !condition.evaluate(agent);
	}

}
