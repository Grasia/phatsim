/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.body.control.parkinson;

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
public class RigidRightArmControl extends AbstractControl {
    SkeletonControl skeletonControl;
    Bone armBone;
    
    public enum Arm {LeftArm, RightArm};
    
    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);

        if (spatial != null) {
            skeletonControl = spatial.getControl(SkeletonControl.class);
            armBone = skeletonControl.getSkeleton().getBone("RightShoulder");
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
    Vector3f lastPosition = new Vector3f(0f,0f,0f);
    Quaternion lastRotation = new Quaternion();
    
    @Override
    protected void controlUpdate(float fps) {
        if (spatial != null && armBone != null) {
            armBone.getCombinedTransform(position, rotation);
            if(lastPosition.length() > 0) {
                setUserControlFrom(armBone, true);
                position.addLocal(lastPosition);
                position.multLocal(0.5f);
                rotation.addLocal(lastRotation);
                rotation.multLocal(lastRotation);
                armBone.setUserTransformsWorld(position, rotation);
                updateBonePositions(armBone);
                setUserControlFrom(armBone, false);
            }
            //updateBonePositions(armBone);
            lastPosition.set(position);
            lastRotation.set(rotation);
        }
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
