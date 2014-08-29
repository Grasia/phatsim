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
package phat.agents.automaton.uses;

import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

import phat.PHATInterface;
import phat.agents.Agent;
import phat.agents.automaton.SimpleState;
import phat.body.commands.CloseObjectCommand;
import phat.body.commands.GoToCommand;
import phat.body.commands.OpenObjectCommand;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.util.Lazy;
import phat.util.SpatialFactory;
import phat.util.SpatialUtils;

/**
 *
 * @author pablo
 */
public class HaveAShowerAutomaton extends SimpleState implements PHATCommandListener {

    boolean haveShowerfinished;
    boolean tapClosed = false;
    boolean fail = false;
    private String showerId;
    GoToCommand goIntoShower;
    CloseObjectCommand closeObjectCommand;

    public HaveAShowerAutomaton(Agent agent, String showerId) {
        super(agent, 0, "HaveAShowerAutomaton-" + showerId);
        this.showerId = showerId;
    }

    @Override
    public boolean isFinished(PHATInterface phatInterface) {
        if (tapClosed) {
            return true;
        }
        haveShowerfinished = super.isFinished(phatInterface) || fail;
        if (haveShowerfinished) {
            closeObjectCommand = new CloseObjectCommand(agent.getId(), showerId, this);
            agent.runCommand(closeObjectCommand);
            return false;
        }
        return haveShowerfinished;
    }
    
    @Override
    public void interrupt() {
    	if(goIntoShower != null && goIntoShower.getState().equals(PHATCommand.State.Running)) {
            goIntoShower.setFunction(PHATCommand.Function.Interrupt);
            agent.runCommand(goIntoShower);
        }
        if(tapClosed == false) {
            agent.runCommand(new CloseObjectCommand(agent.getId(), showerId));
        }
        tapClosed = true;
            
    	super.interrupt();
    }
        
    @Override
    public void commandStateChanged(PHATCommand command) {
        if (command == goIntoShower
                && command.getState().equals(PHATCommand.State.Success)) {
            agent.runCommand(new OpenObjectCommand(agent.getId(), showerId));
        } else if (command == closeObjectCommand && command.getState().equals(PHATCommand.State.Success)) {
            tapClosed = true;            
        }
        if (command.getState().equals(PHATCommand.State.Fail)) {
            fail = true;
        }
    }

    @Override
    public void simpleNextState(PHATInterface phatInterface) {
    }

    @Override
    public void initState(PHATInterface phatInterface) {
        tapClosed = false;
        fail = false;
        haveAShower();
    }

    private void haveAShower() {
        goIntoShower = new GoToCommand(agent.getId(), new Lazy<Vector3f>() {
            @Override
            public Vector3f getLazy() {
                Spatial targetSpatial = SpatialUtils.getSpatialById(
                        SpatialFactory.getRootNode(), showerId);
                return targetSpatial.getWorldTranslation();
            }
        }, this);
        goIntoShower.setMinDistance(0.05f);
        agent.runCommand(goIntoShower);
    }
}
