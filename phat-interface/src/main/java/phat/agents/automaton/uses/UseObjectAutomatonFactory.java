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
package phat.agents.automaton.uses;

import com.jme3.scene.Spatial;

import phat.agents.Agent;
import phat.agents.automaton.Automaton;
import phat.util.SpatialFactory;
import phat.util.SpatialUtils;

public class UseObjectAutomatonFactory {

    public static Automaton getAutomaton(Agent agent, String objectId) {
        Spatial objectSpatial = SpatialUtils.getSpatialById(
                SpatialFactory.getRootNode(), objectId);
        if (objectSpatial == null) {
            return null;
        }

        String role = objectSpatial.getUserData("ROLE");
        if (role == null) {
            return null;
        }

        if (role.equals("Shower")) {
            return new HaveAShowerAutomaton(agent, objectId);
        } else if(role.equals("WC")) {
            return new UseWCAutomaton(agent, objectId);
        } else if(role.equals("Doorbell")) {
            return new UseDoorbellAutomaton(agent, objectId);
        } else if(role.equals("TV")) {
            return new SwitchTVAutomaton(agent, objectId, true);
        } else {
            return new UseCommonObjectAutomaton(agent, objectId);
        }
    }
}
