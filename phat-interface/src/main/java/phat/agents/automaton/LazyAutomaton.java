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
import phat.body.commands.StandUpCommand;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.util.Lazy;

/**
 *
 * @author pablo
 */
public class LazyAutomaton extends SimpleState {
    Lazy<Automaton> lazyAutomaton;
    boolean finished;
    
    public LazyAutomaton( Agent agent, Lazy<Automaton> lazyAutomaton) {
        super(agent, 0, "LazyAutomaton");
        this.lazyAutomaton = lazyAutomaton;
    }
    
    @Override
    public boolean isFinished(PHATInterface phatInterface) {
        return lazyAutomaton.getLazy().isFinished(phatInterface);
    }

    @Override
    public void simpleNextState(PHATInterface phatInterface) {
        lazyAutomaton.getLazy().nextState(phatInterface);
    }

    @Override
    public void initState(PHATInterface phatInterface) {
        lazyAutomaton.getLazy().initState(phatInterface);
    }
}
