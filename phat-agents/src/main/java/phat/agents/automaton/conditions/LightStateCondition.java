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

import com.jme3.light.Light;
import java.util.List;
import java.util.logging.Logger;
import phat.agents.Agent;
import phat.agents.automaton.Automaton;

public class LightStateCondition extends AutomatonCondition {

    private final static Logger logger = Logger.getLogger(LightStateCondition.class.getName());

    String roomId;
    String lightState;

    public LightStateCondition(String roomId, String lightState) {
        this.roomId = roomId;
        this.lightState = lightState;
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
        boolean isOn = agent.getAgentsAppState().getHouseAppState().getHouse("House1").isLightOn(roomId);
        return (lightState.equals("ON") && !isOn)
                || (lightState.equals("OFF") && isOn);
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

    public String getBodyId() {
        return roomId;
    }

    public String getBodyState() {
        return lightState;
    }

    @Override
    public String toString() {
        return "LightStateCondition(" + roomId + "," + lightState + ")";
    }

    @AutoCondParam(name = "roomId")
    public String getRoomId() {
        return roomId;
    }

    @AutoCondParam(name = "lightState")
    public String getLightState() {
        return lightState;
    }
}
