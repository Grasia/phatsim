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

import java.util.logging.Level;

import phat.body.BodiesAppState;
import phat.body.control.navigation.AutonomousControlListener;
import phat.body.control.navigation.navmesh.NavMeshMovementControl;
import phat.body.control.physics.PHATCharacterControl;
import phat.commands.PHATCommParam;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.commands.PHATCommand.State;
import phat.commands.PHATCommandAnn;

/**
 *
 * @author pablo
 */
@PHATCommandAnn(name = "GoCloseToBody", type = "body", debug = false)
public class GoCloseToBodyCommand extends PHATCommand implements
        AutonomousControlListener {

    private String bodyId;
    private String targetBodyId;

    public GoCloseToBodyCommand() {
    }

    public GoCloseToBodyCommand(String bodyId, String targetBodyId,
            PHATCommandListener listener) {
        super(listener);
        this.bodyId = bodyId;
        this.targetBodyId = targetBodyId;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }

    public GoCloseToBodyCommand(String bodyId, String targetBodyId) {
        this(bodyId, targetBodyId, null);
    }

    @Override
    public void runCommand(Application app) {
        BodiesAppState bodiesAppState = app.getStateManager().getState(
                BodiesAppState.class);

        Node body = bodiesAppState.getBody(bodyId);

        if (body != null && body.getParent() != null) {
            Node targetBody = bodiesAppState.getBody(targetBodyId);
            if (targetBody != null && targetBody.getParent() != null) {
                NavMeshMovementControl nmmc = body
                        .getControl(NavMeshMovementControl.class);
                if (nmmc != null) {
                    PHATCharacterControl cc = targetBody
                            .getControl(PHATCharacterControl.class);
                    nmmc.setMinDistance(0.5f);
                    boolean reachable = nmmc.moveTo(cc.getLocation());
                    if (reachable) {
                        nmmc.setListener(this);
                        return;
                    }
                }
            }
        }
        setState(State.Fail);
    }

    @Override
    public void interruptCommand(Application app) {
        BodiesAppState bodiesAppState = app.getStateManager().getState(
                BodiesAppState.class);

        Node body = bodiesAppState.getBody(bodyId);

        if (body != null && body.getParent() != null) {
            NavMeshMovementControl nmmc = body
                    .getControl(NavMeshMovementControl.class);
            nmmc.moveTo(null);
            setState(State.Interrupted);
            return;
        }
        setState(State.Fail);
    }

    @Override
    public void destinationReached(Vector3f destination) {
        setState(State.Success);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + bodyId + ",targetBodyId="
                + targetBodyId + ")";
    }

    public String getBodyId() {
        return bodyId;
    }

    public String getTargetBodyId() {
        return targetBodyId;
    }

    @PHATCommParam(mandatory = true, order = 1)
    public void setBodyId(String bodyId) {
        this.bodyId = bodyId;
    }

    @PHATCommParam(mandatory = true, order = 2)
    public void setTargetBodyId(String targetBodyId) {
        this.targetBodyId = targetBodyId;
    }

}
