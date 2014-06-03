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
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

import java.util.logging.Level;

import phat.body.BodiesAppState;
import phat.body.control.navigation.AutonomousControlListener;
import phat.body.control.navigation.navmesh.NavMeshMovementControl;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.commands.PHATCommand.State;
import phat.structures.houses.HouseAppState;
import phat.util.Lazy;

/**
 *
 * @author pablo
 */
public class GoToCommand extends PHATCommand implements AutonomousControlListener {

    private String bodyId;
    private Lazy<Vector3f> destiny;
    private float minDistance;
    
    public GoToCommand(String bodyId, Lazy<Vector3f> destiny, PHATCommandListener listener) {
        super(listener);
        this.bodyId = bodyId;
        this.destiny = destiny;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }
    
    public GoToCommand(String bodyId, Lazy<Vector3f> destiny) {
        this(bodyId, destiny, null);
    }
    
    @Override
    public void runCommand(Application app) {
        BodiesAppState bodiesAppState = app.getStateManager().getState(BodiesAppState.class);
        
        Node body = bodiesAppState.getAvailableBodies().get(bodyId);

        if (body != null && body.getParent() != null) {
                NavMeshMovementControl nmmc = body.getControl(NavMeshMovementControl.class);
                if(nmmc != null) {
                    nmmc.setMinDistance(minDistance);
                    boolean reachable = nmmc.moveTo(destiny.getLazy());
                    if(reachable) {
                        nmmc.setListener(this);
                        return;
                    }
            }
        }
        setState(State.Fail);
    }
    
    @Override
	public void interruptCommand(Application app) {
		BodiesAppState bodiesAppState = app.getStateManager().getState(
				BodiesAppState.class);

		Node body = bodiesAppState.getAvailableBodies().get(bodyId);

		if (body != null && body.getParent() != null) {
			NavMeshMovementControl nmmc = body
					.getControl(NavMeshMovementControl.class);
			nmmc.moveTo(null);
			setState(State.Interrupted);
			return;
		}
		setState(State.Fail);
	}
    
    @Override
    public String toString() {
        return getClass().getSimpleName()+"("+bodyId+", "+destiny.getLazy()+")";
    }

    @Override
    public void destinationReached(Vector3f destination) {
        setState(State.Success);
    }

    public float getMinDistance() {
        return minDistance;
    }

    public void setMinDistance(float minDistance) {
        this.minDistance = minDistance;
    }
}
