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
package phat.agents;

import com.jme3.math.Vector3f;

import java.util.ArrayList;
import java.util.List;

import phat.PHATInterface;
import phat.agents.automaton.Automaton;
import phat.agents.events.PHATEventManager;
import phat.commands.PHATCommand;
import phat.world.PHATCalendar;

public abstract class Agent implements PHATAgentTick {

    protected Automaton automaton;
    boolean init = false;
    AgentsAppState agentsAppState;
    String bodyId;
    List<AgentListener> listeners = new ArrayList<AgentListener>();
    PHATEventManager eventManager;

    abstract protected void initAutomaton();
    public abstract void agentUpdate(PHATInterface phatInterface);
    public abstract boolean isInTheWorld();
    public abstract boolean isInAHouse(String idHouse);
    public abstract void runCommand(PHATCommand command);
    public abstract Vector3f getLocation();
    
    protected void notifyAgentListener() {
        for (AgentListener al : listeners) {
            al.agentChanged(this);
        }
    }

    public void addListener(AgentListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public String getId() {
        return bodyId;
    }

    public Agent(String bodyId) {
        this.bodyId = bodyId;
        eventManager = new PHATEventManager(this);
    }

    public String getCurrentActionName() {
        if (automaton != null) {
            return automaton.getCurrentActionName();
        }
        return "";
    }
    
    public Automaton getCurrentAction() {
        if (automaton != null) {
            return automaton.getCurrentAction();
        }
        return null;
    }

    public Automaton getAutomaton() {
        return automaton;
    }

    public void setAutomaton(Automaton automaton) {
        this.automaton = automaton;
        notifyAgentListener();
    }

    @Override
    public void update(PHATInterface phatInterface) {
        if (!init) {
            initAutomaton();
            init = true;
        }
        
        if (eventManager.areEvents()) {
            eventManager.process(phatInterface);
        }
        
        if (automaton != null) {
            automaton.nextState(phatInterface);
            /*if (automaton.isIdle()) {
             initAutomaton();
             }*/
        } else {
            initAutomaton();
        }
    }

    public PHATCalendar getTime() {
        return agentsAppState.getBodiesAppState().getTime();
    }

    public long getElapsedTimeSeconds() {
        return agentsAppState.getPHAInterface().getElapsedSimTimeSeconds();
    }

    public void setAgentsAppState(AgentsAppState agentsAppState) {
        this.agentsAppState = agentsAppState;
    }

    public AgentsAppState getAgentsAppState() {
        return agentsAppState;
    }
    
    public PHATEventManager getEventManager() {
        return eventManager;
    }
}
