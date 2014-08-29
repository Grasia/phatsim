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
public class EatAutomaton extends SimpleState implements PHATCommandListener {

    boolean eat = false;
    PlayBodyAnimationCommand playAnimCommand;
    PHATCalendar lastEat = null;
    float eatRate = 15f;
    
    public EatAutomaton(Agent agent) {
        super(agent, 0, "DrinkAutomaton");
    }

    @Override
    public boolean isFinished(PHATInterface phatInterface) {
        return super.isFinished(phatInterface) && !eat;
    }
    
    @Override
    public void commandStateChanged(PHATCommand command) {
        if (command == playAnimCommand
                && (command.getState().equals(PHATCommand.State.Success) ||
                		command.getState().equals(PHATCommand.State.Fail))) {
            eat = false;
        }
    }

    @Override
    public void simpleNextState(PHATInterface phatInterface) {
        if(!eat) {
            int secs = (int)lastEat.spentTimeTo(phatInterface.getSimTime());
            if(secs >= eatRate) {
                eat(phatInterface);
            }
        }
    }

    @Override
    public void initState(PHATInterface phatInterface) {
        eat(phatInterface);
    }

    private void eat(PHATInterface phatInterface) {
        eat = true;
        playAnimCommand = new PlayBodyAnimationCommand(agent.getId(), 
                BasicCharacterAnimControl.AnimName.EatStanding.name(), this);
        agent.runCommand(playAnimCommand);
        lastEat = (PHATCalendar) phatInterface.getSimTime().clone();
    }
}
