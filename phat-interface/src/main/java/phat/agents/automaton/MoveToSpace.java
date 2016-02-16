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
import phat.body.commands.GoToSpaceCommand;
import phat.body.commands.SetBodyInHouseSpaceCommand;
import phat.body.commands.SetSpeedDisplacemenetCommand;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;

/**
 *
 * @author pablo
 */
public class MoveToSpace extends SimpleState implements PHATCommandListener {

    String destinyName;
    PHATCommand goToSpaceCommand;
    boolean destinyReached = false;
    float speed = -1f;
    float lastSpeed = -1f;

    public MoveToSpace(Agent agent, String name, String destinyName) {
        this(agent, name, destinyName, 0.5f);
    }

    @Override
    public void interrupt(PHATInterface phatInterface) {
        if (goToSpaceCommand != null) {
            goToSpaceCommand.setFunction(PHATCommand.Function.Interrupt);
            agent.runCommand(goToSpaceCommand);
        }
        super.interrupt(phatInterface);
    }

    public MoveToSpace(Agent agent, String name, String destinyName, float distance) {
        super(agent, 0, name);
        this.destinyName = destinyName;
    }

    @Override
    public boolean isFinished(PHATInterface phatInterface) {
        return super.isFinished(phatInterface) || destinyReached;
    }

    @Override
    public void commandStateChanged(PHATCommand command) {
        if (command == goToSpaceCommand) {
            if (command.getState().equals(PHATCommand.State.Success)) {
                destinyReached = true;
                if (lastSpeed > 0f) {
                    agent.runCommand(new SetSpeedDisplacemenetCommand(agent.getId(), lastSpeed));
                }
            }
        }
    }

    @Override
    public void simpleNextState(PHATInterface phatInterface) {
    }

    @Override
    public void initState(PHATInterface phatInterface) {
        destinyReached = false;
        if (speed > 0) {
            lastSpeed = agent.getBodiesAppState().getSpeed(agent.getId());
            agent.runCommand(new SetSpeedDisplacemenetCommand(agent.getId(), speed));
        }
        if (agent.isInTheWorld()) {
            goToSpaceCommand = new GoToSpaceCommand(agent.getId(), destinyName, this);
        } else {
            goToSpaceCommand = new SetBodyInHouseSpaceCommand(agent.getId(), "House1", "WorldEntry1", this);
        }
        agent.runCommand(goToSpaceCommand);
    }

    public float getSpeed() {
        return speed;
    }

    public MoveToSpace setSpeed(float speed) {
        this.speed = speed;
        return this;
    }

    public void setDestinyName(String destinyName) {
        this.destinyName = destinyName;
    }
}
