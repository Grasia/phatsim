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
import phat.body.commands.GoCloseToObjectCommand;
import phat.body.commands.PickUpCommand;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;

/**
 *
 * @author pablo
 */
public class PickUpSomething extends SimpleState implements PHATCommandListener {

    PickUpCommand pickUpCommand;
    GoCloseToObjectCommand goCloseToObjectCommand;
    String objectId;
    boolean picked = false;
    boolean useRightHand = true;

    public PickUpSomething(Agent agent, String objectId) {
        super(agent, 0, "PickUpSomething");
        this.objectId = objectId;
    }

    @Override
    public void interrupt() {
        if (pickUpCommand != null && pickUpCommand.getState().equals(PHATCommand.State.Running)) {
            pickUpCommand.setFunction(PHATCommand.Function.Interrupt);
            agent.runCommand(pickUpCommand);
        }

        super.interrupt();
    }

    @Override
    public boolean isFinished(PHATInterface phatInterface) {
        return super.isFinished(phatInterface) || picked;
    }

    @Override
    public void commandStateChanged(PHATCommand command) {
        if (command.getState().equals(PHATCommand.State.Success)) {
            if (command == goCloseToObjectCommand) {
                PickUpCommand.Hand hand = PickUpCommand.Hand.Right;
                if (!useRightHand) {
                    hand = PickUpCommand.Hand.Left;
                }
                pickUpCommand = new PickUpCommand(agent.getId(), objectId, hand, this);
                agent.runCommand(pickUpCommand);
            } else if (command == pickUpCommand) {
                picked = true;
            }
        }
    }

    @Override
    public void simpleNextState(PHATInterface phatInterface) {
    }

    @Override
    public void initState(PHATInterface phatInterface) {
        picked = false;
        goCloseToObjectCommand = new GoCloseToObjectCommand(agent.getId(), objectId, this);
        agent.runCommand(goCloseToObjectCommand);
    }
}
