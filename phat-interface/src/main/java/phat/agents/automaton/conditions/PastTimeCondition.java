package phat.agents.automaton.conditions;

import phat.agents.Agent;

public class PastTimeCondition implements AutomatonCondition {
	int hours;
	int minutes;
	int seconds;
	
	public PastTimeCondition(int hours, int minutes,int seconds) {
		super();
		this.hours = hours;
		this.minutes = minutes;
		this.seconds = seconds;
	}

	@Override
	public boolean evaluate(Agent agent) {
		return agent.getTime().pastTime(hours, minutes, seconds);
	}

}
