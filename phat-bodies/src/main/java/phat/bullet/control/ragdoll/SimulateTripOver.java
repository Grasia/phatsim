/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.bullet.control.ragdoll;

import com.jme3.animation.AnimControl;
import com.jme3.animation.Bone;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.ArrayList;
import java.util.List;
import phat.agents.actors.ActorFactory;

/**
 *
 * @author pablo
 */
public class SimulateTripOver extends RagdollTransitionControl {
    
    List<PhysicsRigidBody> objectsToPush = new ArrayList<PhysicsRigidBody>();
    float frictionFactor = 1f;
    
    public SimulateTripOver(Node model) {
        super(model);
    }
    
    @Override
    public void initBody() {

        objectsToPush.add(kinematicRagdollControl.getBoneRigidBody("Head"));
        objectsToPush.add(kinematicRagdollControl.getBoneRigidBody("Neck"));
        objectsToPush.add(kinematicRagdollControl.getBoneRigidBody("Jaw"));
        objectsToPush.add(kinematicRagdollControl.getBoneRigidBody("Spine1"));
        objectsToPush.add(kinematicRagdollControl.getBoneRigidBody("Spine"));
        //objectsToPush.add(kinematicRagdollControl.getBoneRigidBody("Hips"));

        objectsToPush.add(kinematicRagdollControl.getBoneRigidBody("LeftShoulder"));
        objectsToPush.add(kinematicRagdollControl.getBoneRigidBody("LeftArm"));
        objectsToPush.add(kinematicRagdollControl.getBoneRigidBody("LeftShoulder"));
        objectsToPush.add(kinematicRagdollControl.getBoneRigidBody("LeftForeArm"));
        objectsToPush.add(kinematicRagdollControl.getBoneRigidBody("LeftHand"));

        objectsToPush.add(kinematicRagdollControl.getBoneRigidBody("RightShoulder"));
        objectsToPush.add(kinematicRagdollControl.getBoneRigidBody("RightArm"));
        objectsToPush.add(kinematicRagdollControl.getBoneRigidBody("RightForeArm"));
        objectsToPush.add(kinematicRagdollControl.getBoneRigidBody("RightHand"));
        
        
        kinematicRagdollControl.getBoneRigidBody("RightToeBase").setFriction(frictionFactor);
        kinematicRagdollControl.getBoneRigidBody("RightFoot").setFriction(frictionFactor);
        kinematicRagdollControl.getBoneRigidBody("RightLeg").setFriction(frictionFactor);
        
        kinematicRagdollControl.getBoneRigidBody("LeftToeBase").setFriction(frictionFactor);
        kinematicRagdollControl.getBoneRigidBody("LeftFoot").setFriction(frictionFactor);
        kinematicRagdollControl.getBoneRigidBody("LeftLeg").setFriction(frictionFactor);
    }

    @Override
    public void applyPhysics(PhysicsSpace ps, float tpf) {
        //Vector3f direction = characterControl.getWalkDirection().normalize();
        Vector3f speed = characterControl.getVelocity();
        //Vector3f speed = direction.divide(characterControl.getPhysicsSpace().getAccuracy());
        for (PhysicsRigidBody prb: objectsToPush) {
            prb.setLinearVelocity(speed/*.mult(5f)*/);
        }
    }
    
}
