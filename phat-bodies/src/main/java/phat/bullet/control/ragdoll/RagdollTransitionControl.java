/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.bullet.control.ragdoll;

import com.jme3.animation.AnimControl;
import com.jme3.animation.Bone;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.control.KinematicRagdollControl;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.scene.Node;
import phat.body.control.physics.PHATCharacterControl;

/**
 *
 * @author pablo
 */
public abstract class RagdollTransitionControl implements PhysicsTickListener {
    protected Node node;
    protected PHATCharacterControl characterControl;
    protected KinematicRagdollControl kinematicRagdollControl;
    protected AnimControl animControl;
    
    public RagdollTransitionControl(Node node) {
        this.node = node;
        characterControl = node.getControl(PHATCharacterControl.class);
        kinematicRagdollControl = node.getControl(KinematicRagdollControl.class);
        animControl = node.getControl(AnimControl.class);        
    }
    
    public void activate() {
        if (characterControl != null) {
            characterControl.setEnabled(false);
        }
        kinematicRagdollControl.getPhysicsSpace().addTickListener(this);
    }
    
    public void desactivate() {
        kinematicRagdollControl.getPhysicsSpace().removeTickListener(this);
    }
    
    @Override
    public void prePhysicsTick(PhysicsSpace ps, float tpf) {
        //kinematicRagdollControl.setEnabled(true);
        kinematicRagdollControl.setRagdollMode();
        
        highPrecision();
        
        initBody();
        applyPhysics(ps, tpf);
    }

    public void highPrecision() {
        float linear = 0.0001f;
        float angular = 0.0001f;
        float threshold = 0.0001f;
        
        for(int i = 0; i < animControl.getSkeleton().getBoneCount(); i++) {
            Bone bone = animControl.getSkeleton().getBone(i);
            PhysicsRigidBody prb = kinematicRagdollControl.getBoneRigidBody(bone.getName());
            if(prb != null) {
                prb.setSleepingThresholds(linear, angular);
                prb.setCcdMotionThreshold(threshold);
                prb.setCcdSweptSphereRadius(.5f);
            }
        }
        
    }
    
    @Override
    public void physicsTick(PhysicsSpace ps, float tpf) {
        kinematicRagdollControl.getPhysicsSpace().removeTickListener(this);
    }
    
    protected abstract void initBody();
            
    public abstract void applyPhysics(PhysicsSpace ps, float tpf);
}
