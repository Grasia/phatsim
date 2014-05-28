/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.bullet.control.ragdoll;

import com.jme3.animation.AnimControl;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author pablo
 */
public class SimulateSlip extends RagdollTransitionControl {
    
    List<PhysicsRigidBody> objectsToPush = new ArrayList<PhysicsRigidBody>();
    float frictionFactor = 0f;
    
    public SimulateSlip(Node model) {
        super(model);
    }
    
    @Override
    public void initBody() {
        objectsToPush.add(kinematicRagdollControl.getBoneRigidBody("LeftToeBase"));
        objectsToPush.add(kinematicRagdollControl.getBoneRigidBody("LeftFoot"));
        objectsToPush.add(kinematicRagdollControl.getBoneRigidBody("LeftToeBase"));
        
        
        /*
        kinematicRagdollControl.getBoneRigidBody("RightToeBase").setFriction(frictionFactor);
        kinematicRagdollControl.getBoneRigidBody("RightFoot").setFriction(frictionFactor);
        kinematicRagdollControl.getBoneRigidBody("RightLeg").setFriction(frictionFactor);
        
        kinematicRagdollControl.getBoneRigidBody("LeftToeBase").setFriction(frictionFactor);
        kinematicRagdollControl.getBoneRigidBody("LeftFoot").setFriction(frictionFactor);
        kinematicRagdollControl.getBoneRigidBody("LeftLeg").setFriction(frictionFactor);*/
    }

    @Override
    public void applyPhysics(PhysicsSpace ps, float tpf) {
        /*Vector3f direction = characterControl.getWalkDirection();
        Vector3f speed = direction.divide(characterControl.getPhysicsSpace().getAccuracy());
        System.out.println("SPEED = "+speed);
        for (PhysicsRigidBody prb: objectsToPush) {
            prb.setLinearVelocity(new Vector3f(FastMath.rand.nextFloat(), 0f, FastMath.rand.nextFloat()).normalize().mult(3));
        }*/
    }
    
}
