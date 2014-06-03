/*
 * Copyright (C) 2014 Pablo Campillo-Sanchez <pabcampi@ucm.es>
 *
 * This software has been developed as part of the 
 * SociAAL project directed by Jorge J. Gomez Sanz
 * (http://grasia.fdi.ucm.es/sociaal)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
