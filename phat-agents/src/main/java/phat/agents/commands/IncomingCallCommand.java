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
package phat.agents.commands;

import com.jme3.app.Application;
import com.jme3.scene.Node;
import java.util.logging.Level;
import phat.agents.AgentsAppState;
import phat.agents.events.actuators.DeviceSource;
import phat.agents.events.actuators.PHATCallStateEvent;
import phat.commands.PHATCommandListener;
import phat.devices.DevicesAppState;
import phat.devices.commands.PHATDeviceCommand;

/**
 *
 * @author pablo
 */
public class IncomingCallCommand extends PHATDeviceCommand {
    
    private String deviceId;
    private String phoneNumber;

    /**
     * 
     * @param deviceId
     * @param phoneNumber 
     */
    public IncomingCallCommand(String deviceId, String phoneNumber) {
        this(deviceId, phoneNumber, null);
    }

    public IncomingCallCommand(String deviceId, String phoneNumber, PHATCommandListener listener) {
        super(listener);
        this.deviceId = deviceId;
        this.phoneNumber = phoneNumber;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }

    @Override
    public void runCommand(Application app) {
        DevicesAppState devicesAppState = app.getStateManager().getState(DevicesAppState.class);
        AgentsAppState agentsAppState = app.getStateManager().getState(AgentsAppState.class);
        Node device = devicesAppState.getDevice(deviceId);
        if (device != null) {
            agentsAppState.add(new PHATCallStateEvent(
                    deviceId + "-Call-RINGING",
                    new DeviceSource(device),
                    "RINGING",
                    phoneNumber));
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
        return getClass().getSimpleName() + "(" + deviceId + ", "+phoneNumber+")";
    }
}
