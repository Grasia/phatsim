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
package phat.devices.commands;

import com.jme3.app.Application;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.PhysicsControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import java.util.logging.Level;

import phat.commands.PHATCommandListener;
import phat.devices.DevicesAppState;
import phat.util.SpatialFactory;

/**
 *
 * @author pablo
 */
public class SetDeviceInCoordenatesCommand extends PHATDeviceCommand {

    private String deviceId;
    private Vector3f location;
    private Quaternion rotation;
    private float scale = 1f;
    
    public SetDeviceInCoordenatesCommand(String deviceId, Vector3f location) {
        this(deviceId, location, null);
    }

    public SetDeviceInCoordenatesCommand(String deviceId, Vector3f location, PHATCommandListener listener) {
        super(listener);
        this.deviceId = deviceId;
        this.location = location;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }

    @Override
    public void runCommand(Application app) {
        DevicesAppState devicesAppState = app.getStateManager().getState(DevicesAppState.class);

        Node device = devicesAppState.getDevice(deviceId);

        if (device != null) {
            //device.setLocalRotation(new Quaternion(new float[]{98* FastMath.DEG_TO_RAD, 149* FastMath.DEG_TO_RAD, 5* FastMath.DEG_TO_RAD}));
            SpatialFactory.getRootNode().attachChild(device);
            setLocation(device, location);
            if(rotation != null) {
                setRotation(device, rotation);
            }
            device.setLocalScale(scale);
            
            setState(State.Success);
            return;
        }
        setState(State.Fail);
    }

    private void setLocation(Node device, Vector3f loc) {
        RigidBodyControl rbc = device.getControl(RigidBodyControl.class);
        if(rbc != null) {
            rbc.setPhysicsLocation(loc);
        } else {
            device.setLocalTranslation(loc);
        }
    }
    
    private void setRotation(Node device, Quaternion q) {
        RigidBodyControl rbc = device.getControl(RigidBodyControl.class);
        if(rbc != null) {
            rbc.setPhysicsRotation(q);
        } else {
            device.setLocalRotation(q);
        }
    }

    public Vector3f getLocation() {
        return location;
    }

    public void setLocation(Vector3f location) {
        this.location = location;
    }

    public Quaternion getRotation() {
        return rotation;
    }

    public void setRotation(Quaternion rotation) {
        this.rotation = rotation;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }
    
    @Override
    public void interruptCommand(Application app) {
        setState(State.Fail);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + deviceId + ", " + location + ")";
    }
}
