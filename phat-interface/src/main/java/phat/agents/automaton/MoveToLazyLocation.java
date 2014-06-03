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

import com.jme3.math.Vector3f;

import phat.PHATInterface;
import phat.agents.Agent;
import phat.body.commands.GoToCommand;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.util.Lazy;

/**
 *
 * @author pablo
 */
public class MoveToLazyLocation extends SimpleState implements PHATCommandListener{
    GoToCommand goToCommand;
    boolean targetReached = false;
    
    public MoveToLazyLocation( Agent agent, int priority, String name, 
    		Lazy<Vector3f> destiny) {
        this(agent, priority, name, destiny, 1f);
    }
    
    public MoveToLazyLocation( Agent agent, int priority, String name, Lazy<Vector3f> destiny, float distance) {
        super(agent, priority, name);
        goToCommand = new GoToCommand(agent.getId(), destiny, this);
    }
        
    @Override
    public boolean isFinished(PHATInterface phatInterface) {
        return targetReached;
    }
    
    @Override
    public void commandStateChanged(PHATCommand command) {
        if (command == goToCommand
                && command.getState().equals(PHATCommand.State.Success)) {
            targetReached = true;
        }
    }

    @Override
    public void simpleNextState(PHATInterface phatInterface) {
        
    }

    @Override
    public void initState(PHATInterface phatInterface) {
        targetReached = false;
        agent.runCommand(goToCommand);
    }
}
