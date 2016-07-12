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
import java.util.logging.Level;
import phat.commands.PHATCommandListener;
import phat.devices.DevicesAppState;
import phat.sensors.accelerometer.AccelerometerControl;
import phat.server.PHATServerManager;
import phat.server.ServerAppState;

/**
 *
 * @author pablo
 */
public class ActivateSpeakerServerCommand extends PHATServerCommand {

    private String sensorID;
    private String sensorGroupID;
  

    public ActivateSpeakerServerCommand(String sensorgroupID, String sensorID) {
        this(sensorgroupID,sensorID, null);
    }

    public ActivateSpeakerServerCommand(String sensorgroupID, String sensorID, PHATCommandListener listener) {
        super(listener);
        this.sensorID = sensorID;  
        this.sensorGroupID=sensorgroupID;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }

    @Override
    public void runCommand(Application app) {
        DevicesAppState devicesAppState = app.getStateManager().getState(DevicesAppState.class);
        ServerAppState serverAppState = app.getStateManager().getState(ServerAppState.class);
        devicesAppState.registerAllAndroidDevicesInScenario();
        Node device = devicesAppState.getDevice(sensorGroupID);
        if (device != null) {
          

            PHATServerManager serverManager = serverAppState.getServerManager();
          //  avd.sendConfigFileForService(serverManager.getIP(), serverManager.getPort());
            
            AccelerometerControl accSensor = device.getControl(AccelerometerControl.class);
            if(accSensor != null) {
                serverManager.createAndStartAudioSpeakerServer(
                        serverAppState,sensorGroupID,sensorID, device);
            }          

            setState(State.Success);
            return;
        }
        setState(State.Fail);
    }

    public String getSensorId() {
        return sensorID;
    }

    @Override
    public void interruptCommand(Application app) {
        setState(State.Interrupted);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +sensorGroupID+","+ sensorID + ")";
    }
}
