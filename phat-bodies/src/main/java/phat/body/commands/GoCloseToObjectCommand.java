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
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import java.util.logging.Level;

import phat.body.BodiesAppState;
import phat.body.control.navigation.AutonomousControlListener;
import phat.body.control.navigation.navmesh.NavMeshMovementControl;
import phat.commands.PHATCommParam;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.commands.PHATCommand.State;
import phat.commands.PHATCommandAnn;
import phat.util.SpatialFactory;
import phat.util.SpatialUtils;

/**
 *
 * @author pablo
 */
@PHATCommandAnn(name = "GoCloseToObject", type = "body", debug = false)
public class GoCloseToObjectCommand extends PHATCommand implements
        AutonomousControlListener {

    String bodyId;
    String targetObjectId;
    float minDistance = 0.5f;
    Vector3f relativePosition = new Vector3f(0f, 0f, 0f);

    public GoCloseToObjectCommand() {
    }

    public GoCloseToObjectCommand(String bodyId, String targetObjectId,
            PHATCommandListener listener) {
        super(listener);
        this.bodyId = bodyId;
        this.targetObjectId = targetObjectId;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }

    public GoCloseToObjectCommand(String bodyId, String targetObjectId) {
        this(bodyId, targetObjectId, null);
    }

    @Override
    public void runCommand(Application app) {
        BodiesAppState bodiesAppState = app.getStateManager().getState(
                BodiesAppState.class);

        Node body = bodiesAppState.getBody(bodyId);

        if (body != null && body.getParent() != null) {
            Node rootNode = SpatialUtils.getRootNode(body);
            Spatial targetSpatial = SpatialUtils.getSpatialById(rootNode,
                    targetObjectId);
            if (targetSpatial != null) {
                NavMeshMovementControl nmmc = body
                        .getControl(NavMeshMovementControl.class);
                if (nmmc != null) {
                    nmmc.setMinDistance(minDistance);
                    // Vector3f loc =
                    // SpatialUtils.getCenterBoinding(targetSpatial);
                    Vector3f loc = new Vector3f();
                    loc.set(targetSpatial.getWorldTranslation());
                    loc.addLocal(targetSpatial.getWorldRotation().mult(relativePosition));
                    
                    /*Node root = SpatialUtils.getRootNode(targetSpatial);
                    Spatial s = SpatialFactory.createCube(Vector3f.UNIT_XYZ.mult(0.1f), ColorRGBA.Blue);
                    s.setLocalTranslation(loc);
                    root.attachChild(s);*/
                    
                    boolean reachable = nmmc.moveTo(loc);
                    if (reachable) {
                        nmmc.setListener(this);
                        return;
                    }
                }
            } else {
                System.out.println("Target not found!");
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
        return getClass().getSimpleName() + "(" + bodyId + ",targetObjectId="
                + targetObjectId + ",minDistance=" + minDistance + ")";
    }

    public float getMinDistance() {
        return minDistance;
    }

    public String getBodyId() {
        return bodyId;
    }

    public String getTargetObjectId() {
        return targetObjectId;
    }
    
    public void setRelativePosition(float x, float y, float z) {
        relativePosition.set(x, y, z);
    }

    @PHATCommParam(mandatory=true, order=1)
    public void setBodyId(String bodyId) {
        this.bodyId = bodyId;
    }

    @PHATCommParam(mandatory=true, order=2)
    public void setTargetObjectId(String targetObjectId) {
        this.targetObjectId = targetObjectId;
    }

    @PHATCommParam(mandatory=false, order=3)
    public void setMinDistance(float minDistance) {
        this.minDistance = minDistance;
    }

}
