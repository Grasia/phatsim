/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.agents.actors.parkinson;

import com.jme3.animation.Bone;
import com.jme3.animation.SkeletonControl;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import phat.agents.actors.ActorFactory;

/**
 *
 * @author pablo
 */
public class HandTremblingControl extends AbstractControl {

    public enum Hand {LEFT_HAND, RIGHT_HAND};
    
    Hand handSelected;
    SkeletonControl skeletonControl;
    Bone hand;
    Vector3f position = new Vector3f();
    Quaternion rotation = new Quaternion();
    float[] angles = new float[3];
    int index = 2;
    float minAngle = -FastMath.QUARTER_PI / 4f;
    float maxAngle = FastMath.QUARTER_PI / 4f;
    float angular = FastMath.PI;
    boolean min = true;

    public HandTremblingControl(Hand hand) {        
        handSelected = hand;
    }
    
    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        
        skeletonControl = ActorFactory.findControl(spatial, SkeletonControl.class);

        switch(handSelected) {
            case LEFT_HAND:
                hand = skeletonControl.getSkeleton().getBone("LeftHand");
                break;
            case RIGHT_HAND:
                hand = skeletonControl.getSkeleton().getBone("RightHand");
                break;
        }
        
        //setUserControlFrom(hand, true);

        rotation = new Quaternion();
    }

    @Override
    protected void controlUpdate(float fps) {
        setUserControlFrom(hand, true);
        
        hand.getCombinedTransform(position, rotation);
                
        updateRotation(rotation, fps);
        
        hand.setUserTransforms(position, rotation, Vector3f.UNIT_XYZ);
        
        updateBonePositions(hand);
        
        
        /*setUserControlFrom(hand, true);
        
        hand.getModelSpacePosition();
        hand.getCombinedTransform(position, rotation);
        System.out.println("Position = "+hand.getModelSpacePosition());
        
        updateRotation(rotation, fps);

        //hand.setUserTransforms(position, rotation, Vector3f.UNIT_XYZ);
        
        //updateBonePositions(hand);
        
        //setUserControlFrom(hand, false);
        */
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        
        //setUserControlFrom(hand, enabled);
    }

    private void updateRotation(Quaternion rotation, float tpf) {
        rotation.toAngles(angles);

        //System.out.println("Angles = "+angles[0]+", "+angles[1]+", "+angles[2]);
        float angle = angles[index];

        if (min) {
            if (angle < minAngle) {
                min = false;
            } else {
                angle -= angular * tpf;
            }
        } else {
            if (angle > maxAngle) {
                min = true;
            } else {
                angle += angular * tpf;
            }
        }
        angles[0] = 0;
        angles[1] = 0;
        angles[index] = angle;

        rotation.fromAngles(angles);
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

    public float getMinAngle() {
        return minAngle;
    }

    public void setMinAngle(float minAngle) {
        this.minAngle = minAngle;
    }

    public float getMaxAngle() {
        return maxAngle;
    }

    public void setMaxAngle(float maxAngle) {
        this.maxAngle = maxAngle;
    }

    public float getAngular() {
        return angular;
    }

    public void setAngular(float angular) {
        this.angular = angular;
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    @Override
    public Control cloneForSpatial(Spatial sptl) {
        HandTremblingControl control = new HandTremblingControl(handSelected);
        
        control.setMinAngle(minAngle);
        control.setMaxAngle(maxAngle);
        control.setAngular(angular);
        
        return control;
    }
}
