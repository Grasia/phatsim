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
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
import java.util.List;
import java.util.logging.Level;
import phat.commands.PHATCommParam;
import phat.commands.PHATCommandAnn;
import phat.commands.PHATCommandListener;
import phat.devices.DevicesAppState;
import phat.devices.smartphone.SmartPhoneFactory;
import static phat.devices.smartphone.SmartPhoneFactory.assetManager;
import phat.util.Debug;
import phat.util.SpatialUtils;

/**
 *
 * @author pablo
 */
@PHATCommandAnn(name = "CreateDeviceSensor", type = "device", debug = false)
public class CreateSmartphoneCommand extends PHATDeviceCommand {

    private String smartphoneId;
    private boolean cameraSensor = true;
    private boolean accelerometerSensor = true;
    private boolean microphoneSensor = true;
    private boolean attachCoordinateAxes = false;
    private Vector3f dimensions = new Vector3f(0.048f, 0.08f, 0.002f);
    private float dx = -1;
    private float dy = -1;
    private float dz = -1;

    public CreateSmartphoneCommand() {
    }

    public CreateSmartphoneCommand(String smartphoneId) {
        this(smartphoneId, null);
    }

    public CreateSmartphoneCommand(String smartphoneId, PHATCommandListener listener) {
        super(listener);
        this.smartphoneId = smartphoneId;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }

    @Override
    public void runCommand(Application app) {
        DevicesAppState devicesAppState = app.getStateManager().getState(DevicesAppState.class);
        
        Spatial device = SpatialUtils.getSpatialById(((SimpleApplication)app).getRootNode(), smartphoneId);
        Node smartphone;
        System.out.println("DEVICE ===== "+device);
        if(device != null && device instanceof Node) {
            smartphone = (Node) device;
            fixScreen(device);
        } else {
            if(dx != -1 && dy != -1 && dz != -1) {
                dimensions.set(dx, dy, dz);
            }
            smartphone = SmartPhoneFactory.createSmartphone(smartphoneId, dimensions);
        }
        smartphone.setName(smartphoneId);
        
        if (accelerometerSensor) {
            SmartPhoneFactory.enableAccelerometerFacility(smartphone);
        }
        if (microphoneSensor) {
            SmartPhoneFactory.enableMicrophoneFacility(smartphone);
        }
        if (cameraSensor) {            
            SmartPhoneFactory.enableCameraFacility(smartphone);
        }
        
        SmartPhoneFactory.enableVibratorFacility(smartphone);

        if (attachCoordinateAxes) {
            Debug.attachCoordinateAxes(Vector3f.ZERO, 0.5f, SmartPhoneFactory.assetManager, smartphone);
        }
        devicesAppState.addDevice(smartphoneId, smartphone);
        setState(State.Success);
    }
    
    private void fixScreen(Spatial device) {
        List<Spatial> screens = SpatialUtils.getSpatialsByRole(device, "Screen");
            if (screens.size() > 0) {
                Geometry geo = (Geometry) screens.get(0);
                Node p = geo.getParent();
                geo.removeFromParent();
                geo = new Geometry("Screen", new Quad(1f, 0.6f));
                geo.setUserData("ID", "Screen1");
                geo.setUserData("ROLE", "Screen");
                Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                mat.setTexture("ColorMap", assetManager.loadTexture("Textures/FrontSmartPhone.jpg"));
                geo.setMaterial(mat);
                geo.move(-1.4f, 0f, -0.6f);
                geo.rotate(-FastMath.HALF_PI, 0f, 0f);
                geo.setLocalScale(Vector3f.UNIT_XYZ.divide(p.getWorldScale()));
                p.attachChild(geo);
                setState(State.Success);
                return;
            }
    }

    @Override
    public void interruptCommand(Application app) {
        setState(State.Interrupted);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + smartphoneId + ")";
    }

    public CreateSmartphoneCommand setAttachCoordinateAxes(boolean attachCoordinateAxes) {
        this.attachCoordinateAxes = attachCoordinateAxes;
        return this;
    }
    
    public CreateSmartphoneCommand setDimensions(float width, float height, float depth) {
        dimensions.set(width, height, depth);
        return this;
    }

    public Vector3f getDimensions() {
        return dimensions;
    }

    public boolean isCameraSensor() {
        return cameraSensor;
    }

    public boolean isAccelerometerSensor() {
        return accelerometerSensor;
    }

    public boolean isMicrophoneSensor() {
        return microphoneSensor;
    }

    public String getSmartphoneId() {
        return smartphoneId;
    }

    @PHATCommParam(mandatory = true, order = 1)
    public void setSmartphoneId(String smartphoneId) {
        this.smartphoneId = smartphoneId;
    }

    @PHATCommParam(mandatory = false, order = 2)
    public void setDx(float dx) {
        this.dx = dx;
    }

    @PHATCommParam(mandatory = false, order = 3)
    public void setDy(float dy) {
        this.dy = dy;
    }

    @PHATCommParam(mandatory = false, order = 4)
    public void setDz(float dz) {
        this.dz = dz;
    }

    public void setCameraSensor(boolean cameraSensor) {
        this.cameraSensor = cameraSensor;
    }

    public void setAccelerometerSensor(boolean accelerometerSensor) {
        this.accelerometerSensor = accelerometerSensor;
    }

    public void setMicrophoneSensor(boolean microphoneSensor) {
        this.microphoneSensor = microphoneSensor;
    }
}
