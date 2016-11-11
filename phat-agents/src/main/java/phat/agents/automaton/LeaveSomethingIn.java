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
import phat.devices.commands.SetDeviceOnFurnitureCommand;
import phat.structures.houses.HouseAppState;

/**
 *
 * @author pablo
 */
public class LeaveSomethingIn extends SimpleState implements PHATCommandListener {

    SetDeviceOnFurnitureCommand setObjectOnCommand;
    GoCloseToObjectCommand goCloseToObjectCommand;
    String objectId;
    String furniture;
    boolean left = false;

    public LeaveSomethingIn(Agent agent, String name, String objectId, String furniture) {
        super(agent, 0, name);
        this.objectId = objectId;
        this.furniture = furniture;
    }
    
    public LeaveSomethingIn(Agent agent, String objectId, String furniture) {
        this(agent, "LeaveSomethingIn", objectId, furniture);
    }

    @Override
    public void interrupt(PHATInterface phatInterface) {
        if (setObjectOnCommand != null && setObjectOnCommand.getState().equals(PHATCommand.State.Running)) {
            setObjectOnCommand.setFunction(PHATCommand.Function.Interrupt);
            agent.runCommand(setObjectOnCommand);
        }

        super.interrupt(phatInterface);
    }

    @Override
    public boolean isFinished(PHATInterface phatInterface) {
        return super.isFinished(phatInterface) || left;
    }

    @Override
    public void commandStateChanged(PHATCommand command) {
        if (command.getState().equals(PHATCommand.State.Success)) {
            if (command == goCloseToObjectCommand) {
                String loc = agent.getAgentsAppState().getHouseAppState().getHouse("House1").getClosestPlaceToPutThings(agent.getLocation(), furniture);
                if(loc != null) {
                    setObjectOnCommand = new SetDeviceOnFurnitureCommand(objectId, "House1", furniture, this);
                    setObjectOnCommand.setPlaceId(loc);
                    agent.runCommand(setObjectOnCommand);
                } else {
                    left = true;
                }
            } else if (command == setObjectOnCommand) {
                left = true;
            }
        }
    }

    @Override
    public void simpleNextState(PHATInterface phatInterface) {
    }

    @Override
    public void initState(PHATInterface phatInterface) {
        left = false;
        goCloseToObjectCommand = new GoCloseToObjectCommand(agent.getId(), furniture, this);
        agent.runCommand(goCloseToObjectCommand);        
    }
}
