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
package phat.structures.houses.commands;

import com.jme3.app.Application;
import phat.commands.PHATCommParam;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandAnn;
import phat.commands.PHATCommandListener;
import phat.structures.houses.House;
import phat.structures.houses.HouseAppState;

/**
 *
 * @author pablo
 */
@PHATCommandAnn(name="SwitchLight", type="env", debug = false)
public class SwitchLightOfRoomCommand extends PHATCommand {

    boolean on;
    String roomName;
    String houseName = "House1";

    public SwitchLightOfRoomCommand() {
    }

    public SwitchLightOfRoomCommand(String houseName, String roomName, boolean on) {
        this(houseName, roomName, on, null);
    }

    public SwitchLightOfRoomCommand(String houseName, String roomName, boolean on, PHATCommandListener l) {
        super(l);
        this.houseName = houseName;
        this.roomName = roomName;
        this.on = on;
    }

    @Override
    public void runCommand(Application app) {
        HouseAppState houseAppState = app.getStateManager().getState(HouseAppState.class);

        if (houseAppState != null) {
            House house = houseAppState.getHouse(houseName);
            if (house != null) {
                house.switchLights(roomName, on);
                setState(State.Success);
                return;
            }
        }
        setState(State.Fail);
    }

    @Override
    public void interruptCommand(Application app) {
        setState(State.Fail);
    }

    public boolean isOn() {
        return on;
    }

    public String getRoomName() {
        return roomName;
    }

    public String getHouseName() {
        return houseName;
    }

    public void setHouseName(String houseName) {
        this.houseName = "House1";
    }

    @PHATCommParam(mandatory=true, order=1)
    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    @PHATCommParam(mandatory=true, order=2)
    public void setOn(boolean on) {
        this.on = on;
    }
}
