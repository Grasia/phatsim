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
package phat.body.commands;

import com.jme3.app.Application;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.Observable;
import java.util.Observer;

import java.util.logging.Level;

import phat.body.BodiesAppState;
import phat.body.control.navigation.AutonomousControlListener;
import phat.body.control.navigation.navmesh.NavMeshMovementControl;
import phat.body.control.physics.PHATCharacterControl;
import phat.body.sensing.BasicObjectPerceptionControl;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.commands.PHATCommand.State;

/**
 *
 * @author pablo
 */
public class WaitForCloseToBodyCommand extends PHATCommand implements
        Observer {

    private String bodyId;
    private String targetBodyId;

    private BasicObjectPerceptionControl perceptionControl;
    
    BodiesAppState bodiesAppState;
    Node body;
    
    public WaitForCloseToBodyCommand(String bodyId, String targetBodyId,
            PHATCommandListener listener) {
        super(listener);
        this.bodyId = bodyId;
        this.targetBodyId = targetBodyId;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }

    public WaitForCloseToBodyCommand(String bodyId, String targetBodyId) {
        this(bodyId, targetBodyId, null);
    }

    @Override
    public void runCommand(Application app) {
        bodiesAppState = app.getStateManager().getState(
                BodiesAppState.class);

        body = bodiesAppState.getBody(bodyId);

        if (body != null && body.getParent() != null) {
            final Node targetBody = bodiesAppState.getBody(targetBodyId);
            
            perceptionControl = new BasicObjectPerceptionControl();
            perceptionControl.setTarget(targetBody);
            perceptionControl.setDistance(0.5f);
            perceptionControl.setFrecuency(0.5f);
            perceptionControl.addObserver(this);
            
            body.addControl(perceptionControl);
        }

    }

    private void removePerceptionControl() {
        if (body != null && body.getParent() != null) {
            body.removeControl(perceptionControl);
        }
    }
    
    @Override
    public void interruptCommand(Application app) {
        removePerceptionControl();
        setState(State.Interrupted);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + bodyId + ",targetBodyId="
                + targetBodyId + ")";
    }

    @Override
    public void update(Observable o, Object arg) {
        removePerceptionControl();
        setState(phat.commands.PHATCommand.State.Success);
    }
}
