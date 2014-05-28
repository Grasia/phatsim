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
import com.jme3.math.FastMath;
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
public class StoopedControl  extends AbstractControl {
    SkeletonControl skeletonControl;
    Bone backBone;
    Vector3f position = new Vector3f();
    Quaternion rotation = new Quaternion();
    
    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);

        if (spatial != null) {
            skeletonControl = spatial.getControl(SkeletonControl.class);
            backBone = skeletonControl.getSkeleton().getBone("Spine1");
        } else {
            resetHand();
            skeletonControl = null;

        }
    }

    private void resetHand() {
        if (backBone != null) {
            setUserControlFrom(backBone, true);
        }
    }

    float [] angles = new float[3];
    @Override
    protected void controlUpdate(float fps) {
        if (spatial != null && backBone != null) {
            backBone.setUserControl(true);
            //setUserControlFrom(backBone, true);
            backBone.getCombinedTransform(position, rotation);
            rotation.fromAngles(FastMath.HALF_PI*0.2f, 0f, 0f);
            backBone.setUserTransforms(position, rotation, Vector3f.UNIT_XYZ);
            //updateBonePositions(backBone);
            //skeletonControl.getSkeleton().updateWorldVectors();
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
