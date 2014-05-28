/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.body.commands;

import com.jme3.app.Application;
import com.jme3.bullet.control.KinematicRagdollControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.logging.Level;
import phat.body.BodiesAppState;
import phat.body.control.animation.AnimFinishedListener;
import phat.body.control.animation.BasicCharacterAnimControl;
import phat.body.control.animation.SitDownControl;
import phat.body.control.navigation.StraightMovementControl;
import phat.body.control.physics.PHATCharacterControl;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.util.SpatialUtils;

/**
 *
 * @author pablo
 */
public class StandUpCommand extends PHATCommand implements AnimFinishedListener {

    private String bodyId;
    private Node body;
    
    public StandUpCommand(String bodyId, PHATCommandListener listener) {
        super(listener);
        this.bodyId = bodyId;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }

    public StandUpCommand(String bodyId) {
        this(bodyId, null);
    }

    KinematicRagdollControl krc;
    PHATCharacterControl cc;
    
    @Override
    public void runCommand(Application app) {
        BodiesAppState bodiesAppState = app.getStateManager().getState(BodiesAppState.class);

        body = bodiesAppState.getAvailableBodies().get(bodyId);

        if (body != null && body.getParent() != null) {
            krc = body.getControl(KinematicRagdollControl.class);
            cc = body.getControl(PHATCharacterControl.class);
            StraightMovementControl smc = body.getControl(StraightMovementControl.class);

            SitDownControl sdc = body.getControl(SitDownControl.class);
            if (sdc != null) {
                // Character is seat in a chair or something like that
                sdc.standUp();
                setState(PHATCommand.State.Success);
            } else if (krc != null && krc.isEnabled() && krc.getMode() == KinematicRagdollControl.Mode.Ragdoll) {
                BasicCharacterAnimControl bcac = body.getControl(BasicCharacterAnimControl.class);
                bcac.setEnabled(true);
                bcac.standUpAnimation(this);
                krc.blendToKinematicMode(1f);
            } else if (body.getParent().getParent().getName().equals("Seats")) {
                Vector3f accessLoc = body.getParent().getChild("Access").getWorldTranslation();
                body.removeFromParent();
                bodiesAppState.getBodiesNode().attachChild(body);
                body.setLocalTranslation(accessLoc);
                cc.setEnabled(true);
                setState(PHATCommand.State.Success);
            } else {
                setState(PHATCommand.State.Success);
            }
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + bodyId + ")";
    }

    @Override
    public void animFinished(BasicCharacterAnimControl.AnimName animationName) {
        krc.setEnabled(false);
        cc.setEnabled(true);
        cc.setWalkDirection(Vector3f.ZERO);
        cc.setViewDirection(body.getLocalRotation().getRotationColumn(2));
        setState(State.Success);
    }

    @Override
    public void interruptCommand(Application app) {
        setState(State.Fail);
    }
}