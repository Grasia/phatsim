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
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.logging.Level;
import phat.commands.PHATCommandListener;
import phat.devices.DevicesAppState;
import phat.devices.smartphone.SmartPhoneFactory;
import phat.util.Debug;
import phat.util.SpatialUtils;

/**
 *
 * @author pablo
 */
public class CreateAccelerometerSensorCommand extends PHATDeviceCommand {

    private String sensorID;
    private boolean attachCoordinateAxes = false;
    private Vector3f dimensions = new Vector3f(0.018f, 0.02f, 0.001f);

    public CreateAccelerometerSensorCommand(String sensorID) {
        this(sensorID, null);
    }

    public CreateAccelerometerSensorCommand(String sensorID, PHATCommandListener listener) {
        super(listener);
        this.sensorID = sensorID;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }

    @Override
    public void runCommand(Application app) {
        DevicesAppState devicesAppState = app.getStateManager().getState(DevicesAppState.class);

        Spatial device = SpatialUtils.getSpatialById(((SimpleApplication) app).getRootNode(), sensorID);
        Node sensor;
        System.out.println("DEVICE ===== " + device);
        if (device != null && device instanceof Node) {
            sensor = (Node) device;
        } else {
            sensor = SmartPhoneFactory.createAccelGeometry(sensorID, dimensions);
        }
        sensor.setName(sensorID);

        SmartPhoneFactory.enableAccelerometerFacility(sensor);

        if (attachCoordinateAxes) {
            Debug.attachCoordinateAxes(Vector3f.ZERO, 0.5f, SmartPhoneFactory.assetManager, sensor);
        }
        devicesAppState.addDevice(sensorID, sensor);
        setState(State.Success);
    }

    @Override
    public void interruptCommand(Application app) {
        setState(State.Interrupted);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + sensorID + ")";
    }

    public CreateAccelerometerSensorCommand setAttachCoordinateAxes(boolean attachCoordinateAxes) {
        this.attachCoordinateAxes = attachCoordinateAxes;
        return this;
    }

    public CreateAccelerometerSensorCommand setDimensions(float width, float height, float depth) {
        dimensions.set(width, height, depth);
        return this;
    }

    public Vector3f getDimensions() {
        return dimensions;
    }

    public String getSensorID() {
        return sensorID;
    }
}
