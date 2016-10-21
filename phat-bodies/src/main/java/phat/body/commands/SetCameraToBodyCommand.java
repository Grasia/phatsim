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
import com.jme3.app.SimpleApplication;
import com.jme3.math.Vector3f;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
import com.jme3.scene.control.CameraControl.ControlDirection;
import java.util.logging.Level;
import phat.body.BodiesAppState;
import phat.commands.PHATCommParam;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandAnn;
import phat.commands.PHATCommandListener;

/**
 *
 * @author pablo
 */
@PHATCommandAnn(name="SetCameraToBody", type="body", debug = true)
public class SetCameraToBodyCommand extends PHATCommand {

    private String bodyId;
    CameraNode camNode;
    float distance = 2f;
    float height = 2f;

    public SetCameraToBodyCommand() {
    }

    public SetCameraToBodyCommand(String bodyId, PHATCommandListener listener) {
        super(listener);
        this.bodyId = bodyId;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }

    public SetCameraToBodyCommand(String bodyId) {
        this(bodyId, null);
    }

    @Override
    public void runCommand(Application app) {
        BodiesAppState bodiesAppState = app.getStateManager().getState(BodiesAppState.class);

        Node body = bodiesAppState.getBody(bodyId);
        if (app instanceof SimpleApplication) {
            ((SimpleApplication) app).getFlyByCamera().setEnabled(false);
        }
        if (body != null && body.getParent() != null) {
            camNode = new CameraNode("CamNode", app.getCamera());
            camNode.setControlDir(ControlDirection.SpatialToCamera);
            camNode.setLocalTranslation(new Vector3f(0, height, distance));
            camNode.lookAt(body.getLocalTranslation().add(Vector3f.UNIT_Y), Vector3f.UNIT_Y);
            body.attachChild(camNode);
            setState(State.Success);
            return;
        }
        setState(State.Fail);
    }

    @Override
    public void interruptCommand(Application app) {
        BodiesAppState bodiesAppState = app.getStateManager().getState(BodiesAppState.class);

        Node body = bodiesAppState.getBody(bodyId);
        if (body != null && body.getParent() != null) {
            camNode.removeFromParent();
            setState(State.Interrupted);
            return;
        }
        setState(State.Fail);
    }

    public float getDistance() {
        return distance;
    }

    public float getHeight() {
        return height;
    }

    public boolean isFront() {
        return distance > 0;
    }

    public void setFront(boolean front) {
        if((front && distance < 0) || (!front && distance > 0)) {
            distance *= -1;
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + bodyId + ",distance="+Math.abs(distance)+",height="+height+",front="+isFront()+")";
    }

    @PHATCommParam(mandatory=true, order=1)
    public void setBodyId(String bodyId) {
        this.bodyId = bodyId;
    }

    @PHATCommParam(mandatory=true, order=2)
    public void setDistance(float distance) {
        this.distance = distance;
    }

    @PHATCommParam(mandatory=true, order=3)
    public void setHeight(float height) {
        this.height = height;
    }
}
