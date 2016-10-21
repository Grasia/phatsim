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
import phat.devices.commands.TextToSpeachCommand;

/**
 *
 * @author pablo
 */
public class TextToSpeachAutomaton extends SimpleState implements PHATCommandListener {

    String deviceId;
    String message = "";
    boolean done = false;
    TextToSpeachCommand textToSpeachCommand;

    public TextToSpeachAutomaton(Agent agent, String name) {
        super(agent, 0, name);
    }

    @Override
    public boolean isFinished(PHATInterface phatInterface) {
        return super.isFinished(phatInterface) || done;
    }

    @Override
    public void interrupt(PHATInterface phatInterface) {
        if (textToSpeachCommand != null && textToSpeachCommand.getState().equals(PHATCommand.State.Running)) {
            textToSpeachCommand.setFunction(PHATCommand.Function.Interrupt);
            agent.runCommand(textToSpeachCommand);
        }

        super.interrupt(phatInterface);
    }

    @Override
    public void commandStateChanged(PHATCommand command) {
        if (command == textToSpeachCommand
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
        tts(phatInterface);
    }

    private void tts(PHATInterface phatInterface) {
        if(deviceId == null) {
            deviceId = agent.getId();
        }
        textToSpeachCommand = new TextToSpeachCommand(deviceId, message, this);
        agent.runCommand(textToSpeachCommand);
    }

    public String getDeviceId() {
        return deviceId;
    }

    public TextToSpeachAutomaton setDeviceId(String deviceId) {
        this.deviceId = deviceId;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public TextToSpeachAutomaton setMessage(String message) {
        this.message = message;
        return this;
    }
    
    
}
