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
import com.jme3.scene.Node;
import java.util.logging.Level;
import phat.commands.PHATCommParam;
import phat.commands.PHATCommandAnn;
import phat.commands.PHATCommandListener;
import phat.devices.DevicesAppState;
import phat.devices.actuators.VibratorActuator;

/**
 *
 * @author pablo
 */
@PHATCommandAnn(name = "vibrate", type = "device", debug = false)
public class VibrateDeviceCommand extends PHATDeviceCommand {
    
    private String deviceId;
    private long millis;

    public VibrateDeviceCommand() {
    }

    public VibrateDeviceCommand(String deviceId, long millis) {
        this(deviceId, millis, null);
    }

    public VibrateDeviceCommand(String deviceId, long millis, PHATCommandListener listener) {
        super(listener);
        this.deviceId = deviceId;
        this.millis = millis;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }

    @Override
    public void runCommand(Application app) {
        DevicesAppState devicesAppState = app.getStateManager().getState(DevicesAppState.class);
        Node device = devicesAppState.getDevice(deviceId);
        if (device != null) {
            VibratorActuator vibratorActuator = device.getControl(VibratorActuator.class);
            if (vibratorActuator != null) {
                vibratorActuator.vibrate(millis);
                setState(State.Success);
                return;
            }
        }
        setState(State.Fail);
    }

    @Override
    public void interruptCommand(Application app) {
        setState(State.Interrupted);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + deviceId + ", "+millis+")";
    }

    @PHATCommParam(mandatory = true, order = 1)
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    @PHATCommParam(mandatory = true, order = 2)
    public void setMillis(long millis) {
        this.millis = millis;
    }
}
