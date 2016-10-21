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
import phat.body.commands.WaitForCloseToBodyCommand;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;

/**
 *
 * @author pablo
 */
public class WaitForCloseToBodyAutomaton extends SimpleState implements PHATCommandListener {

    String destinyBodyName;

    public String getDestinyBodyName() {
        return destinyBodyName;
    }

    public PHATCommand getMoveToBodyCommand() {
        return waitForBodyClose;
    }

    public boolean isDestinyReached() {
        return destinyReached;
    }
    PHATCommand waitForBodyClose;
    boolean destinyReached = false;
    float speed = -1f;

    public WaitForCloseToBodyAutomaton(Agent agent, String name, String destinyBodyName) {
        this(agent, name, destinyBodyName, 1f);
    }

    @Override
    public void interrupt(PHATInterface phatInterface) {
        super.interrupt(phatInterface);
        waitForBodyClose.setFunction(PHATCommand.Function.Interrupt);
        agent.runCommand(waitForBodyClose);
    }

    public WaitForCloseToBodyAutomaton(Agent agent, String name, String destinyBodyName, float distance) {
        super(agent, 0, name);
        this.destinyBodyName = destinyBodyName;
    }

    @Override
    public boolean isFinished(PHATInterface phatInterface) {
        return super.isFinished(phatInterface) || destinyReached;
    }

    @Override
    public void commandStateChanged(PHATCommand command) {
        if (command == waitForBodyClose) {
            if (command.getState().equals(PHATCommand.State.Success)
                    || command.getState().equals(PHATCommand.State.Interrupted)) {
                destinyReached = true;
            }
        }
    }

    @Override
    public void simpleNextState(PHATInterface phatInterface) {
    }

    @Override
    public void initState(PHATInterface phatInterface) {
        waitForBodyClose = new WaitForCloseToBodyCommand(agent.getId(), destinyBodyName, this);
        agent.runCommand(waitForBodyClose);
    }
}
