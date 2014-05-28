/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.body.commands;

import com.jme3.animation.AnimControl;
import com.jme3.app.Application;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.AbstractPhysicsControl;
import com.jme3.bullet.control.KinematicRagdollControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;

import java.util.logging.Level;

import phat.body.BodiesAppState;
import phat.body.control.animation.BasicCharacterAnimControl;
import phat.body.control.navigation.StraightMovementControl;
import phat.body.control.physics.PHATCharacterControl;
import phat.body.control.physics.ragdoll.BVHRagdollPreset;
import phat.bullet.control.ragdoll.SimulateTripOver;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.commands.PHATCommand.State;
import phat.structures.houses.HouseAppState;
import phat.util.PhysicsUtils;
import phat.util.SpatialUtils;

/**
 *
 * @author pablo
 */
public class FallDownCommand extends PHATCommand {

    private String bodyId;

    public FallDownCommand(String bodyId, PHATCommandListener listener) {
        super(listener);
        this.bodyId = bodyId;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }

    public FallDownCommand(String bodyId) {
        this(bodyId, null);
    }

    @Override
    public void runCommand(Application app) {
        BodiesAppState bodiesAppState = app.getStateManager().getState(BodiesAppState.class);
        BulletAppState bulletAppState = app.getStateManager().getState(BulletAppState.class);

        Node body = bodiesAppState.getAvailableBodies().get(bodyId);

        if (body != null && body.getParent() != null) {
            SpatialUtils.printControls(body);
            KinematicRagdollControl krc = body.getControl(KinematicRagdollControl.class);
            if (krc == null) {
                BVHRagdollPreset preset = new BVHRagdollPreset();
                krc = new KinematicRagdollControl(preset, 0.5f);
                body.addControl(krc);
                bulletAppState.getPhysicsSpace().add(krc);
            }
            krc.setRagdollMode();
            if (!krc.isEnabled()) {
                krc.setEnabled(true);
            }

            PHATCharacterControl cc = body.getControl(PHATCharacterControl.class);
            cc.setEnabled(false);
            //body.getControl(BasicCharacterAnimControl.class).setEnabled(false);

            StraightMovementControl smc = body.getControl(StraightMovementControl.class);
            if (smc != null) {
                body.removeControl(smc);
            }
            
            BasicCharacterAnimControl bcac = body.getControl(BasicCharacterAnimControl.class);
            if(bcac != null) {
                bcac.setManualAnimation(null, null);
            }
            
            SpatialUtils.printControls(body);

            //SimulateTripOver sto = new SimulateTripOver(body);
            //sto.activate();
            //krc.setEnabled(true);
            //PhysicsUtils.setHighPhysicsPrecision(app.getStateManager().getState(HouseAppState.class).getHouse().getRootNode());
            setState(State.Success);
            return;
        }
        setState(State.Fail);
    }

    @Override
    public void interruptCommand(Application app) {
        setState(State.Fail);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + bodyId + ")";
    }
}
