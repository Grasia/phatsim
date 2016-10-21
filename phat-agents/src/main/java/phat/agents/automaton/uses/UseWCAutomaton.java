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

import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

import phat.PHATInterface;
import phat.agents.Agent;
import phat.agents.automaton.SimpleState;
import phat.body.commands.CloseObjectCommand;
import phat.body.commands.GoToCommand;
import phat.body.commands.OpenObjectCommand;
import phat.body.commands.SitDownCommand;
import phat.body.commands.StandUpCommand;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.util.Lazy;
import phat.util.SpatialFactory;
import phat.util.SpatialUtils;

/**
 *
 * @author pablo
 */
public class UseWCAutomaton extends SimpleState implements PHATCommandListener {

    boolean useWCfinished;
    boolean tapClosed = false;
    boolean wcBusy = false;
    private String wcId;
    SitDownCommand sitDownCommand;

    public UseWCAutomaton(Agent agent, String wcId) {
        super(agent, 0, "UseWCAutomaton-" + wcId);
        this.wcId = wcId;
    }

    @Override
    public boolean isFinished(PHATInterface phatInterface) {
        if (tapClosed) {
            return true;
        }
        useWCfinished = super.isFinished(phatInterface);
        if (useWCfinished) {
            agent.runCommand(new StandUpCommand(agent.getId()));
            agent.runCommand(new OpenObjectCommand(agent.getId(), wcId));
            tapClosed = true;
        }
        return useWCfinished;
    }

    @Override
    public void commandStateChanged(PHATCommand command) {
        if (command.getState().equals(PHATCommand.State.Fail)) {
            wcBusy = true;
        }
    }

    @Override
    public void simpleNextState(PHATInterface phatInterface) {
        if (wcBusy) {
            finishCondition.automatonReset(automatonFahter);
            if (sitDownCommand.existsANearestFreeSeat(wcId, agent.getId())) {
                wcBusy = false;
                useWC();
            }
        }
    }

    @Override
    public void initState(PHATInterface phatInterface) {
        useWC();
    }

    private void useWC() {
        sitDownCommand = new SitDownCommand(agent.getId(), wcId, this);
        agent.runCommand(sitDownCommand);
    }
}
