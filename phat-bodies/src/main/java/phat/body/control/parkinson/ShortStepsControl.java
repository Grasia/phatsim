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
public class ShortStepsControl  extends AbstractControl {
    SkeletonControl skeletonControl;
    Bone leftLeg;
    Bone rightLeg;
    Bone leftUpLeg;
    Bone rightUpLeg;
    float factor = 0.9f;
    
    public enum Arm {LeftArm, RightArm};
    
    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);

        if (spatial != null) {
            skeletonControl = spatial.getControl(SkeletonControl.class);
            leftLeg = skeletonControl.getSkeleton().getBone("LeftLeg");
            rightLeg = skeletonControl.getSkeleton().getBone("RightLeg");
            leftUpLeg = skeletonControl.getSkeleton().getBone("LeftUpLeg");
            rightUpLeg = skeletonControl.getSkeleton().getBone("RightUpLeg");
        } else {
            resetHand();
            skeletonControl = null;

        }
    }

    private void resetHand() {
        if (leftLeg != null) {
            setUserControlFrom(leftLeg, true);
        }
        if (rightLeg != null) {
            setUserControlFrom(rightLeg, true);
        }
        if (leftUpLeg != null) {
            setUserControlFrom(rightLeg, true);
        }
        if (rightUpLeg != null) {
            setUserControlFrom(rightLeg, true);
        }
    }

    Vector3f position = new Vector3f();
    Quaternion rotation = new Quaternion();
    Vector3f lastLeftPosition = new Vector3f(0f,0f,0f);
    Quaternion lastLeftRotation = new Quaternion();
    Vector3f lastRightPosition = new Vector3f(0f,0f,0f);
    Quaternion lastRightRotation = new Quaternion();
    Vector3f lastLeftUpPosition = new Vector3f(0f,0f,0f);
    Quaternion lastLeftUpRotation = new Quaternion();
    Vector3f lastRightUpPosition = new Vector3f(0f,0f,0f);
    Quaternion lastRightUpRotation = new Quaternion();
    
    @Override
    protected void controlUpdate(float fps) {
        update(leftLeg, lastLeftPosition, lastLeftRotation);
        update(rightLeg, lastRightPosition, lastRightRotation);
        update(leftUpLeg, lastLeftUpPosition, lastLeftUpRotation);
        update(rightUpLeg, lastRightUpPosition, lastRightUpRotation);
    }

    Vector3f tempPos = new Vector3f();
    private void update(Bone bone, Vector3f lastPos, Quaternion lastRot) {
        if (spatial != null && bone != null) {
            setUserControlFrom(bone, true);
            position.set(bone.getLocalPosition());
            rotation.set(bone.getLocalRotation());
            if(lastPos.length() > 0) {
                
                /*tempPos.set(lastPos);
                lastPos.multLocal(factor - 1f);
                position.multLocal(factor);
                position.addLocal(lastPos);
                position.addLocal(tempPos);
                position.subtractLocal(bone.getLocalPosition());*/
                 
                updateRotation(bone, lastRot);
            }
            setUserControlFrom(bone, false);
            lastPos.set(bone.getLocalPosition());
            lastRot.set(bone.getLocalRotation());
        }
    }
    
    float[] lastAngles = new float[3];
    float[] currentAngles = new float[3];
    private void updateRotation(Bone bone, Quaternion lastRot) {
        lastRot.toAngles(lastAngles);
        bone.getLocalRotation().toAngles(currentAngles);
        
         float distX = currentAngles[0] - ((currentAngles[0] * factor) - (lastAngles[0] * (factor - 1f)));
         float distY = currentAngles[1] -((currentAngles[1] * factor) - (lastAngles[1] * (factor - 1f)));
         float distZ = currentAngles[2] - ((currentAngles[2] * factor) - (lastAngles[2] * (factor - 1f)));
         
         //System.out.println("Angles = "+distX+","+distY+","+distZ);
         bone.setUserTransforms(Vector3f.ZERO, rotation.fromAngles(distX, distY, distZ), Vector3f.UNIT_XYZ);
         //updateBonePositions(bone);
         skeletonControl.getSkeleton().updateWorldVectors();
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

    public float getFactor() {
        return factor;
    }

    public void setFactor(float factor) {
        this.factor = factor;
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
