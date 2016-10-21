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
package phat.agents.filters;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import phat.PHATInterface;
import phat.agents.Agent;
import phat.agents.automaton.Automaton;
import phat.agents.automaton.FSM;
import phat.agents.automaton.Transition;
import phat.agents.automaton.conditions.SymptomCondition;

/**
 *
 * @author pablo
 */
public abstract class FSMSymptomEvolution extends FSM implements SymptomEvolution {

    private final static Logger logger = Logger.getLogger(SymptomCondition.class.getName());
    Symptom symptom;

    public FSMSymptomEvolution(Agent agent, Symptom symptom) {
        super(agent);
        this.symptom = symptom;
    }

    @Override
    public void nextState(PHATInterface phatInterface) {
        if (currentState != null && areNextStatesAvailable(currentState)) {
            currentState.setState(STATE.FINISHED);
        }
        super.nextState(phatInterface);
    }

    @Override
    public void initState(PHATInterface phatInterface) {
        initSymptomEvolutionBehavior(phatInterface);

        chooseAnInitialState(phatInterface);
        logger.log(Level.SEVERE, "Initial State for level {0} of symptom {1} is {2}",
                new Object[]{symptom.getSymptomType(), symptom.getCurrentLevel().name(), initialState});
    }

    protected abstract void initSymptomEvolutionBehavior(PHATInterface phatInterface);

    protected void chooseAnInitialState(PHATInterface phatInterface) {
        System.out.println("Choosing an initial state for "+symptom.getSymptomType());
        List<SymptomState> states = getStateForSymptomLevel(symptom.getCurrentLevel());
        if (!states.isEmpty()) {
            //int index = phatInterface.getRandom().nextInt(states.size());
            //registerStartState(states.get(index));
            // Choose one whose evolution may keep at the same level of the symptom
            registerStartState(getFirstState(states));
        } else {
            logger.log(Level.SEVERE, "No States for level {0} of symptom {1}",
                    new Object[]{symptom.getSymptomType(), symptom.getCurrentLevel().name()});
        }
    }

    private SymptomState getFirstState(List<SymptomState> states) {
        SymptomState ss = null;
        int count = 0;
        
        for (SymptomState state : states) {
            System.out.println("\tState = "+state.getName());
            int c = getNumberNodesAtSameLevel(state);
            System.out.println("\tc = "+c);
            if(c > count) {
                count = c;
                ss = state;
            }
        }

        return ss;
    }

    private int getNumberNodesAtSameLevel(SymptomState state) {
        int result = 0;
        for (Transition t : possibleTransitions.get(state)) {
            System.out.println("\tTransitions("+state.getName()+"): "+t.getTarget().getName());
            if (t.getTarget() instanceof SymptomState) {
                SymptomState target = (SymptomState) t.getTarget();
                System.out.println("\tLevel = "+target.getLevel());
                if (target.getLevel().equals(state.getLevel())) {
                    return 1 + getNumberNodesAtSameLevel(target);
                }
            }
        }
        return result;
    }

    public List<SymptomState> getStateForSymptomLevel(Symptom.Level level) {
        List<SymptomState> result = new ArrayList<>();
        for (Automaton a1 : possibleTransitions.keySet()) {
            SymptomState sourceState = (SymptomState) a1;
            if (sourceState.getLevel().equals(level)
                    && !result.contains(sourceState)) {
                result.add((SymptomState) sourceState);
            }
            for (Transition a2 : possibleTransitions.get(sourceState)) {
                SymptomState targetState = (SymptomState) a2.getTarget();
                if (targetState.getLevel().equals(level)
                        && !result.contains(targetState)) {
                    result.add((SymptomState) targetState);
                }
            }
        }
        return result;
    }

    @Override
    public Symptom updateSymptom(PHATInterface phat) {
        nextState(phat);
        return symptom;
    }

    @Override
    public Symptom getSymptom() {
        return symptom;
    }

    /**
     * Return true if there are transitions available as true.
     *
     * @param source
     * @return
     */
    private boolean areNextStatesAvailable(Automaton source) {
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
