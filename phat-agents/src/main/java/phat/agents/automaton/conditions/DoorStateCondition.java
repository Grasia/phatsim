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
package phat.agents.automaton.conditions;

import com.jme3.scene.Node;
import java.util.logging.Logger;
import phat.agents.Agent;
import phat.agents.automaton.Automaton;
import phat.sensors.door.DoorData;
import phat.sensors.door.PHATDoorSensor;

public class DoorStateCondition extends AutomatonCondition {

    private final static Logger logger = Logger.getLogger(DoorStateCondition.class.getName());

    String deviceId;
    String doorState;

    public DoorStateCondition(String objId, String doorState) {
        this.deviceId = objId;
        this.doorState = doorState;
    }

    /**
     * Return true if the agent has the posture indicated in the atribut of the
     * class, in another case it returns false;
     *
     * @param agent
     * @return
     */
    @Override
    public boolean simpleEvaluation(Agent agent) {
        Node device = agent.getAgentsAppState().getDevicesAppState().getDevice(deviceId);
        if (device != null) {
            PHATDoorSensor psControl = device.getControl(PHATDoorSensor.class);
            if (psControl != null) {
                DoorData doorData = psControl.getDoorData();
                if(doorData != null) {
                    return (doorState.equals("ON") && psControl.getDoorData().isOpened()) ||
                            (doorState.equals("OFF") && !psControl.getDoorData().isOpened());
                }
            }
        }
        return false;
    }

    @Override
    public void automatonReset(Automaton automaton) {
    }

    @Override
    public void automatonInterrupted(Automaton automaton) {
    }

    @Override
    public void automatonResumed(Automaton automaton) {
    }

    @AutoCondParam(name = "deviceId")
    public String getObjId() {
        return deviceId;
    }

    @AutoCondParam(name = "doorState")
    public String getDoorState() {
        return doorState;
    }

    @Override
    public String toString() {
        return "DoorStateCondition(" + deviceId + "," + doorState + ")";
    }
}
