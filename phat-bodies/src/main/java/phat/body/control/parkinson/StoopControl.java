package phat.body.control.parkinson;

import com.jme3.animation.AnimControl;
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
 * It the character a character a given degree.
 *
 * Depends on <b>SkeletonControl</b>
 *
 * @author pablo
 */
public class StoopControl extends AbstractControl {
    AnimControl animControl;
    Bone spine1;
    Vector3f position = new Vector3f();
    Quaternion rotation = new Quaternion();
    float[] angles = new float[3];
    float degree = 0.4f;
    float currentDegree = 0.4f;

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);

        if (spatial != null) {
            animControl = spatial.getControl(AnimControl.class);
            if (animControl != null) {
                spine1 = animControl.getSkeleton().getBone("Spine1");
                stoop();
            }
        } else {
            resetSpine1();
            animControl = null;
            rotation = null;

        }
    }

    private void resetSpine1() {
        if (spine1 != null) {
            setUserControlFrom(spine1, true);
            spine1.getCombinedTransform(position, rotation);
            angles[0] = 0;
            angles[1] = 0;
            angles[2] = 0;
            rotation.fromAngles(angles);
            spine1.setUserTransforms(position, rotation, Vector3f.UNIT_XYZ);
            updateBonePositions(spine1);
        }
    }

    @Override
    protected void controlUpdate(float fps) {
        if(currentDegree != degree) {
            currentDegree = degree;
            stoop();
        }
    }
    
    public void stoop() {
        spine1.getCombinedTransform(position, rotation);
        spine1.setUserControl(true);
        
        rotation.toAngles(angles);
        angles[0] = degree;
        rotation.fromAngles(angles);
        
        spine1.setUserTransforms(position, rotation, Vector3f.UNIT_XYZ);
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

    public float getDegree() {
        return degree;
    }

    public void setDegree(float degree) {
        this.degree = degree;
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    @Override
    public Control cloneForSpatial(Spatial sptl) {
        StoopControl control = new StoopControl();
        control.setSpatial(sptl);
        control.setDegree(degree);
        return control;
    }
    
    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule oc = ex.getCapsule(this);
        oc.write(degree, "degree", 0.4f);
        
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule ic = im.getCapsule(this);
        degree = ic.readFloat("degree", 0.4f);
    }
}