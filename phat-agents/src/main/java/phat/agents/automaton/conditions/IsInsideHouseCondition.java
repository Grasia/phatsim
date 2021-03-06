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

import phat.agents.Agent;
import phat.agents.automaton.Automaton;

public class IsInsideHouseCondition extends AutomatonCondition {
    String bodyId;
    
    public IsInsideHouseCondition() {
        super();
    }

    public IsInsideHouseCondition(String bodyId) {
        this();
        this.bodyId = bodyId;
    }
    
    @Override
    public boolean simpleEvaluation(Agent agent) {
        if(bodyId != null) {
            return agent.isInAHouse(bodyId);
        } else {
            return agent.isInAHouse(agent.getId());
        }
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

    @AutoCondParam(name = "bodyId")
    public String getBodyId() {
        return bodyId;
    }
}
