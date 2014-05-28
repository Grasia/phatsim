/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.body.control.physics.ragdoll;

import com.jme3.animation.AnimControl;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;

/**
 *
 * @author pablo
 */
public class ApplyLinealVelocityAllBody extends RagdollTransitionControl {
    
    @Override
    public void initBody() {
        
    }

    @Override
    public void applyPhysics(PhysicsSpace ps, float tpf) {
        Vector3f direction = characterControl.getWalkDirection();
        Vector3f speed = direction.divide(characterControl.getPhysicsSpace().getAccuracy());
        for (int i = 0; i < animControl.getSkeleton().getBoneCount(); i++) {
            PhysicsRigidBody prb = kinematicRagdollControl.getBoneRigidBody(animControl.getSkeleton().getBone(i).getName());
            prb.setLinearVelocity(speed);
        }
    }

    @Override
    protected void createSpatialData(Spatial sptl) {
    }

    @Override
    protected void removeSpatialData(Spatial sptl) {
    }

    @Override
    protected void setPhysicsLocation(Vector3f vctrf) {
    }

    @Override
    protected void setPhysicsRotation(Quaternion qtrn) {
    }

    @Override
    public Control cloneForSpatial(Spatial sptl) {
        return new ApplyLinealVelocityAllBody();
    }
    
}
