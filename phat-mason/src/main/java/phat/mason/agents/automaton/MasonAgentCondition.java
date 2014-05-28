package phat.mason.agents.automaton;

import phat.mason.agents.Agent;

public abstract class MasonAgentCondition implements AutomatonCondition {

	private Agent masonAgent=null;
	public MasonAgentCondition(Agent agent){
		this.masonAgent=agent;
	}
	
	public Agent getAgent(){return masonAgent;};
	
	@Override
	public abstract boolean evaluate() ;

}
