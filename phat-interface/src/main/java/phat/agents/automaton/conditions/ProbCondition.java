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

public class ProbCondition implements AutomatonCondition {

    float prob;
    float value = -1f;
    
    public ProbCondition(float prob) {
        super();
        this.prob = prob;
    }

    private float getRandomValue(Agent agent) {
        if(value == -1f) {
            this.value = agent.getAgentsAppState().getPHAInterface().getRandom().nextFloat();
        }
        return value;
    }
    
    @Override
    public boolean evaluate(Agent agent) {
        return getRandomValue(agent) <= prob;
    }
}
