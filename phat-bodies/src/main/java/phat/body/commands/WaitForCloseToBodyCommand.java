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
import phat.body.control.physics.PHATCharacterControl;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.commands.PHATCommand.State;

/**
 * 
 * @author pablo
 */
public class WaitForCloseToBodyCommand extends PHATCommand implements
AutonomousControlListener {

	private String bodyId;
	private String targetBodyId;

	public WaitForCloseToBodyCommand(String bodyId, String targetBodyId,
			PHATCommandListener listener) {
		super(listener);
		this.bodyId = bodyId;
		this.targetBodyId = targetBodyId;
		logger.log(Level.INFO, "New Command: {0}", new Object[] { this });
	}

	public WaitForCloseToBodyCommand(String bodyId, String targetBodyId) {
		this(bodyId, targetBodyId, null);
	}
    public float getDistanceToTarget(Vector3f l1, Vector3f l2) {
        Vector3f loc = l1.clone();
        Vector3f target = l2.clone();
        if (Math.abs(target.getY() - loc.getY()) < 2f) {
            return target.setY(0f).distance(loc.setY(0f));
        }
        return target.distance(loc);
    }
	
	
	@Override
	public void runCommand(Application app) {
		BodiesAppState bodiesAppState = app.getStateManager().getState(
				BodiesAppState.class);

		final Node body = bodiesAppState.getBody(bodyId);

		if (body != null && body.getParent() != null) {
			final Node targetBody = bodiesAppState.getBody(targetBodyId);

			new Thread(){
				public void run(){
					while (getDistanceToTarget(targetBody
							.getControl(NavMeshMovementControl.class).
							getLocation(),body.getControl(
									NavMeshMovementControl.class).getLocation())>=0.5 || getState().equals(phat.commands.PHATCommand.State.Interrupted)){
						try {
							Thread.currentThread().sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					setState(phat.commands.PHATCommand.State.Success);
				}
			}.start();
		}

	}

	@Override
	public void interruptCommand(Application app) {
		BodiesAppState bodiesAppState = app.getStateManager().getState(
				BodiesAppState.class);
		setState(State.Interrupted);
	}

	@Override
	public void destinationReached(Vector3f destination) {
		setState(State.Success);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + bodyId + ",targetBodyId="
				+ targetBodyId + ")";
	}
}
