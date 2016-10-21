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

import java.util.LinkedList;
import phat.PHATInterface;
import phat.agents.Agent;
import phat.agents.automaton.Automaton;
import phat.agents.automaton.DoNothing;

/**
 *
 * @author pablo
 */
public class ProgState extends Automaton {

    protected LinkedList<Automaton> actionsFinished = new LinkedList<>();
    
    public ProgState(Agent agent, String name) {
        super(agent, 0, name);
    }

    @Override
    public void initState(PHATInterface phatInterface) {
        if(!actionsFinished.isEmpty()) {
            pendingTransitions.addAll(actionsFinished);
            actionsFinished.clear();
            
            for(Automaton a: pendingTransitions) {
                a.setState(STATE.NOT_INIT);
            }
        }
    }

    @Override
    public Automaton getDefaultState(PHATInterface phatInterface) {
        return new DoNothing(agent, "WaitingProgState");
    }
    
    @Override
    protected Automaton getNextAutomaton() {
        Automaton newTransition = null;
        if (!pendingTransitions.isEmpty()) {
            newTransition = pendingTransitions.getFirst();
            pendingTransitions.removeFirst();
            actionsFinished.add(newTransition);

            transmitListeners(newTransition);
        }
        return newTransition;
    }
}