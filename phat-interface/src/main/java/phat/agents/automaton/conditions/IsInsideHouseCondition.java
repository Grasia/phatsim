package phat.agents.automaton.conditions;

import phat.agents.Agent;

public class IsInsideHouseCondition implements AutomatonCondition {
        
	public IsInsideHouseCondition() {
		super();
	}

	@Override
	public boolean evaluate(Agent agent) {
            return agent.getBodiesAppState().isBodyInAHouse(agent.getId());
	}

}
