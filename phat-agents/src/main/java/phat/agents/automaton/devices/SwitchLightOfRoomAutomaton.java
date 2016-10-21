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
package phat.agents.automaton.devices;

import phat.agents.automaton.*;
import phat.PHATInterface;
import phat.agents.Agent;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.devices.commands.VibrateDeviceCommand;
import phat.structures.houses.commands.SwitchLightOfRoomCommand;

/**
 *
 * @author pablo
 */
public class SwitchLightOfRoomAutomaton extends SimpleState implements PHATCommandListener {

    String deviceId;
    String roomName;
    boolean onOff;
    boolean done = false;
    SwitchLightOfRoomCommand switchLightRoomCommand;

    public SwitchLightOfRoomAutomaton(Agent agent) {
        this(agent, agent.getId());
    }

    public SwitchLightOfRoomAutomaton(Agent agent, String name) {
        super(agent, 0, name);
    }
    
    @Override
    public boolean isFinished(PHATInterface phatInterface) {
        return  super.isFinished(phatInterface) || done;
    }

    @Override
    public void interrupt(PHATInterface phatInterface) {
        if (switchLightRoomCommand != null && switchLightRoomCommand.getState().equals(PHATCommand.State.Running)) {
            switchLightRoomCommand.setFunction(PHATCommand.Function.Interrupt);
            agent.runCommand(switchLightRoomCommand);
        }

        super.interrupt(phatInterface);
    }

    @Override
    public void commandStateChanged(PHATCommand command) {
        if (command == switchLightRoomCommand
                && (command.getState().equals(PHATCommand.State.Success)
                || command.getState().equals(PHATCommand.State.Fail))) {
            done = true;
        }
    }

    @Override
    public void simpleNextState(PHATInterface phatInterface) {
    }
    
    @Override
    public void initState(PHATInterface phatInterface) {
        done = false;
        switchLights(phatInterface);
    }

    private void switchLights(PHATInterface phatInterface) {
        if(deviceId == null) {
            deviceId = agent.getId();
        }
        switchLightRoomCommand = new SwitchLightOfRoomCommand("House1", roomName, onOff, this);
        agent.getAgentsAppState().getHouseAppState().runCommand(switchLightRoomCommand);
    }

    public SwitchLightOfRoomAutomaton setRoomName(String roomName) {
        this.roomName = roomName;
        return this;
    }
    
    public SwitchLightOfRoomAutomaton setOnOff(String onOff) {
        this.onOff = onOff.equals("ON");
        return this;
    }

    public String getRoomName() {
        return roomName;
    }

    public boolean isOnOff() {
        return onOff;
    }
    
    public String getDeviceId() {
        return deviceId;
    }

    public SwitchLightOfRoomAutomaton setDeviceId(String deviceId) {
        this.deviceId = deviceId;
        return this;
    }
}
