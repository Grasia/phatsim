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
package phat.agents.automaton.conditions;

import java.util.logging.Level;
import java.util.logging.Logger;
import phat.agents.Agent;
import phat.agents.automaton.Automaton;
import phat.agents.events.PHATEvent;
import phat.agents.events.PHATEventManager;

public class EventCondition implements AutomatonCondition {

    private final static Logger logger = Logger.getLogger(EventCondition.class.getName()); 

    String idEvent;

    public EventCondition(String idEvent) {
        this.idEvent = idEvent;
    }

    /**
     * Return true if the agent has a symptom with the name symptomName 
     * and the same level of symptomLevel
     * or if the agent doesn't have the symptome and symptomLeve == "NONE",
     * in another case it returns false;
     * 
     * @param agent
     * @return 
     */
    @Override
    public boolean evaluate(Agent agent) {
        PHATEventManager em = agent.getEventManager();
        if (em != null) {
            PHATEvent event = em.getEvent(idEvent);
            if (event != null) {
                return true;
            }
        } else {
            logger.log(Level.WARNING, "Agent {0} hasn't got EventManager!", new Object[]{agent.getId()});
        }
        return false;
    }

    @Override
    public void automatonInterrupted(Automaton automaton) {
    }

    @Override
    public void automatonResumed(Automaton automaton) {
    }
    
    @Override
    public void automatonReset(Automaton automaton) {
    }
}
