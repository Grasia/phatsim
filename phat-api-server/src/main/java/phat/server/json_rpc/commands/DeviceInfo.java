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

/**
 *
 * @author pablo
 */
@PHATCommandAnn(name = "agentInfo", type = "info", debug = false)
public class DeviceInfo extends PHATCommand {

    enum KEYS {
        id, date, type, currentAction, disease, lastEvents
    }

    String agentId;

    JSONArray result;

    public DeviceInfo() {
    }

    public DeviceInfo(String agentId) {
        this(agentId, null);
    }

    public DeviceInfo(String agentId, PHATCommandListener listener) {
        super(listener);
        this.agentId = agentId;
    }

    @Override
    public void runCommand(Application app) {
        AgentsAppState agentsAppState = app.getStateManager().getState(AgentsAppState.class);

        System.out.println("running command: " + toString());
        result = new JSONArray();
        if (agentId == null) {
            result = new JSONArray();
            Iterator<String> it = agentsAppState.getAgentIds().iterator();
            while (it.hasNext()) {
                String aId = it.next();
                Map<String, Object> aprops = new HashMap<>();
                aprops.put(KEYS.id.name(), aId);
                aprops.put(KEYS.type.name(), agentsAppState.getAgent(aId).getClass().getSimpleName());
                JSONObject ja = new JSONObject(aprops);
                result.add(ja);
            }
            setState(State.Success);
        } else if (agentsAppState.exists(agentId)) {
            System.out.println("Get info!");
            result.add(getInfo(agentId, agentsAppState));
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
        return getClass().getSimpleName() + "(" + agentId + ")";
    }

    @PHATCommParam(mandatory = true, order = 1)
    public void setBodyId(String bodyId) {
        this.agentId = bodyId;
    }

    @Override
    public Object getResult() {
        return result;
    }
}
