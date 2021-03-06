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
import phat.agents.automaton.conditions.TimerFinishedCondition;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.server.commands.PressOnScreen;

/**
 *
 * @author Pablo
 */
public class PressOnScreenXYAutomaton extends SimpleState implements PHATCommandListener {

    private String smartphoneId;
    private int x;
    private int y;

    PressOnScreen pressOnScreenCommand;
    boolean done = false;
    
    public PressOnScreenXYAutomaton(Agent agent, String name, String smartphoneId) {
        super(agent, 0, name);
        this.smartphoneId = smartphoneId;
    }
    
    public PressOnScreenXYAutomaton(Agent agent, String name, String smartphoneId, int x, int y) {
        super(agent, 0, name);
        this.smartphoneId = smartphoneId;
        this.x = x;
        this.y = y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getSmartphoneId() {
        return smartphoneId;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public void simpleNextState(PHATInterface phatInterface) {
        
    }

    @Override
    public boolean isFinished(PHATInterface phatInterface) {
        return super.isFinished(phatInterface) && done;
    }
    
    @Override
    public void commandStateChanged(PHATCommand command) {
        if (command == pressOnScreenCommand
                && (
                		command.getState().equals(PHATCommand.State.Success) ||
                		command.getState().equals(PHATCommand.State.Fail))) {
            done = true;
        }
    }

    @Override
    public void initState(PHATInterface phatInterface) {
        setFinishCondition(new TimerFinishedCondition(0, 0, 1));
        pressOnScreenCommand = new PressOnScreen(smartphoneId, x, y, this);
        phatInterface.getServerConfig().runCommand(pressOnScreenCommand);
    }
}
