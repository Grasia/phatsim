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

/**
 * Interface for evaluating transitions
 *
 * @author escalope
 *
 */
public abstract class AutomatonCondition {

    long timestamp = 0;
    boolean evaluation = false;

    /**
     * It tells if the condition is met
     *
     * @param agent
     * @return
     */
    public boolean evaluate(Agent agent) {
        long t = agent.getAgentsAppState().getPHAInterface().getSimTime().getTimeInMillis();
        if (t != timestamp) {
            timestamp = t;
            evaluation = simpleEvaluation(agent);
        }
        return evaluation;
    }

    abstract boolean simpleEvaluation(Agent agent);

    public abstract void automatonInterrupted(Automaton automaton);

    public abstract void automatonResumed(Automaton automaton);

    public abstract void automatonReset(Automaton automaton);
}
