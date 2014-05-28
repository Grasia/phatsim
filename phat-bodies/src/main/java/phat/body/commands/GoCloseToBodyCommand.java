/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
public class GoCloseToBodyCommand extends PHATCommand implements
		AutonomousControlListener {

	private String bodyId;
	private String targetBodyId;

	public GoCloseToBodyCommand(String bodyId, String targetBodyId,
			PHATCommandListener listener) {
		super(listener);
		this.bodyId = bodyId;
		this.targetBodyId = targetBodyId;
		logger.log(Level.INFO, "New Command: {0}", new Object[] { this });
	}

	public GoCloseToBodyCommand(String bodyId, String targetBodyId) {
		this(bodyId, targetBodyId, null);
	}

	@Override
	public void runCommand(Application app) {
		BodiesAppState bodiesAppState = app.getStateManager().getState(
				BodiesAppState.class);

		Node body = bodiesAppState.getAvailableBodies().get(bodyId);

		if (body != null && body.getParent() != null) {
			Node targetBody = bodiesAppState.getAvailableBodies().get(
					targetBodyId);
			if (targetBody != null && targetBody.getParent() != null) {
				NavMeshMovementControl nmmc = body
						.getControl(NavMeshMovementControl.class);
				if (nmmc != null) {
					PHATCharacterControl cc = targetBody
							.getControl(PHATCharacterControl.class);
					nmmc.setMinDistance(0.5f);
					boolean reachable = nmmc.moveTo(cc.getLocation());
					if (reachable) {
						nmmc.setListener(this);
						return;
					}
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
	public void destinationReached(Vector3f destination) {
		setState(State.Success);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + bodyId + ",targetBodyId="
				+ targetBodyId + ")";
	}
}
