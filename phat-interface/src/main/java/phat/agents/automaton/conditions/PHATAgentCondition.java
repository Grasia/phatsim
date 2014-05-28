package phat.agents.automaton.conditions;

import phat.agents.Agent;


public abstract class PHATAgentCondition implements AutomatonCondition {

	private Agent masonAgent=null;
	public PHATAgentCondition(Agent agent){
		this.masonAgent=agent;
	}
	
	public Agent getAgent(){return masonAgent;};
	
	@Override
	public abstract boolean evaluate(Agent agent) ;

}
