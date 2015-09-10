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
import phat.body.commands.AlignWithCommand;
import phat.body.commands.CloseObjectCommand;
import phat.body.commands.GoCloseToObjectCommand;
import phat.body.commands.OpenObjectCommand;
import phat.body.commands.StandUpCommand;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;

/**
 *
 * @author pablo
 */
public class CloseObjectAutomaton extends SimpleState implements PHATCommandListener {

    boolean closeObjFinished;
    boolean objClosed = false;
    boolean fail = false;
    private String obj;
    GoCloseToObjectCommand goCloseToObjectCommand;
    AlignWithCommand alignWithCommand;
    CloseObjectCommand closeObj;

    public CloseObjectAutomaton(Agent agent, String obj) {
        super(agent, 0, "UseWCAutomaton-" + obj);
        this.obj = obj;
    }

    @Override
    public boolean isFinished(PHATInterface phatInterface) {
        if (objClosed) {
            return true;
        }
        closeObjFinished = super.isFinished(phatInterface) || fail;
        if (closeObjFinished) {
            agent.runCommand(new StandUpCommand(agent.getId()));
            agent.runCommand(new OpenObjectCommand(agent.getId(), obj));
            objClosed = true;
        }
        return closeObjFinished;
    }

    @Override
    public void commandStateChanged(PHATCommand command) {
        if (command.getState().equals(PHATCommand.State.Fail)) {
            fail = true;
        } else if (goCloseToObjectCommand == command
                && goCloseToObjectCommand.getState().equals(PHATCommand.State.Success)) {
            align();
        } else if (alignWithCommand == command
                && alignWithCommand.getState().equals(PHATCommand.State.Success)) {
            closeObj();
            objClosed = true;
        }
    }

    @Override
    public void simpleNextState(PHATInterface phatInterface) {
    }

    @Override
    public void initState(PHATInterface phatInterface) {
        goToObj();
    }

    private void goToObj() {
        goCloseToObjectCommand = new GoCloseToObjectCommand(agent.getId(), obj, this);
        goCloseToObjectCommand.setMinDistance(0.1f);
        agent.runCommand(goCloseToObjectCommand);
    }

    private void closeObj() {
        closeObj = new CloseObjectCommand(agent.getId(), obj, this);
        agent.runCommand(closeObj);
    }
    
    private void align() {
        alignWithCommand = new AlignWithCommand(agent.getId(), obj, this);
        agent.runCommand(alignWithCommand);
    }
}