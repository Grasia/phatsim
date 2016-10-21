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
package phat.agents.commands;

import com.jme3.app.Application;
import phat.agents.AgentsAppState;
import phat.agents.events.actuators.EventLauncher;
import phat.commands.PHATCommandListener;
import phat.devices.DevicesAppState;

public class ActivateActuatorEventsLauncherCommand extends PHATAgentCommand {
    
    public ActivateActuatorEventsLauncherCommand(PHATCommandListener listener) {
        super(listener);
    }

    @Override
    public void runCommand(Application app) {
        AgentsAppState agentsAppState = app.getStateManager().getState(AgentsAppState.class);
        DevicesAppState devicesAppState = app.getStateManager().getState(DevicesAppState.class);
        EventLauncher el = new EventLauncher(agentsAppState, devicesAppState);
        
        setState(State.Success);
    }

    @Override
    public void interruptCommand(Application app) {
        setState(State.Interrupted);
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "()";
    }
}
