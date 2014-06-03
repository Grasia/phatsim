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
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.logging.Level;
import phat.commands.PHATCommandListener;
import phat.devices.DevicesAppState;
import phat.devices.smartphone.SmartPhoneFactory;
import phat.util.Debug;

/**
 *
 * @author pablo
 */
public class CreateSmartphoneCommand extends PHATDeviceCommand {

    private String smartphoneId;
    private boolean cameraSensor = false;
    private boolean accelerometerSensor = false;
    private boolean microphoneSensor = false;
    private boolean attachCoordinateAxes = false;
    
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
        Node smartphone = SmartPhoneFactory.createSmartphone(smartphoneId);        
        smartphone.setName(smartphoneId);

        SmartPhoneFactory.enableAccelerometerFacility(smartphone);
        SmartPhoneFactory.enableMicrophoneFacility(smartphone);
        SmartPhoneFactory.enableCameraFacility(smartphone);
        
        if(attachCoordinateAxes) {
            Debug.attachCoordinateAxes(Vector3f.ZERO, 0.5f, SmartPhoneFactory.assetManager, smartphone);
        }
        devicesAppState.addDevice(smartphoneId, smartphone);
        setState(State.Success);
    }

    @Override
    public void interruptCommand(Application app) {
        setState(State.Interrupted);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + smartphoneId +")";
    }

    public CreateSmartphoneCommand setAttachCoordinateAxes(boolean attachCoordinateAxes) {
        this.attachCoordinateAxes = attachCoordinateAxes;
        return this;
    }
}
