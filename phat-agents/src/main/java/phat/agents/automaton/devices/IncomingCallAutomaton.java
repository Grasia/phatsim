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
import phat.agents.commands.IncomingCallCommand;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;

/**
 *
 * @author pablo
 */
public class IncomingCallAutomaton extends SimpleState implements PHATCommandListener {

    String deviceId;
    String phoneNumber;
    boolean done = false;
    IncomingCallCommand incomingCallCommand;
    
     public IncomingCallAutomaton(Agent agent, String name) {
        super(agent, 0, name);
    }

    @Override
    public boolean isFinished(PHATInterface phatInterface) {
        return super.isFinished(phatInterface) || done;
    }
    
    @Override
    public void interrupt(PHATInterface phatInterface) {
    	if(incomingCallCommand != null && incomingCallCommand.getState().equals(PHATCommand.State.Running)) {
            incomingCallCommand.setFunction(PHATCommand.Function.Interrupt);
            agent.runCommand(incomingCallCommand);
        }
            
    	super.interrupt(phatInterface);
    }
    
    @Override
    public void commandStateChanged(PHATCommand command) {
        if (command == incomingCallCommand
                && (command.getState().equals(PHATCommand.State.Success) ||
                		command.getState().equals(PHATCommand.State.Fail))) {
            done = true;
        }
    }

    @Override
    public void simpleNextState(PHATInterface phatInterface) {
    }

    @Override
    public void initState(PHATInterface phatInterface) {
        done = false;
        call(phatInterface);
    }

    private void call(PHATInterface phatInterface) {
        if(deviceId == null) {
            deviceId = agent.getId();
        }
        incomingCallCommand = new IncomingCallCommand(deviceId, phoneNumber, this);
        agent.runCommand(incomingCallCommand);
    }

    public String getDeviceId() {
        return deviceId;
    }

    public IncomingCallAutomaton setDeviceId(String deviceId) {
        this.deviceId = deviceId;
        return this;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public IncomingCallAutomaton setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }
}
