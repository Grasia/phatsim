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
package phat.body.commands;

import com.jme3.app.Application;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import java.util.logging.Level;

import phat.body.BodiesAppState;
import phat.body.control.navigation.AutonomousControlListener;
import phat.body.control.navigation.navmesh.NavMeshMovementControl;
import phat.commands.PHATCommParam;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.commands.PHATCommand.State;
import phat.commands.PHATCommandAnn;
import phat.structures.houses.HouseAppState;
import phat.structures.houses.commands.SwitchLightOfRoomCommand;

/**
 *
 * @author pablo
 */
@PHATCommandAnn(name = "GoCloseToObject", type = "body", debug = false)
public class GoCloseToSwitchLightOfRoom extends PHATCommand implements PHATCommandListener {

    String bodyId;
    String roomId;
    boolean on;
    float minDistance = 0.3f;

    BodiesAppState bodiesAppState;
    GoCloseToObjectCommand goCloseToObjectCommand;
    SwitchLightOfRoomCommand switchLSwitchLightOfRoomCommand;

    public GoCloseToSwitchLightOfRoom() {
    }

    public GoCloseToSwitchLightOfRoom(String bodyId, String roomId, boolean on,
            PHATCommandListener listener) {
        super(listener);
        this.bodyId = bodyId;
        this.roomId = roomId;
        this.on = on;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }

    public GoCloseToSwitchLightOfRoom(String bodyId, String targetObjectId, boolean on) {
        this(bodyId, targetObjectId, on, null);
    }

    @Override
    public void runCommand(Application app) {
        bodiesAppState = app.getStateManager().getState(
                BodiesAppState.class);
        HouseAppState houseAppState = app.getStateManager().getState(HouseAppState.class);

        Node body = bodiesAppState.getBody(bodyId);

        if (body != null && body.getParent() != null) {
            Spatial lightSwitch = houseAppState.getHouse("House1").getClosetLightSwitch(body, roomId);
            if (lightSwitch != null) {
                String switchId = lightSwitch.getUserData("ID");
                goCloseToObjectCommand = new GoCloseToObjectCommand(bodyId, switchId, this);
                goCloseToObjectCommand.setMinDistance(minDistance);
                goCloseToObjectCommand.setRelativePosition(0f, 0f, 0.5f);
                bodiesAppState.runCommand(goCloseToObjectCommand);
                bodiesAppState.runCommand(new LookAtCommand(bodyId, switchId));
                return;
            }
        }
        setState(State.Fail);
    }

    @Override
    public void commandStateChanged(PHATCommand command) {
        if (command.getState().equals(PHATCommand.State.Success)) {
            if (command.equals(goCloseToObjectCommand)) {
                bodiesAppState.runCommand(new LookAtCommand(bodyId, null));
                switchLSwitchLightOfRoomCommand
                        = new SwitchLightOfRoomCommand("House1", roomId, on, this);
                bodiesAppState.runCommand(switchLSwitchLightOfRoomCommand);

            } else if (command.equals(switchLSwitchLightOfRoomCommand)) {
                setState(State.Success);
            }
        } else if (command.getState().equals(PHATCommand.State.Fail)) {
            bodiesAppState.runCommand(new LookAtCommand(bodyId, null));
            setState(State.Fail);
        }
    }

    @Override
    public void interruptCommand(Application app) {
        BodiesAppState bodiesAppState = app.getStateManager().getState(
                BodiesAppState.class);

        Node body = bodiesAppState.getBody(bodyId);

        if (body != null && body.getParent() != null) {
            NavMeshMovementControl nmmc = body
                    .getControl(NavMeshMovementControl.class);
            nmmc.moveTo(null);
            setState(State.Interrupted);
            return;
        }
        setState(State.Fail);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + bodyId + ",roomId="
                + roomId + ",minDistance=" + minDistance + ")";
    }

    public float getMinDistance() {
        return minDistance;
    }

    public String getBodyId() {
        return bodyId;
    }

    public String getTargetObjectId() {
        return roomId;
    }

    @PHATCommParam(mandatory = true, order = 1)
    public void setBodyId(String bodyId) {
        this.bodyId = bodyId;
    }

    @PHATCommParam(mandatory = true, order = 2)
    public void setRoomId(String targetObjectId) {
        this.roomId = targetObjectId;
    }

    @PHATCommParam(mandatory = false, order = 3)
    public void setMinDistance(float minDistance) {
        this.minDistance = minDistance;
    }

}
