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
package phat.agents.automaton.devices;

import java.util.ArrayList;
import java.util.logging.Logger;
import phat.PHATInterface;
import phat.agents.Agent;
import phat.agents.automaton.Automaton;
import phat.agents.automaton.FSM;
import phat.agents.automaton.Transition;

/**
 *
 * @author pablo
 */
public abstract class FSMProgramBehavior extends FSM {

    private final static Logger logger = Logger.getLogger(FSMProgramBehavior.class.getName());

    public FSMProgramBehavior(Agent agent) {
        super(agent);
    }
    
    public FSMProgramBehavior(Agent agent, String name) {
        super(agent, 0, name);
    }

    @Override
    public void initState(PHATInterface phatInterface) {
        initProgramStates(phatInterface);

    }

    protected abstract void initProgramStates(PHATInterface phatInterface);
    
    @Override
    public void nextState(PHATInterface phatInterface) {
        if (currentState != null &&
                currentState.getState().equals(Automaton.STATE.DEFAULT_STARTED) && 
                areNextStatesAvailable(currentState)) {  
            currentState.setState(STATE.FINISHED);
        }
        super.nextState(phatInterface);
    }
    
    /**
     * Return true if there are transitions available as true.
     *
     * @param source
     * @return
     */
    private boolean areNextStatesAvailable(Automaton source) {
        if(source == null) {
            return false;
        }
        ArrayList<Transition> r = possibleTransitions.get(source);
        if (r != null) {
            for (Transition t : r) {
                if (t.evaluate()) {
                    return true;
                }
            }
        }
        return false;
    }
}
