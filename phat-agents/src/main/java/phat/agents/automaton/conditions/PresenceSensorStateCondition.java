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
import phat.sensors.presence.PHATPresenceSensor;
import phat.sensors.presence.PresenceData;

public class PresenceSensorStateCondition extends AutomatonCondition {

    private final static Logger logger = Logger.getLogger(PresenceSensorStateCondition.class.getName());

    String presenceSensorId;
    String presenceSensorState;

    public PresenceSensorStateCondition(String presenceSensorId, String presenceSensorState) {
        this.presenceSensorId = presenceSensorId;
        this.presenceSensorState = presenceSensorState;
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
        Node device = agent.getAgentsAppState().getDevicesAppState().getDevice(presenceSensorId);
        if (device != null) {
            PHATPresenceSensor psControl = device.getControl(PHATPresenceSensor.class);
            if (psControl != null) {
                PresenceData presenceData = psControl.getPresenceData();
                if(presenceData != null) {
                    return (presenceSensorState.equals("ON") && presenceData.isPresence()) ||
                            (presenceSensorState.equals("OFF") && !presenceData.isPresence());
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

    @AutoCondParam(name = "sensorId")
    public String getPresenceSensorId() {
        return presenceSensorId;
    }

    @AutoCondParam(name = "sensorState")
    public String getPresenceSensorState() {
        return presenceSensorState;
    }

    @Override
    public String toString() {
        return "LightStateCondition(" + presenceSensorId + "," + presenceSensorState + ")";
    }
}
