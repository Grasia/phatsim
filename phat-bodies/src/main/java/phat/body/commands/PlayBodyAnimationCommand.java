package phat.body.commands;

import com.jme3.app.Application;
import com.jme3.scene.Node;

import java.util.logging.Level;

import phat.body.BodiesAppState;
import phat.body.control.animation.AnimFinishedListener;
import phat.body.control.animation.BasicCharacterAnimControl;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.commands.PHATCommand.State;

/**
 * 
 * @author pablo
 */
public class PlayBodyAnimationCommand extends PHATCommand implements
		AnimFinishedListener {

	private String bodyId;
	private String animationName;

	public PlayBodyAnimationCommand(String bodyId, String animationName) {
		this(bodyId, animationName, null);
	}

	public PlayBodyAnimationCommand(String bodyId, String animationName,
			PHATCommandListener listener) {
		super(listener);
		this.bodyId = bodyId;
		this.animationName = animationName;
		logger.log(Level.INFO, "New Command: {0}", new Object[] { this });
	}

	@Override
	public void runCommand(Application app) {
		BodiesAppState bodiesAppState = app.getStateManager().getState(
				BodiesAppState.class);

		Node body = bodiesAppState.getAvailableBodies().get(bodyId);

		if (body != null && body.getParent() != null) {
			BasicCharacterAnimControl bcac = body
					.getControl(BasicCharacterAnimControl.class);
			bcac.setManualAnimation(
					BasicCharacterAnimControl.AnimName.valueOf(animationName),
					this);
		}
	}

	@Override
	public void interruptCommand(Application app) {
		BodiesAppState bodiesAppState = app.getStateManager().getState(
				BodiesAppState.class);

		Node body = bodiesAppState.getAvailableBodies().get(bodyId);

		if (body != null && body.getParent() != null) {
			BasicCharacterAnimControl bcac = body
					.getControl(BasicCharacterAnimControl.class);
			bcac.setManualAnimation(null,null);
			setState(State.Interrupted);
			return;
		}
		setState(State.Fail);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + bodyId + ",animationName="
				+ animationName + ")";
	}

	@Override
	public void animFinished(BasicCharacterAnimControl.AnimName animationName) {
		System.out.println("Animation finished = " + animationName.name());
		setState(State.Success);
	}
}
