/*
 * Copyright (C) 2014 Pablo Campillo-Sanchez <pabcampi@ucm.es>
 *
 * This software has been developed as part of the 
 * SociAAL project directed by Jorge J. Gomez Sanz
 * (http://grasia.fdi.ucm.es/sociaal)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package phat.bullet.control.ragdoll;

import com.jme3.animation.Bone;
import com.jme3.animation.SkeletonControl;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.control.KinematicRagdollControl;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 *
 * @author pablo
 */
public class PushChestFoward implements PhysicsTickListener {

    Spatial body;

    public PushChestFoward(Spatial body) {
        this.body = body;
    }

    @Override
    public void prePhysicsTick(PhysicsSpace ps, float f) {
        KinematicRagdollControl krc = body.getControl(KinematicRagdollControl.class);
        SkeletonControl sc = body.getControl(SkeletonControl.class);
        
        Vector3f dir = sc.getSkeleton().getBone("Spine").getModelSpaceRotation().mult(Vector3f.UNIT_Z);
        //Vector3f dir = Vector3f.UNIT_Z;
        dir.multLocal(1f);
        
        //highPrecision(krc, sc);
        
        setLinearVelocity(krc, sc, "Head", dir);
        setLinearVelocity(krc, sc, "Neck", dir);
        setLinearVelocity(krc, sc, "Spine", dir);
        setLinearVelocity(krc, sc, "Spine1", dir);
        setLinearVelocity(krc, sc, "LeftShoulder", dir);
        setLinearVelocity(krc, sc, "RightShoulder", dir);
        setLinearVelocity(krc, sc, "LeftForeArm", dir);
        setLinearVelocity(krc, sc, "RightForeArm", dir);
        setLinearVelocity(krc, sc, "LeftHand", dir);
        setLinearVelocity(krc, sc, "RightHand", dir);
    }
    
    void highPrecision(KinematicRagdollControl krc, SkeletonControl sc) {
        float linear = 0.0001f;
        float angular = 0.0001f;
        float threshold = 0.0001f;
        
        for(int i = 0; i < sc.getSkeleton().getBoneCount(); i++) {
            Bone bone = sc.getSkeleton().getBone(i);
            PhysicsRigidBody prb = krc.getBoneRigidBody(bone.getName());
            if(prb != null) {
                prb.setSleepingThresholds(linear, angular);
                prb.setCcdMotionThreshold(threshold);
                prb.setCcdSweptSphereRadius(.5f);
            }
        }
        
    }
    
    void setLinearVelocity(KinematicRagdollControl krc, SkeletonControl sc, String boneName, Vector3f dir) {
        PhysicsRigidBody spine = krc.getBoneRigidBody(boneName);
        spine.setLinearVelocity(dir);
    }

    @Override
    public void physicsTick(PhysicsSpace ps, float f) {
        ps.removeTickListener(this);
    }
}
