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
import phat.server.commands.*;

/**
 *
 * @author Pablo
 */
public class SlideFingerOnScreenAutomaton extends SimpleState implements PHATCommandListener {

    private String smartphoneId;
    private int xSource;
    private int ySource;
    private int xTarget;
    private int yTarget;

    SlideFingerOnScreen slideFingerOnScreenCommand;
    boolean done = false;
    
    public SlideFingerOnScreenAutomaton(Agent agent, String name, String smartphoneId) {
        super(agent, 0, name);
        this.smartphoneId = smartphoneId;
    }

    public String getSmartphoneId() {
        return smartphoneId;
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
        if (command == slideFingerOnScreenCommand
                && (
                		command.getState().equals(PHATCommand.State.Success) ||
                		command.getState().equals(PHATCommand.State.Fail))) {
            done = true;
        }
    }

    @Override
    public void initState(PHATInterface phatInterface) {
        setFinishCondition(new TimerFinishedCondition(0, 0, 1));
        int duration = 1000;
        if(getFinishCondition() instanceof TimerFinishedCondition) {
            TimerFinishedCondition tfc = (TimerFinishedCondition) getFinishCondition();
            duration = (int) tfc.getSeconds()*1000;
        }
        slideFingerOnScreenCommand = new SlideFingerOnScreen(smartphoneId, xSource, ySource, xTarget, yTarget, duration, this);
        phatInterface.getServerConfig().runCommand(slideFingerOnScreenCommand);
    }

    public int getxSource() {
        return xSource;
    }

    public void setxSource(int xSource) {
        this.xSource = xSource;
    }

    public int getySource() {
        return ySource;
    }

    public void setySource(int ySource) {
        this.ySource = ySource;
    }

    public int getxTarget() {
        return xTarget;
    }

    public void setxTarget(int xTarget) {
        this.xTarget = xTarget;
    }

    public int getyTarget() {
        return yTarget;
    }

    public void setyTarget(int yTarget) {
        this.yTarget = yTarget;
    }

    public void setSmartphoneId(String smartphoneId) {
        this.smartphoneId = smartphoneId;
    }
}
