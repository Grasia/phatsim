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
import phat.body.commands.SitDownCommand;
import phat.body.commands.StandUpCommand;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.util.Lazy;
import phat.util.SpatialFactory;
import phat.util.SpatialUtils;

/**
 *
 * @author pablo
 */
public class UseDoorbellAutomaton extends SimpleState implements PHATCommandListener {

    boolean useDoorbellfinished;
    boolean buttonPushed = false;
    boolean fail = false;
    private String doorbellId;
    GoToCommand goCloseToDoorbell;
    OpenObjectCommand useDoorbell;

    public UseDoorbellAutomaton(Agent agent, String doorbellId) {
        super(agent, 0, "UseDoorbellAutomaton-" + doorbellId);
        this.doorbellId = doorbellId;
    }

    @Override
    public boolean isFinished(PHATInterface phatInterface) {        
        useDoorbellfinished = super.isFinished(phatInterface) || fail;
        if (useDoorbellfinished) {
            buttonPushed = true;
        }
        return buttonPushed;
    }

    @Override
    public void commandStateChanged(PHATCommand command) {
        if (command == goCloseToDoorbell
                && command.getState().equals(PHATCommand.State.Success)) {
            useDoorbell = new OpenObjectCommand(agent.getId(), doorbellId, this);
            agent.runCommand(useDoorbell);
        } else if (command == useDoorbell
                && command.getState().equals(PHATCommand.State.Success)) {
            buttonPushed = true;
        }
        if (command.getState().equals(PHATCommand.State.Fail)) {
            fail = true;
        }
    }

    @Override
    public void simpleNextState(PHATInterface phatInterface) {
    }

    public void interrupt() {
    	if(goCloseToDoorbell != null && goCloseToDoorbell.getState().equals(PHATCommand.State.Running)) {
            goCloseToDoorbell.setFunction(PHATCommand.Function.Interrupt);
            agent.runCommand(goCloseToDoorbell);
        }            
    	super.interrupt();
    }
        
    @Override
    public void initState(PHATInterface phatInterface) {
        buttonPushed = false;
        fail = false;
        useWC();
    }

    private void useWC() {
        goCloseToDoorbell = new GoToCommand(agent.getId(), new Lazy<Vector3f>() {
            @Override
            public Vector3f getLazy() {
                Spatial targetSpatial = SpatialUtils.getSpatialById(
                        SpatialFactory.getRootNode(), doorbellId);
                return targetSpatial.getWorldTranslation();
            }
        }, this);
        goCloseToDoorbell.setMinDistance(0.5f);
        agent.runCommand(goCloseToDoorbell);
    }
}
