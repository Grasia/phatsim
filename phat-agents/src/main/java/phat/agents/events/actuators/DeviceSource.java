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
package phat.agents.events.actuators;

import phat.agents.events.*;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

public class DeviceSource implements EventSource {

    Node device;
    
    public DeviceSource(Node device) {
        this.device = device;
    }
    
    @Override
    public Vector3f getLocation() {
        return device.getWorldTranslation();
    }

    @Override
    public String getId() {
        return device.getUserData("ID");
    }

    public Node getDevice() {
        return device;
    }
}
