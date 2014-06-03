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
package phat.body.control.physics;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.control.AbstractPhysicsControl;
import com.jme3.bullet.control.KinematicRagdollControl;
import com.jme3.bullet.control.PhysicsControl;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import java.io.IOException;
import phat.body.control.animation.BasicCharacterAnimControl;
import phat.body.control.physics.ragdoll.BVHRagdollPreset;

/**
 *
 * @author pablo
 */
public class PHATKinematicRagdollControl extends AbstractPhysicsControl implements AnimEventListener, Control {

    boolean ragdollMode = false;
    PHATCharacterControl characterControl;
    KinematicRagdollControl kinematicRagdollControl;

    @Override
    public void setSpatial(Spatial spatial) {
        System.out.println(getClass().getSimpleName()+": setSpatial... "+spatial);
        if(spatial == null) {
            kinematicRagdollControl = this.spatial.getControl(KinematicRagdollControl.class);
            if(kinematicRagdollControl != null) {
                this.spatial.removeControl(kinematicRagdollControl);
            }
        }
        super.setSpatial(spatial);
        if(spatial != null) {
            createKinematicRagdollControl();
            spatial.addControl(kinematicRagdollControl);
        }
        System.out.println(getClass().getSimpleName()+": ...setSpatial");
    }

    private KinematicRagdollControl createKinematicRagdollControl() {
        kinematicRagdollControl = spatial.getControl(KinematicRagdollControl.class);
        if (kinematicRagdollControl == null) {
            BVHRagdollPreset preset = new BVHRagdollPreset();
            kinematicRagdollControl = new KinematicRagdollControl(preset, 0.5f);                                    
        }
        return kinematicRagdollControl;
    }

    private boolean isKRagdollMode() {
        return createKinematicRagdollControl().getMode() == KinematicRagdollControl.Mode.Ragdoll;
    }

    @Override
    public void update(float fps) {
        if (space == null) {
            return;
        }
        if (ragdollMode && !isKRagdollMode()) {
            characterControl = spatial.getControl(PHATCharacterControl.class);
            if (characterControl != null) {
                characterControl.setEnabled(false);
            }
            kinematicRagdollControl.setEnabled(true);
            kinematicRagdollControl.setRagdollMode();
            for (int i = 0; i < spatial.getNumControls(); i++) {
                Control c = spatial.getControl(i);
                System.out.println("-> " + c.getClass().getSimpleName());
            }
        } else if (!ragdollMode && isKRagdollMode()) {
            standUp();
        }
    }

    private void standUp() {
        System.out.println("StandUp!");
        BasicCharacterAnimControl basicCharacterAnimControl = spatial.getControl(BasicCharacterAnimControl.class);
        if (basicCharacterAnimControl != null) {
            System.out.println("\t setKinematicMode()");
            createKinematicRagdollControl().setKinematicMode();
            System.out.println("\t enabling basicCharacterAnimControl");
            basicCharacterAnimControl.setEnabled(true);
            System.out.println("\t standUpAnimation()");
            basicCharacterAnimControl.standUpAnimation(null);
        }
        /*AnimControl animControl = spatial.getControl(AnimControl.class);

        AnimChannel ac;
        if (animControl.getNumChannels() == 0) {
            ac = animControl.createChannel();
        } else {
            ac = animControl.getChannel(0);
        }

        basicCharacterAnimControl.standUpAnimation();
        ac.setAnim("StandUpGround");
        ac.setLoopMode(LoopMode.DontLoop);
        System.out.println("-------------> Kinematic Mode!!");
        kinematicRagdollControl.blendToKinematicMode(1f);*/
    }

    public boolean isRagdollMode() {
        return ragdollMode;
    }

    public void setRagdollMode(boolean ragdollMode) {
        System.out.println("setRagdollMode = " + ragdollMode);
        this.ragdollMode = ragdollMode;
    }

    @Override
    protected void createSpatialData(Spatial spat) {
    }

    @Override
    protected void removeSpatialData(Spatial spat) {
    }

    @Override
    protected void setPhysicsLocation(Vector3f vec) {
    }

    @Override
    protected void setPhysicsRotation(Quaternion quat) {
    }

    @Override
    protected void addPhysics(PhysicsSpace space) {
        System.out.println(getClass().getSimpleName()+": addPhysics...");
        space.add(createKinematicRagdollControl());
        //spatial.addControl(kinematicRagdollControl);
        kinematicRagdollControl.setKinematicMode();
        //kinematicRagdollControl.setEnabled(false);
        System.out.println(getClass().getSimpleName()+": ...addPhysics");
    }

    @Override
    protected void removePhysics(PhysicsSpace space) {
        space.remove(kinematicRagdollControl);
    }

    @Override
    public Control cloneForSpatial(Spatial sptl) {
        PHATKinematicRagdollControl c = new PHATKinematicRagdollControl();
        c.setRagdollMode(ragdollMode);
        c.setSpatial(sptl);
        return c;
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule oc = ex.getCapsule(this);
        oc.write(ragdollMode, "ragdollMode", false);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule ic = im.getCapsule(this);
        ragdollMode = ic.readBoolean("ragdollMode", false);
    }

    @Override
    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
        if(animName.equals(BasicCharacterAnimControl.AnimName.StandUpGround.name())) {
            PHATCharacterControl c = spatial.getControl(PHATCharacterControl.class);
            c.setEnabled(true);
            kinematicRagdollControl.setEnabled(false);
        }
    }

    @Override
    public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
        
    }
}
