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
package phat.agents.automaton;

import phat.PHATInterface;
import phat.agents.Agent;
import phat.agents.DeviceAgent;
import phat.agents.HumanAgent;
import phat.body.commands.GoCloseToSwitchLightOfRoom;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.structures.houses.commands.SwitchLightOfRoomCommand;

/**
 *
 * @author pablo
 */
public class SwitchLight extends SimpleState implements PHATCommandListener {

    PHATCommand command;
    private final String roomName;
    private final boolean on;
    private boolean done;
    
    public SwitchLight(Agent agent, int priority, String name, String roomName, boolean on) {
        super(agent, priority, name);
        this.roomName = roomName;
        this.on = on;
        this.done = false;
    }

    public SwitchLight(Agent agent, String name, String roomName, String ONOFF) {
        this(agent, 0, name, roomName, ONOFF.equalsIgnoreCase("ON"));
    }
    
    @Override
    public boolean isFinished(PHATInterface phatInterface) {
        return super.isFinished(phatInterface) || done;
    }

    @Override
    public void simpleNextState(PHATInterface phatInterface) {
        if (command == null) {
            if (agent instanceof DeviceAgent) {
                command = new SwitchLightOfRoomCommand("House1", roomName, on);
                agent.runCommand(command);
                this.done = true;
            } else if(agent instanceof HumanAgent) {
                command = new GoCloseToSwitchLightOfRoom(agent.getId(), roomName, on, this);
                agent.runCommand(command);
            }
        }
    }

    @Override
    public void initState(PHATInterface phatInterface) {
        this.done = false;
    }

    @Override
    public void commandStateChanged(PHATCommand command) {
        if (command == this.command
                && command.getState().equals(PHATCommand.State.Success)) {
            done = true;
        }
    }
}
