/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.bullet.control.ragdoll;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.control.KinematicRagdollControl;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Vector3f;
import java.util.List;

/**
 *
 * @author Pablo
 */
public class PushControl implements PhysicsTickListener {
    private List<PhysicsRigidBody> physicsRigidBodies;
    private Vector3f force;
    private KinematicRagdollControl kinematicRagdollControl;
    
    public PushControl(List<PhysicsRigidBody> physicsRigidBodies, Vector3f force, KinematicRagdollControl kinematicRagdollControl) {
        this.physicsRigidBodies = physicsRigidBodies;
        this.force = force;
        this.kinematicRagdollControl = kinematicRagdollControl;
        kinematicRagdollControl.getPhysicsSpace().addTickListener(this);
    }
    
    
    @Override
    public void prePhysicsTick(PhysicsSpace ps, float f) {
        kinematicRagdollControl.setEnabled(true);
        kinematicRagdollControl.setRagdollMode();
        for(PhysicsRigidBody prb: physicsRigidBodies) {
            prb.setLinearVelocity(force);
            //prb.applyImpulse(force.mult(prb.getMass()), Vector3f.ZERO);
        }
    }

    @Override
    public void physicsTick(PhysicsSpace ps, float f) {
        kinematicRagdollControl.getPhysicsSpace().removeTickListener(this);
    }
    
}
