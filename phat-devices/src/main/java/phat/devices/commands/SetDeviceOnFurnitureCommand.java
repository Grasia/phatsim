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
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.List;
import java.util.logging.Level;
import phat.commands.PHATCommParam;
import phat.commands.PHATCommandAnn;
import phat.commands.PHATCommandListener;
import phat.devices.DevicesAppState;
import phat.structures.houses.HouseAppState;
import phat.util.PhysicsUtils;

/**
 *
 * @author pablo
 */
@PHATCommandAnn(name = "SetDeviceOnFurniture", type = "device", debug = false)
public class SetDeviceOnFurnitureCommand extends PHATDeviceCommand {

    private String deviceId;
    private String houseId;
    private String furnitureId;
    private String placeId;

    public SetDeviceOnFurnitureCommand() {
    }

    public SetDeviceOnFurnitureCommand(String smartphoneId, String houseId, String furnitureId) {
        this(smartphoneId, houseId, furnitureId, null);
    }

    public SetDeviceOnFurnitureCommand(String smartphoneId, String houseId, String furnitureId, PHATCommandListener listener) {
        super(listener);
        this.deviceId = smartphoneId;
        this.houseId = houseId;
        this.furnitureId = furnitureId;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }

    @Override
    public void runCommand(Application app) {
        HouseAppState houseAppState = app.getStateManager().getState(HouseAppState.class);
        BulletAppState bulletAppState = app.getStateManager().getState(BulletAppState.class);
        DevicesAppState devicesAppState = app.getStateManager().getState(DevicesAppState.class);

        Node device = devicesAppState.getDevice(deviceId);

        if (device != null) {
            if(device.getParent() != null) {
                device.removeFromParent();
                //device.setLocalRotation(new Quaternion().fromAngles(0f, 0f, 0f));
            }
            List<Node> places = houseAppState.getHouse(houseId).getPlaceToPutThings(furnitureId);
            if (!places.isEmpty()) {
                Node place = null;
                if (placeId != null) {
                    place = getNodeWithName(places, placeId);
                    if (place == null) {
                        setState(State.Fail);
                        return;
                    }
                }
                
                if(placeId == null) {
                    place = places.get(0);
                }
                device.setLocalTranslation(Vector3f.ZERO);

                device.getControl(RigidBodyControl.class).setEnabled(true);
                device.getControl(RigidBodyControl.class).setPhysicsLocation(place.getWorldTranslation().addLocal(0f, 0.015f, 0f));
                device.getControl(RigidBodyControl.class).setPhysicsRotation(new Quaternion().fromAngles(-FastMath.HALF_PI, 0f, 0f));

                PhysicsUtils.setHighPhysicsPrecision(device);
                bulletAppState.getPhysicsSpace().addAll(device);

                place.attachChild(device);
                //device.getControl(RigidBodyControl.class).setPhysicsLocation(places.get);
                setState(State.Success);
                return;
            }
        }
        setState(State.Fail);
    }

    private Node getNodeWithName(List<Node> places, String placeName) {
        for (Node n : places) {
            if (n.getName().equals(placeName)) {
                return n;
            }
        }
        return null;
    }

    public String getPlaceId() {
        return placeId;
    }

    @Override
    public void interruptCommand(Application app) {
        setState(State.Interrupted);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + deviceId + ", " + houseId + ", " + furnitureId + ", " + placeId + ")";
    }
    
    @PHATCommParam(mandatory = true, order = 1)
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    
    @PHATCommParam(mandatory = true, order = 2)
    public void setFurnitureId(String furnitureId) {
        this.furnitureId = furnitureId;
    }

    @PHATCommParam(mandatory = false, order = 3)
    public SetDeviceOnFurnitureCommand setPlaceId(String placeId) {
        this.placeId = placeId;
        return this;
    }
    
    public void setHouseId(String houseId) {
        this.houseId = "House1";
    }
}
