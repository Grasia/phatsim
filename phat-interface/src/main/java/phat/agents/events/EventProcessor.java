package phat.agents.events;

import phat.agents.Agent;
import phat.agents.automaton.Automaton;
import phat.agents.automaton.conditions.AutomatonCondition;

public class EventProcessor {
	String eventId;
	AutomatonCondition condition;
	Automaton activity;
	
	public EventProcessor(String eventId, AutomatonCondition condition,
			Automaton activity) {
		super();
		this.eventId = eventId;
		this.condition = condition;
		this.activity = activity;
	}
	
	public Automaton process(Agent agent) {
		if(condition == null || condition.evaluate(agent)) {
			return activity;
		}
		return null;
	}

	public String getEventId() {
		return eventId;
	}
}
