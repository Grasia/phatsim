package phat.body.commands;

import com.jme3.app.Application;
import com.jme3.scene.Node;

import java.util.logging.Level;

import phat.body.BodiesAppState;
import phat.body.control.animation.AnimFinishedListener;
import phat.body.control.animation.BasicCharacterAnimControl;
import phat.body.control.navigation.RandomWalkControl;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.commands.PHATCommand.State;

/**
 *
 * @author pablo
 */
public class RandomWalkingCommand extends PHATCommand implements
        AnimFinishedListener {

    private String bodyId;
    private boolean enabled;

    public RandomWalkingCommand(String bodyId, boolean enabled) {
        this(bodyId, enabled, null);
    }

    public RandomWalkingCommand(String bodyId, boolean enabled, PHATCommandListener listener) {
        super(listener);
        this.bodyId = bodyId;
        this.enabled = enabled;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }

    @Override
    public void runCommand(Application app) {
        BodiesAppState bodiesAppState = app.getStateManager().getState(
                BodiesAppState.class);

        Node body = bodiesAppState.getAvailableBodies().get(bodyId);

        if (body != null && body.getParent() != null) {
            if(enabled) {
                body.addControl(new RandomWalkControl());
            } else {
                RandomWalkControl rwc = body.getControl(RandomWalkControl.class);
                if(rwc != null) {
                    body.removeControl(rwc);
                }
                
            }
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
            bcac.setManualAnimation(null, null);
            setState(State.Interrupted);
            return;
        }
        setState(State.Fail);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + bodyId + ",enabled="
                + enabled + ")";
    }

    @Override
    public void animFinished(BasicCharacterAnimControl.AnimName animationName) {
        System.out.println("Animation finished = " + animationName.name());
        setState(State.Success);
    }
}
