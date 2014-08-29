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
package phat.agents.automaton.uses;

import phat.PHATInterface;
import phat.agents.Agent;
import phat.agents.automaton.SimpleState;
import phat.body.commands.AlignWithCommand;
import phat.body.commands.CloseObjectCommand;
import phat.body.commands.GoCloseToObjectCommand;
import phat.body.commands.GoToCommand;
import phat.body.commands.OpenObjectCommand;
import phat.body.commands.SitDownCommand;
import phat.body.commands.StandUpCommand;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.util.Lazy;
import phat.util.SpatialFactory;
import phat.util.SpatialUtils;

/**
 *
 * @author pablo
 */
public class UseCommonObjectAutomaton extends SimpleState implements PHATCommandListener {

    boolean useObjfinished;
    boolean tapClosed = false;
    boolean fail = false;
    private String objId;
    GoCloseToObjectCommand goCloseToObj;

    public UseCommonObjectAutomaton(Agent agent, String objId) {
        super(agent, 0, "UseWCAutomaton-" + objId);
        this.objId = objId;
    }

    @Override
    public boolean isFinished(PHATInterface phatInterface) {
        if (tapClosed) {
            return true;
        }
        useObjfinished = super.isFinished(phatInterface) || fail;
        if (useObjfinished) {
            agent.runCommand(new CloseObjectCommand(agent.getId(), objId));
            tapClosed = true;
        }
        return useObjfinished;
    }

    @Override
    public void commandStateChanged(PHATCommand command) {
        if (command == goCloseToObj && command.getState().equals(PHATCommand.State.Success)) {
            agent.runCommand(new AlignWithCommand(agent.getId(), objId));
            agent.runCommand(new OpenObjectCommand(agent.getId(), objId));
        }
        if (command.getState().equals(PHATCommand.State.Fail)) {
            fail = true;
        }
    }
    
    @Override
    public void interrupt() {
    	if(goCloseToObj != null && goCloseToObj.getState().equals(PHATCommand.State.Running)) {
            goCloseToObj.setFunction(PHATCommand.Function.Interrupt);
            agent.runCommand(goCloseToObj);
        }
        agent.runCommand(new CloseObjectCommand(agent.getId(), objId));
        tapClosed = true;
            
    	super.interrupt();
    }
    
    @Override
    public void simpleNextState(PHATInterface phatInterface) {
    }

    @Override
    public void initState(PHATInterface phatInterface) {
        tapClosed = false;
        fail = false;
        goToUse(objId);
    }

    private void goToUse(final String obj) {
        goCloseToObj = new GoCloseToObjectCommand(agent.getId(), obj, this);
        goCloseToObj.setMinDistance(0.1f);
        agent.runCommand(goCloseToObj);
    }
}
