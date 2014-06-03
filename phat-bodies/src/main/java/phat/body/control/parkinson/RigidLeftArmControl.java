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
package phat.body.control.parkinson;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.Bone;
import com.jme3.animation.SkeletonControl;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import java.io.IOException;

/**
 *
 * @author pablo
 */
public class RigidLeftArmControl extends AbstractControl implements AnimEventListener {

    SkeletonControl skeletonControl;
    AnimControl animControl;
    Bone armBone;

    @Override
    public void onAnimCycleDone(AnimControl ac, AnimChannel animChannel, String animName) {
        setUserControlFrom(armBone, true);
    }

    @Override
    public void onAnimChange(AnimControl ac, AnimChannel animChannel, String animName) {
        if (spatial != null && armBone != null &&
                animName.equals("WalkForward")) {
            setUserControlFrom(armBone, true);
            //armBone.setUserTransforms(position, R, Vector3f.UNIT_XYZ);
            //updateBonePositions(armBone);
        }
    }

    public enum Arm {

        LeftArm, RightArm
    };

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);

        if (spatial != null) {
            skeletonControl = spatial.getControl(SkeletonControl.class);
            animControl = spatial.getControl(AnimControl.class);
            armBone = skeletonControl.getSkeleton().getBone("LeftShoulder");
            animControl.addListener(this);
        } else {
            resetHand();
            skeletonControl = null;

        }
    }

    private void resetHand() {
        if (armBone != null) {
            setUserControlFrom(armBone, true);
        }
    }
    Vector3f position = new Vector3f();
    Quaternion rotation = new Quaternion();
    Vector3f lastPosition = new Vector3f(0f, 0f, 0f);
    Quaternion lastRotation = new Quaternion();

    @Override
    protected void controlUpdate(float fps) {
        
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
    }

    private void updateBonePositions(Bone bone) {
        Transform t = new Transform();
        for (Bone b : bone.getChildren()) {
            t = b.getCombinedTransform(bone.getModelSpacePosition(), bone.getModelSpaceRotation());
            b.setUserTransformsWorld(t.getTranslation(), b.getModelSpaceRotation());
            updateBonePositions(b);
        }
    }

    private void setUserControlFrom(Bone bone, boolean userControl) {
        bone.setUserControl(userControl);
        for (Bone b : bone.getChildren()) {
            setUserControlFrom(b, userControl);
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    @Override
    public Control cloneForSpatial(Spatial sptl) {
        LeftHandTremblingControl control = new LeftHandTremblingControl();
        control.setSpatial(sptl);
        return control;
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule oc = ex.getCapsule(this);

    }

    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule ic = im.getCapsule(this);
    }
}
