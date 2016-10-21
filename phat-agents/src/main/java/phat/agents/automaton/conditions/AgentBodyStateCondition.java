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
import phat.agents.HumanAgent;
import phat.agents.automaton.Automaton;
public class AgentBodyStateCondition extends AutomatonCondition {

    private final static Logger logger = Logger.getLogger(AgentBodyStateCondition.class.getName());

    String bodyId;
    String bodyState;

    public AgentBodyStateCondition(String bodyState) {
        this.bodyState = bodyState;
    }
    
    public AgentBodyStateCondition(String bodyId, String bodyState) {
        this.bodyId = bodyId;
        this.bodyState = bodyState;
    }

    /**
     * Return true if the agent has the posture indicated in the atribut of the class,
     * in another case it returns false;
     *
     * @param currentAgent
     * @return
     */
    @Override
    public boolean simpleEvaluation(Agent currentAgent) {
        Agent agent = currentAgent;
        if (bodyId != null) {
            agent = agent.getAgentsAppState().getAgent(bodyId);
            if(agent == null) {
                logger.log(Level.SEVERE, "Body {0} does not exists!", new Object[]{bodyId});
                return false;
            }
        }
        if(agent instanceof HumanAgent) {
            return ((HumanAgent)agent).getBodyPosture().name().equals(bodyState);
        }
        return false;
    }

    @Override
    public void automatonReset(Automaton automaton) {
    }

    @Override
    public void automatonInterrupted(Automaton automaton) {
    }

    @Override
    public void automatonResumed(Automaton automaton) {
    }

    @AutoCondParam(name = "bodyId")
    public String getBodyId() {
        return bodyId;
    }

    @AutoCondParam(name = "bodyState")
    public String getBodyState() {
        return bodyState;
    }

    @Override
    public String toString() {
        return "SymptomCondition(" + bodyId + "," + bodyState + ")";
    }
}
