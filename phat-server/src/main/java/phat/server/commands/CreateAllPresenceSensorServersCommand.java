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
package phat.server.commands;

import com.jme3.app.Application;
import com.jme3.scene.Node;
import phat.devices.DevicesAppState;
import phat.sensors.presence.PHATPresenceSensor;
import phat.server.PHATServerManager;
import phat.server.ServerAppState;

/**
 *
 * @author pablo
 */
public class CreateAllPresenceSensorServersCommand extends PHATServerCommand {

    public CreateAllPresenceSensorServersCommand() {
        super(null);
    }

    @Override
    public void runCommand(Application app) {        
        DevicesAppState devicesAppState = app.getStateManager().getState(DevicesAppState.class);
        ServerAppState serverAppState = app.getStateManager().getState(ServerAppState.class);
        PHATServerManager serverManager = serverAppState.getServerManager();
        devicesAppState.registerAllAndroidDevicesInScenario();
        for (String deviceId : devicesAppState.getDeviceIds()) {
            Node device = devicesAppState.getDevice(deviceId);
            String type = device.getUserData("ROLE");
            String sensorID=deviceId; // TBD: change in the future with a specific sensorid 
            if (type != null && type.equals("PresenceSensor")) {
                PHATPresenceSensor ps = (PHATPresenceSensor)device.getControl(PHATPresenceSensor.class);
                if(ps != null) {
                    serverManager.createAndStartPresenceServer(deviceId, sensorID, ps);
                }
            }
        }
        setState(State.Success);
    }

    @Override
    public void interruptCommand(Application app) {
        setState(State.Interrupted);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "()";
    }
}
