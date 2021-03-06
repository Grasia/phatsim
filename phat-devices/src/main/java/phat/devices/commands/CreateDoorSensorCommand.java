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
import com.jme3.app.SimpleApplication;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.logging.Level;
import phat.commands.PHATCommParam;
import phat.commands.PHATCommandAnn;
import phat.commands.PHATCommandListener;
import phat.devices.DevicesAppState;
import phat.sensors.door.PHATDoorSensor;
import phat.util.SpatialUtils;
import phat.world.WorldAppState;

/**
 *
 * @author pablo
 */
@PHATCommandAnn(name = "CreateDoorSensor", type = "device", debug = false)
public class CreateDoorSensorCommand extends PHATDeviceCommand {

    private String objectId;
    private String doorSensorId;

    public CreateDoorSensorCommand() {
    }

    public CreateDoorSensorCommand(String objectId, String doorSensorId) {
        this(objectId, doorSensorId, null);
    }

    public CreateDoorSensorCommand(String objectId, String doorSensorId, PHATCommandListener listener) {
        super(listener);
        this.objectId = objectId;
        this.doorSensorId = doorSensorId;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }

    @Override
    public void runCommand(Application app) {
        DevicesAppState devicesAppState = app.getStateManager().getState(DevicesAppState.class);
        WorldAppState worldAppState = app.getStateManager().getState(WorldAppState.class);

        Spatial object = SpatialUtils.getSpatialById(((SimpleApplication) app).getRootNode(), objectId);
        if (object != null) {
            Node sensorNode = new Node(doorSensorId);
            object.getParent().attachChild(sensorNode);

            sensorNode.setName(doorSensorId);

            PHATDoorSensor doorSensor = new PHATDoorSensor(
                    doorSensorId, worldAppState.getCalendar());

            sensorNode.addControl(doorSensor);

            devicesAppState.addDevice(doorSensorId, sensorNode);
            setState(State.Success);
            return;
        }
        setState(State.Fail);
    }

    @Override
    public void interruptCommand(Application app) {
        setState(State.Interrupted);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +objectId+ ","+ doorSensorId + ")";
    }

    public String getObjectId() {
        return objectId;
    }

    public String getDoorSensorId() {
        return doorSensorId;
    }

    @PHATCommParam(mandatory = true, order = 1)
    public void setDoorSensorId(String doorSensorId) {
        this.doorSensorId = doorSensorId;
    }

    @PHATCommParam(mandatory = true, order = 2)
    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }
}