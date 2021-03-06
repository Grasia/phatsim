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
package phat.body.control.physics.ragdoll;

import com.jme3.animation.AnimControl;
import com.jme3.animation.Bone;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.control.AbstractPhysicsControl;
import com.jme3.bullet.control.KinematicRagdollControl;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.scene.Spatial;
import java.util.ArrayList;
import java.util.List;
import phat.body.control.physics.PHATCharacterControl;

/**
 *
 * @author pablo
 */
public abstract class RagdollTransitionControl extends AbstractPhysicsControl implements PhysicsTickListener {

    protected PHATCharacterControl characterControl;
    protected KinematicRagdollControl kinematicRagdollControl;
    protected AnimControl animControl;
    
    List<PhysicsRigidBody> objectsToPush = new ArrayList<>();

    protected PHATCharacterControl getCharacterControl() {
        if (characterControl == null && spatial != null) {
            characterControl = spatial.getControl(PHATCharacterControl.class);
        }
        return characterControl;
    }

    protected KinematicRagdollControl getKinematicRagdollControl() {
        if (kinematicRagdollControl == null && spatial != null) {
            kinematicRagdollControl = spatial.getControl(KinematicRagdollControl.class);
            if (kinematicRagdollControl == null) {
                BVHRagdollPreset preset = new BVHRagdollPreset();
                kinematicRagdollControl = new KinematicRagdollControl(preset, 0.5f);
                kinematicRagdollControl.setEnabled(false);
            }
        }
        return kinematicRagdollControl;
    }

    protected AnimControl getAnimControl() {
        if (animControl == null && spatial != null) {
            animControl = spatial.getControl(AnimControl.class);
        }
        return animControl;
    }

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);

        if (spatial == null) {
            if (kinematicRagdollControl != null) {
                kinematicRagdollControl.setEnabled(false);
            }
        }
    }

    public void activate() {
        System.out.println("Transition.activate()...");
        if (getCharacterControl() != null && getKinematicRagdollControl() != null) {
            characterControl.setEnabled(false);
            kinematicRagdollControl.getPhysicsSpace().addTickListener(this);
        }
        System.out.println("...Transition.activate()");
    }

    public void desactivate() {
        if (getKinematicRagdollControl() != null) {
            kinematicRagdollControl.getPhysicsSpace().removeTickListener(this);
        }
    }
    
    @Override
    public void update(float tpf) {
        super.update(tpf);
        
    }

    @Override
    public void prePhysicsTick(PhysicsSpace ps, float tpf) {
        System.out.println("prePhysicsTick...");
        if (getKinematicRagdollControl() != null) {
            System.out.println("\tRagdon on!");
            kinematicRagdollControl.setEnabled(true);
            kinematicRagdollControl.setRagdollMode();

            highPrecision();

            initBody();
            applyPhysics(ps, tpf);
        }
        System.out.println("...prePhysicsTick");
    }

    public void highPrecision() {
        float linear = 0.0001f;
        float angular = 0.0001f;
        float threshold = 0.0001f;

        if (getAnimControl() != null) {
            for (int i = 0; i < animControl.getSkeleton().getBoneCount(); i++) {
                Bone bone = animControl.getSkeleton().getBone(i);
                PhysicsRigidBody prb = kinematicRagdollControl.getBoneRigidBody(bone.getName());
                if (prb != null) {
                    prb.setSleepingThresholds(linear, angular);
                    prb.setCcdMotionThreshold(threshold);
                    prb.setCcdSweptSphereRadius(.5f);
                }
            }
        }

    }

    @Override
    public void physicsTick(PhysicsSpace ps, float tpf) {
        if (getKinematicRagdollControl() != null) {
            kinematicRagdollControl.getPhysicsSpace().removeTickListener(this);
        }
    }
    
    @Override
    protected void addPhysics(PhysicsSpace space) {
        space.addTickListener(this);
    }

    @Override
    protected void removePhysics(PhysicsSpace space) {
        space.removeTickListener(this);
    }

    protected abstract void initBody();

    public abstract void applyPhysics(PhysicsSpace ps, float tpf);
}
