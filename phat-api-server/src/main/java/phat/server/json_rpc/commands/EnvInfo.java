/*
 * Copyright (C) 2016 Pablo Campillo-Sanchez <pabcampi@ucm.es>
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
package phat.server.json_rpc.commands;

import com.jme3.app.Application;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import phat.agents.Agent;
import phat.agents.AgentsAppState;
import phat.agents.HumanAgent;
import phat.agents.automaton.Automaton;
import phat.commands.PHATCommParam;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandAnn;
import phat.commands.PHATCommandListener;
import phat.server.json_rpc.PHATObjectToJSON;
import phat.structures.houses.House;
import phat.structures.houses.HouseAppState;
import phat.structures.houses.HouseFactory;

/**
 *
 * @author pablo
 */
@PHATCommandAnn(name = "envInfo", type = "info", debug = false)
public class EnvInfo extends PHATCommand {

    enum KEYS {
        id, date, type, currentAction, disease, lastEvents
    }

    String roomId;
    
    JSONObject result;

    public EnvInfo() {
    }

    public EnvInfo(PHATCommandListener listener) {
        super(listener);
    }

    @Override
    public void runCommand(Application app) {
        HouseAppState houseAppState = app.getStateManager().getState(HouseAppState.class);
        House house = houseAppState.getHouse("House1");
        if (house != null) {
            if(roomId != null) {
                result = PHATObjectToJSON.getJSON(roomId, house);
            } else {
                result = PHATObjectToJSON.getJSON("House1", houseAppState.getHouseType(), house);
            }
            setState(State.Success);
        } else {
            setState(State.Fail);
        }
    }

    private JSONObject getInfo(String agentId, AgentsAppState agentsAppState) {
        Agent agent = agentsAppState.getAgent(agentId);
        Map<String, Object> map = new HashMap<>();
        map.put(KEYS.id.name(), agentId);
        map.put(KEYS.type.name(), agent.getClass().getSimpleName());
        map.put(KEYS.date.name(), new Date(agentsAppState.getTime().getTimeInMillis()));
        System.out.println("getting current action...");
        Automaton a = agentsAppState.getCurrentAction(agentId);
        map.put(KEYS.currentAction.name(), PHATObjectToJSON.getJSON(a));
        if (agent instanceof HumanAgent) {
            HumanAgent humanAgent = (HumanAgent) agent;
            map.put(KEYS.disease.name(), PHATObjectToJSON.getDisease(humanAgent.getDiseaseManager()));
            map.put(KEYS.lastEvents.name(), null);
        }
        JSONObject result = new JSONObject(map);
        System.out.println("Result = " + result.toJSONString());
        return result;
    }

    @Override
    public void interruptCommand(Application app) {
        setState(State.Interrupted);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + roomId + ")";
    }

    @PHATCommParam(mandatory = false, order = 1)
    public void setRoomId(String houseId) {
        this.roomId = houseId;
    }

    @Override
    public Object getResult() {
        return result;
    }
}
