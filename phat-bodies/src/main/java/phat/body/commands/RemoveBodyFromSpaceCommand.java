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
package phat.body.commands;

import com.jme3.app.Application;
import com.jme3.scene.Node;
import java.util.logging.Level;
import phat.body.BodiesAppState;
import phat.commands.PHATCommParam;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandAnn;

/**
 *
 * @author pablo
 */
@PHATCommandAnn(name="RemoveBodyFromSpace", type="body", debug = false)
public class RemoveBodyFromSpaceCommand extends PHATCommand {

    private String bodyId;

    public RemoveBodyFromSpaceCommand() {
    }
    
    public RemoveBodyFromSpaceCommand(String bodyId) {
        super(null);
        this.bodyId = bodyId;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }
    
    @Override
    public void runCommand(Application app) {
        BodiesAppState bodiesAppState = app.getStateManager().getState(BodiesAppState.class);
        Node body = bodiesAppState.getBody(bodyId);

        if (body != null && body.getParent() != null) {
            body.removeFromParent();
        }
        setState(State.Success);
    }
    
    @Override
	public void interruptCommand(Application app) {
    	setState(State.Fail);
	}
    
    @Override
    public String toString() {
        return getClass().getSimpleName()+"("+bodyId+")";
    }

    @PHATCommParam(mandatory=true, order=1)
    public void setBodyId(String bodyId) {
        this.bodyId = bodyId;
    }
}
