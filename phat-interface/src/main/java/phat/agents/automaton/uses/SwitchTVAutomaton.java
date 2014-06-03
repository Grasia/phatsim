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

import phat.PHATInterface;
import phat.agents.Agent;
import phat.agents.automaton.SimpleState;
import phat.agents.automaton.conditions.TimerFinishedCondition;
import phat.body.commands.GoCloseToObjectCommand;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.config.DeviceConfigurator;
import phat.devices.commands.SwitchTVCommand;

/**
 *
 * @author Pablo
 */
public class SwitchTVAutomaton extends SimpleState implements PHATCommandListener {

    private String tvId;
    private boolean on;
    GoCloseToObjectCommand goCloseToObj;
    DeviceConfigurator deviceConfigurator;
    SwitchTVCommand switchTVCommandd;
    boolean done = false;

    public SwitchTVAutomaton(Agent agent, String tvId, boolean on) {
        super(agent, 0, "SwitchTVAutomaton");
        this.tvId = tvId;
        this.on = on;
    }

    @Override
    public void simpleNextState(PHATInterface phatInterface) {
    }

    @Override
    public boolean isFinished(PHATInterface phatInterface) {
        return done;
    }

    @Override
    public void commandStateChanged(PHATCommand command) {
        if (command == goCloseToObj
                && command.getState().equals(PHATCommand.State.Success)) {
            switchTVCommandd = new SwitchTVCommand(tvId, on, this);
            deviceConfigurator.runCommand(switchTVCommandd);            
            done = true;
        }
    }

    @Override
    public void initState(PHATInterface phatInterface) {
        goCloseToObj = new GoCloseToObjectCommand(agent.getId(), tvId, this);
        goCloseToObj.setMinDistance(0.1f);
        agent.runCommand(goCloseToObj);
        deviceConfigurator = phatInterface.getDevicesConfig();
    }
}
