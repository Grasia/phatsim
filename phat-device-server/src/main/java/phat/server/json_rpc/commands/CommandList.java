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
import java.util.Map;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import phat.commands.PHATCommParam;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandAnn;
import phat.commands.PHATCommandListener;
import phat.server.json_rpc.JsonRpcAppState;
import phat.server.json_rpc.JsonToPHATCommandFactory;

/**
 *
 * @author pablo
 */
@PHATCommandAnn(name = "help", type = "info", debug = false)
public class CommandList extends PHATCommand {

    public static enum COMMAND_TYPE {env, body, device, info}
    
    COMMAND_TYPE type;
    
    JSONArray result;
    
    public CommandList() {
    }

    public CommandList(COMMAND_TYPE type) {
        this(type, null);
    }

    public CommandList(COMMAND_TYPE type, PHATCommandListener listener) {
        super(listener);
        this.type = type;
    }

    @Override
    public void runCommand(Application app) {
        JsonRpcAppState jsonRpcAppState = app.getStateManager().getState(JsonRpcAppState.class);
        result = new JSONArray();
        JsonToPHATCommandFactory factory = jsonRpcAppState.getCommandFactory();
        
        for (String methodName : factory.getMethodNames()) {
            if (type == null || factory.getAnnType(methodName).equals(type.name())) {
                Map<String, Object> map = new HashMap<>();
                map.put("cName", methodName);
                map.put("cType", factory.getAnnType(methodName));
                map.put("cDebug", String.valueOf(factory.isAnnDebug(methodName)));                
                map.put("cUsage", factory.getUsage(methodName));
                JSONObject obj = new JSONObject(map);
                result.add(obj);
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
        return getClass().getSimpleName() + "(" + type + ")";
    }

    @PHATCommParam(mandatory = false, order = 1)
    public void setType(COMMAND_TYPE type) {
        this.type = type;
    }
    
    @Override
    public Object getResult() {
        return result;
    }
}
