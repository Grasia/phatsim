package phat.agents.events;

import phat.agents.Agent;
import phat.agents.automaton.Automaton;

/**
 *
 * @author pablo
 */
public abstract class PHATEvent {
    public enum State {Started, Ignored, Assigned, Fail, Success};
    
    String id;
    EventSource eventSource; 
    State state = State.Started;
    
    abstract public boolean isPerceptible(Agent agent);
    
    public PHATEvent(String id, EventSource eventSource) {
        this.id = id;
        this.eventSource = eventSource;
    }

    public EventSource getEventSource() {
        return eventSource;
    }
    
    public String getId() {
        return id;
    }
    
    public void setEventState(State state) {
    	this.state = state;
    }
}
