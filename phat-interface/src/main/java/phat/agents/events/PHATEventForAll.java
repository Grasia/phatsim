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
import phat.agents.automaton.AutomatonListener;

public class PHATEventForAll extends PHATEvent implements AutomatonListener {

    Agent agent;

    public PHATEventForAll(Agent agent, String id, EventSource eventSource) {
        super(id, eventSource);
        this.agent = agent;
    }

    @Override
    public void automatonFinished(Automaton automaton, boolean isSuccessful) {
        if (isSuccessful) {
            agent.getAgentsAppState().add(this);
        }
    }

    @Override
    public void nextAutomaton(Automaton previousAutomaton, Automaton nextAutomaton) {
    }

    @Override
    public void preInit(Automaton automaton) {
        
    }

    @Override
    public void postInit(Automaton automaton) {
        
    }

    @Override
    public void automatonInterrupted(Automaton automaton) {
        
    }

    @Override
    public void automatonResumed(Automaton resumedAutomaton) {
        
    }

    @Override
    public boolean isPerceptible(Agent agent) {
        return true;
    }
}
