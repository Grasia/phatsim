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

/**
 *
 * @author pablo
 */
public class VibrateDeviceAutomaton extends SimpleState implements PHATCommandListener {

    String deviceId;
    long millis = 1000;
    boolean done = false;
    VibrateDeviceCommand vibrateDeviceCommand;

    public VibrateDeviceAutomaton(Agent agent) {
        this(agent, agent.getId());
    }

    public VibrateDeviceAutomaton(Agent agent, String name) {
        super(agent, 0, name);
    }
    
    @Override
    public boolean isFinished(PHATInterface phatInterface) {
        return  super.isFinished(phatInterface) || done;
    }

    @Override
    public void interrupt(PHATInterface phatInterface) {
        if (vibrateDeviceCommand != null && vibrateDeviceCommand.getState().equals(PHATCommand.State.Running)) {
            vibrateDeviceCommand.setFunction(PHATCommand.Function.Interrupt);
            agent.runCommand(vibrateDeviceCommand);
        }

        super.interrupt(phatInterface);
    }

    @Override
    public void commandStateChanged(PHATCommand command) {
        if (command == vibrateDeviceCommand
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
        vibrate(phatInterface);
    }

    private void vibrate(PHATInterface phatInterface) {
        if(deviceId == null) {
            deviceId = agent.getId();
        }
        vibrateDeviceCommand = new VibrateDeviceCommand(deviceId, millis, this);
        agent.runCommand(vibrateDeviceCommand);
    }

    public VibrateDeviceAutomaton setMillis(long millis) {
        this.millis = millis;
        return this;
    }
    
    public long getMillis() {
        return this.millis;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public VibrateDeviceAutomaton setDeviceId(String deviceId) {
        this.deviceId = deviceId;
        return this;
    }
}
