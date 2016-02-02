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
import phat.body.commands.PlayBodyAnimationCommand;
import phat.body.control.animation.BasicCharacterAnimControl;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.world.PHATCalendar;

/**
 *
 * @author pablo
 */
public class DrinkAutomaton extends SimpleState implements PHATCommandListener {

    boolean drinking = false;
    PlayBodyAnimationCommand playAnimCommand;
    PHATCalendar lastDrink = null;
    float drinkRate = 10f;
    String beverage;
    
     public DrinkAutomaton(Agent agent) {
        super(agent, 0, "DrinkAutomaton");
    }
     
    public DrinkAutomaton(Agent agent, String name, String beverage) {
        super(agent, 0, name);
        this.beverage = beverage;
    }

    @Override
    public boolean isFinished(PHATInterface phatInterface) {
        return super.isFinished(phatInterface) && !drinking;
    }
    
    @Override
    public void interrupt() {
    	if(playAnimCommand != null && playAnimCommand.getState().equals(PHATCommand.State.Running)) {
            playAnimCommand.setFunction(PHATCommand.Function.Interrupt);
            agent.runCommand(playAnimCommand);
        }
            
    	super.interrupt();
    }
    
    @Override
    public void commandStateChanged(PHATCommand command) {
        if (command == playAnimCommand
                && (command.getState().equals(PHATCommand.State.Success) ||
                		command.getState().equals(PHATCommand.State.Fail))) {
            drinking = false;
        }
    }

    @Override
    public void simpleNextState(PHATInterface phatInterface) {
        if(!drinking) {
            int secs = (int)lastDrink.spentTimeTo(phatInterface.getSimTime());
            if(secs >= drinkRate) {
                drink(phatInterface);
            }
        }
    }

    @Override
    public void initState(PHATInterface phatInterface) {
        drinking = false;
        lastDrink = null;
        drink(phatInterface);
    }

    private void drink(PHATInterface phatInterface) {
        drinking = true;
        playAnimCommand = new PlayBodyAnimationCommand(agent.getId(), 
                BasicCharacterAnimControl.AnimName.DrinkStanding.name(), this);
        agent.runCommand(playAnimCommand);
        lastDrink = (PHATCalendar) phatInterface.getSimTime().clone();
    }
}
