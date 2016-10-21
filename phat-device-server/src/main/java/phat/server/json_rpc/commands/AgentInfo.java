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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import phat.body.BodiesAppState;
import phat.body.BodyUtils;
import phat.commands.PHATCommParam;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandAnn;
import phat.commands.PHATCommandListener;

/**
 *
 * @author pablo
 */
@PHATCommandAnn(name = "bodyInfo", type = "info", debug = false)
public class AgentInfo extends PHATCommand {

    String bodyId;

    JSONArray result;

    public AgentInfo() {
    }

    public AgentInfo(String bodyId) {
        this(bodyId, null);
    }

    public AgentInfo(String bodyId, PHATCommandListener listener) {
        super(listener);
        this.bodyId = bodyId;
    }

    @Override
    public void runCommand(Application app) {
        BodiesAppState bodiesAppState = app.getStateManager().getState(BodiesAppState.class);

        result = new JSONArray();
        if (bodyId == null) {
            result = new JSONArray();
            Iterator<String> it = bodiesAppState.getBodyIds().iterator();
            while (it.hasNext()) {
                result.add(getInfo(it.next(), bodiesAppState));
            }
            setState(State.Success);
        } else if (bodiesAppState.exists(bodyId)) {

            result.add(getInfo(bodyId, bodiesAppState));
            setState(State.Success);
        } else {
            setState(State.Fail);
        }
    }

    private JSONObject getInfo(String bodyId, BodiesAppState bodiesAppState) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", bodyId);
        map.put("maxSpeed", bodiesAppState.getSpeed(bodyId));
        map.put("posture", BodyUtils.getBodyPosture(bodiesAppState.getBody(bodyId)));
        map.put("anim", bodiesAppState.getCurrentAnimation(bodyId));
        map.put("walking", bodiesAppState.isWalking(bodyId));
        PHATCommand command = bodiesAppState.getLastCommand();
        map.put("lastCommand", command != null ? command.toString() : null);

        map.put("room", bodiesAppState.getRoomNameLocation(bodyId));

        return new JSONObject(map);
    }

    @Override
    public void interruptCommand(Application app) {
        setState(State.Interrupted);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + bodyId + ")";
    }

    @PHATCommParam(mandatory = true, order = 1)
    public void setBodyId(String bodyId) {
        this.bodyId = bodyId;
    }

    @Override
    public Object getResult() {
        return result;
    }
}
