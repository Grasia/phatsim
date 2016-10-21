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

import phat.agents.Agent;
import phat.agents.automaton.Automaton;

public class IsObjInRoomCondition extends AutomatonCondition {
    String objId;
    String roomName;
    
    public IsObjInRoomCondition() {
        super();
    }

    public IsObjInRoomCondition(String objId, String roomName) {
        this();
        this.objId = objId;
        this.roomName = roomName;
    }
    
    @Override
    public boolean simpleEvaluation(Agent agent) {
        if(objId != null) {
            return agent.getAgentsAppState().getHouseAppState().getHouse("House1").isObjInRoom(objId, roomName);
        } else {
            return agent.getAgentsAppState().getHouseAppState().getHouse("House1").isObjInRoom(agent.getId(), roomName);
        }
    }

    @Override
    public void automatonInterrupted(Automaton automaton) {
    }

    @Override
    public void automatonResumed(Automaton automaton) {
    }
    
    @Override
    public void automatonReset(Automaton automaton) {
    }

    @AutoCondParam(name = "entityId")
    public String getObjId() {
        return objId;
    }

    @AutoCondParam(name = "roomName")
    public String getRoomName() {
        return roomName;
    }
}
