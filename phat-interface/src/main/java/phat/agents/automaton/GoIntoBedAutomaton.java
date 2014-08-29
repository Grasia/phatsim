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
import phat.body.commands.GoIntoBedCommand;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;

/**
 *
 * @author pablo
 */
public class GoIntoBedAutomaton extends SimpleState implements PHATCommandListener {

    GoIntoBedCommand goIntoBedCommand;
    boolean finished;
    String bedId;

    public GoIntoBedAutomaton(Agent agent, String bedId) {
        super(agent, 0, "GoIntoBedAutomaton");
        this.bedId = bedId;
    }

    @Override
    public void interrupt() {
        if (goIntoBedCommand != null && goIntoBedCommand.getState().equals(PHATCommand.State.Running)) {
            goIntoBedCommand.setFunction(PHATCommand.Function.Interrupt);
            agent.runCommand(goIntoBedCommand);
        }

        super.interrupt();
    }

    @Override
    public boolean isFinished(PHATInterface phatInterface) {
        return super.isFinished(phatInterface) || finished;
    }

    @Override
    public void commandStateChanged(PHATCommand command) {
        if (command == goIntoBedCommand
                && command.getState().equals(PHATCommand.State.Success)) {
            finished = true;
        }
    }

    @Override
    public void simpleNextState(PHATInterface phatInterface) {
    }

    @Override
    public void initState(PHATInterface phatInterface) {
        finished = false;
        goIntoBedCommand = new GoIntoBedCommand(agent.getId(), bedId, this);
        agent.runCommand(goIntoBedCommand);
    }
}
