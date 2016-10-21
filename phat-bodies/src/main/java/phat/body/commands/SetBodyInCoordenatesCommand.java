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
import com.jme3.bullet.BulletAppState;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

import java.util.logging.Level;

import phat.body.BodiesAppState;
import phat.body.control.physics.PHATCharacterControl;
import phat.commands.PHATCommParam;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandAnn;
import phat.commands.PHATCommandListener;
import phat.structures.houses.HouseAppState;
import phat.util.PhysicsUtils;

/**
 *
 * @author pablo
 */
@PHATCommandAnn(name="SetBodyXYZ", type="body", debug = false)
public class SetBodyInCoordenatesCommand extends PHATCommand {

    private String bodyId;
    private Vector3f location;
    private float x = -1;
    private float y = -1;
    private float z = -1;

    public SetBodyInCoordenatesCommand() {
    }

    public SetBodyInCoordenatesCommand(String bodyId, Vector3f location) {
        this(bodyId, location, null);
    }

    public SetBodyInCoordenatesCommand(String bodyId, Vector3f location, PHATCommandListener listener) {
        super(listener);
        this.bodyId = bodyId;
        this.location = location;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }

    @Override
    public void runCommand(Application app) {
        BodiesAppState bodiesAppState = app.getStateManager().getState(BodiesAppState.class);
        HouseAppState houseAppState = app.getStateManager().getState(HouseAppState.class);
        BulletAppState bulletAppState = app.getStateManager().getState(BulletAppState.class);

        Node body = bodiesAppState.getBody(bodyId);

        if (body != null && body.getParent() == null) {
            PHATCharacterControl cc = body.getControl(PHATCharacterControl.class);
            bodiesAppState.getBodiesNode().attachChild(body);

            PhysicsUtils.addAllPhysicsControls(body, bulletAppState);
            //bulletAppState.getPhysicsSpace().addAll(body);

            if(location == null) {
                location = new Vector3f(x, y, z);
            }
            if(cc != null)
                cc.warp(location);
            else
                body.setLocalTranslation(location);
            setState(State.Success);
            return;
        }
        setState(State.Fail);
    }

    @Override
    public void interruptCommand(Application app) {
        setState(State.Fail);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + bodyId + ", " + location + ")";
    }

    @PHATCommParam(mandatory=true, order=1)
    public void setBodyId(String bodyId) {
        this.bodyId = bodyId;
    }

    @PHATCommParam(mandatory=true, order=2)
    public void setX(float x) {
        this.x = x;
    }

    @PHATCommParam(mandatory=true, order=3)
    public void setY(float y) {
        this.y = y;
    }

    @PHATCommParam(mandatory=true, order=4)
    public void setZ(float z) {
        this.z = z;
    }
    
    
}
