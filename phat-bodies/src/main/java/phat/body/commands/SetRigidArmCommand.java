/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.body.commands;

import com.jme3.animation.Bone;
import com.jme3.animation.SkeletonControl;
import com.jme3.app.Application;
import com.jme3.scene.Node;
import java.util.logging.Level;
import phat.body.BodiesAppState;
import phat.body.control.parkinson.LeftHandTremblingControl;
import phat.body.control.parkinson.RightHandTremblingControl;
import phat.body.control.parkinson.RigidLeftArmControl;
import phat.body.control.parkinson.RigidRightArmControl;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;

/**
 *
 * @author pablo
 */
public class SetRigidArmCommand extends PHATCommand {

    private String bodyId;
    private Boolean on;
    private Boolean left;

    public SetRigidArmCommand(String bodyId, Boolean on, Boolean left, PHATCommandListener listener) {
        super(listener);
        this.bodyId = bodyId;
        this.on = on;
        this.left = left;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }

    public SetRigidArmCommand(String bodyId, Boolean on, Boolean left) {
        this(bodyId, on, left, null);
    }

    @Override
    public void runCommand(Application app) {
        BodiesAppState bodiesAppState = app.getStateManager().getState(BodiesAppState.class);

        Node body = bodiesAppState.getAvailableBodies().get(bodyId);
        if (body != null) {
            if (on) {
                active(body);
            } else {
                desactive(body);
            }
        }
        setState(PHATCommand.State.Success);
    }

    private void setUserControlFrom(Bone bone, boolean userControl) {
        bone.setUserControl(userControl);
        for (Bone b : bone.getChildren()) {
            setUserControlFrom(b, userControl);
        }
    }

    @Override
    public void interruptCommand(Application app) {
        setState(PHATCommand.State.Fail);
    }

    private void active(Node body) {
        if (left) {
            RigidLeftArmControl htc = body.getControl(RigidLeftArmControl.class);
            if (htc == null) {
                htc = new RigidLeftArmControl();
                body.addControl(htc);
            }
        } else {
            RigidRightArmControl htc = body.getControl(RigidRightArmControl.class);
            if (htc == null) {
                htc = new RigidRightArmControl();
                body.addControl(htc);
            }
        }
    }

    private void desactive(Node body) {
        if (left) {
            LeftHandTremblingControl lhtc = body.getControl(LeftHandTremblingControl.class);
            if (lhtc != null) {
                body.removeControl(lhtc);
            }
        } else {
            RightHandTremblingControl lhtc = body.getControl(RightHandTremblingControl.class);
            if (lhtc != null) {
                body.removeControl(lhtc);
            }
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + bodyId + ", on=" + on + ", left=" + left + ")";
    }
}