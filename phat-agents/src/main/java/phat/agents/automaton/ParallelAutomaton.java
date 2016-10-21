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

import phat.PHATInterface;
import phat.agents.Agent;
import phat.agents.automaton.conditions.BooleanCondition;

/**
 *
 * @author pablo
 */
public class ParallelAutomaton extends Automaton {

    Automaton defaultAutomaton = null;

    public ParallelAutomaton(Agent agent) {
        super(agent);
    }

    public ParallelAutomaton(Agent agent, String name) {
        super(agent, 0, name);
    }

    @Override
    public boolean isFinished(PHATInterface phatInterface) {
        return (finishCondition != null && finishCondition.evaluate(agent))
                || (haveFinisedAll(phatInterface));
    }

    private boolean haveFinisedAll(PHATInterface phatInterface) {
        for (Automaton a : pendingTransitions) {
            if (!a.isFinished(phatInterface)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void nextState(PHATInterface phatInterface) {
        switch (state) {
            case NOT_INIT:
                initState(phatInterface);
                setState(STATE.STARTED);
                break;
            case DEFAULT:
                defaultAutomaton = getDefaultState(phatInterface);
                if (defaultAutomaton == null) {
                    setState(STATE.FINISHED);
                } else {
                    defaultAutomaton.setAutomatonModificator(automatonModificator);
                    for (AutomatonListener al : listeners) {
                        defaultAutomaton.addListener(al);
                    }
                    setState(STATE.STARTED);
                }
                break;
            case STARTED:
                if (defaultAutomaton != null) {
                    defaultAutomaton.nextState(phatInterface);
                } else {
                    run(phatInterface);
                }
                break;
            case FINISHED:
                break;
            case INTERRUPTED:
                if (finishCondition != null) {
                    finishCondition.automatonInterrupted(this);
                }
                if (currentState != null) {
                    currentState.setState(STATE.INTERRUPTED);
                    currentState.nextState(phatInterface);
                }
                interrupt(phatInterface);
                break;
            case RESUMED:
                if (finishCondition != null) {
                    finishCondition.automatonResumed(this);
                }
                if (currentState != null) {
                    currentState.setState(STATE.RESUMED);
                    currentState.nextState(phatInterface);
                }
                resume(phatInterface);
                setState(STATE.STARTED);
                break;
        }
    }

    @Override
    protected void run(PHATInterface phatInterface) {
        if (isFinished(phatInterface)) {
            setState(STATE.FINISHED);
        } else {
            for (Automaton a : pendingTransitions) {
                if (!a.isFinished(phatInterface)) {
                    a.nextState(phatInterface);
                    transmitListeners(a);
                }
            }
            // There is one state with higher priority?
            /*if (isPossibleAttendAHigherPriorityState()) {
                currentState.setState(STATE.INTERRUPTED);
                currentState.nextState(phatInterface); // Transmit the interruption
                addTransition(currentState, true);
                currentState = getNextAutomaton();
            } else if (currentState.getState() == STATE.FINISHED) {
                currentState = getNextAutomaton();
            } else {
                if (automatonModificator != null) {
                    Automaton last = currentState;
                    currentState = automatonModificator.monitoring(currentState);
                    if (last != currentState) {
                        transmitListeners(currentState);
                    }
                }
                currentState.nextState(phatInterface);
            }*/
        }
    }

    @Override
    public void initState(PHATInterface phatInterface) {
    }

    @Override
    public Automaton getDefaultState(PHATInterface phatInterface) {
        // Do Nothing forever
        return new DoNothing(agent, name).setFinishCondition(new BooleanCondition(false));
    }
}
