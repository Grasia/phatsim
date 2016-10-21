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
package phat.agents;

import com.jme3.math.Vector3f;
import phat.PHATInterface;
import phat.commands.PHATCommand;
import phat.devices.commands.PHATDeviceCommand;

/**
 *
 * @author pablo
 */
public class DeviceAgent extends Agent {

    public DeviceAgent(String bodyId) {
        super(bodyId);
    }

    @Override
    public void agentUpdate(PHATInterface phatInterface) {
        super.update(phatInterface);
    }
    
    @Override
    protected void initAutomaton() {
    }

    @Override
    public boolean isInTheWorld() {
        return agentsAppState.getDevicesAppState().isBodyInTheWorld(bodyId);
    }
    
    @Override
    public boolean isInAHouse(String idHouse) {
        return agentsAppState.getDevicesAppState().isBodyInAHouse(bodyId);
    }

    @Override
    public void runCommand(PHATCommand command) {
        agentsAppState.getDevicesAppState().runCommand((PHATDeviceCommand)command);
    }

    @Override
    public Vector3f getLocation() {
        return agentsAppState.getDevicesAppState().getLocation(bodyId);
    }
}
