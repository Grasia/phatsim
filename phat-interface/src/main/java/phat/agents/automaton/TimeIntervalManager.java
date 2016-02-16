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
package phat.agents.automaton;

import java.util.ArrayList;
import phat.PHATInterface;
import phat.agents.Agent;
import phat.agents.automaton.conditions.PastTimeCondition;

/**
 *
 * @author pablo
 */
public abstract class TimeIntervalManager extends FSM {

    public TimeIntervalManager(Agent agent, String name) {
        this(agent, 0, name);
    }
    
    public TimeIntervalManager(Agent agent, int priority, String name) {
        super(agent, priority, name);
        initTIs();
    }

    @Override
    public void nextState(PHATInterface phatInterface) {
        if (currentState != null && areNextStatesAvailable(currentState)) {
            System.out.println("Set finish!!!");
            currentState.setState(STATE.FINISHED);
        }
        super.nextState(phatInterface);
    }

    /**
     * Return true if there are transitions with the PastTimeCondition evaluated
     * as true.
     *
     * @param source
     * @return
     */
    private boolean areNextStatesAvailable(Automaton source) {
        ArrayList<Transition> r = possibleTransitions.get(source);
        if (r != null) {
            for (Transition t : r) {
                if (t.getCondition() instanceof PastTimeCondition) {
                    if (t.evaluate()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    protected abstract void initTIs();
}
