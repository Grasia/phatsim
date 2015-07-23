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
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.logging.Level;
import phat.commands.PHATCommandListener;
import phat.devices.DevicesAppState;
import phat.devices.smartphone.SmartPhoneFactory;
import phat.sensors.presence.PHATPresenceSensor;
import phat.util.Debug;
import phat.util.SpatialUtils;
import phat.world.WorldAppState;

/**
 *
 * @author pablo
 */
public class CreatePresenceSensorCommand extends PHATDeviceCommand {

    private String presenceSensorId;
    private boolean enableDebug = false;
    private Vector3f dimensions = new Vector3f(0.048f, 0.08f, 0.002f);
    private float frecuency = 1f;
    private float distance = 5.0f;
    private float hAngle = 180.0f;
    private float angleStep = 10f;
    private float vAngle = 30.0f;
    private Vector3f rotate = new Vector3f();

    public CreatePresenceSensorCommand(String presenceSensorId) {
        this(presenceSensorId, null);
    }

    public CreatePresenceSensorCommand(String presenceSensorId, PHATCommandListener listener) {
        super(listener);
        this.presenceSensorId = presenceSensorId;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }

    @Override
    public void runCommand(Application app) {
        DevicesAppState devicesAppState = app.getStateManager().getState(DevicesAppState.class);
        WorldAppState worldAppState = app.getStateManager().getState(WorldAppState.class);
        
        Spatial device = SpatialUtils.getSpatialById(((SimpleApplication)app).getRootNode(), presenceSensorId);
        Node psNode;
        if(device != null && device instanceof Node) {
            psNode = (Node) device;
        } else {
            psNode = new Node();
            //Geometry psGeo = SpatialFactory.createCube(dimensions, ColorRGBA.Blue);
            //psNode.attachChild(psGeo);
            
        }
        psNode.setName(presenceSensorId);
        psNode.rotate(rotate.x, rotate.y, rotate.z);
                
        PHATPresenceSensor presenceSensor = new PHATPresenceSensor(
                presenceSensorId, worldAppState.getCalendar());
        presenceSensor.setDebug(enableDebug);
        presenceSensor.setDistance(distance);
        presenceSensor.setFrecuency(frecuency);
        presenceSensor.sethAngle(hAngle);
        presenceSensor.setvAngle(vAngle);
        presenceSensor.setAngleStep(angleStep);
        
        if (enableDebug) {
            Debug.attachCoordinateAxes(Vector3f.ZERO, 0.5f, SmartPhoneFactory.assetManager, psNode);
        }
        
        psNode.addControl(presenceSensor);
        
        devicesAppState.addDevice(presenceSensorId, psNode);
        setState(State.Success);
    }

    @Override
    public void interruptCommand(Application app) {
        setState(State.Interrupted);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + presenceSensorId + ")";
    }

    public CreatePresenceSensorCommand setEnableDebug(boolean enableDebug) {
        this.enableDebug = enableDebug;
        return this;
    }
    
    public CreatePresenceSensorCommand setDimensions(float width, float height, float depth) {
        dimensions.set(width, height, depth);
        return this;
    }

    public Vector3f getDimensions() {
        return dimensions;
    }

    public String getPresenceSensorId() {
        return presenceSensorId;
    }

    public void setPresenceSensorId(String presenceSensorId) {
        this.presenceSensorId = presenceSensorId;
    }

    public boolean isEnableDebug() {
        return enableDebug;
    }

    public float getFrecuency() {
        return frecuency;
    }

    public void setFrecuency(float frecuency) {
        this.frecuency = frecuency;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public float gethAngle() {
        return hAngle;
    }

    public void sethAngle(float hAngle) {
        this.hAngle = hAngle;
    }

    public float getAngleStep() {
        return angleStep;
    }

    public void setAngleStep(float angleStep) {
        this.angleStep = angleStep;
    }

    public float getvAngle() {
        return vAngle;
    }

    public void setvAngle(float vAngle) {
        this.vAngle = vAngle;
    }

    public Vector3f getRotate() {
        return rotate;
    }

    public void setRotate(Vector3f rotate) {
        this.rotate = rotate;
    }
}
