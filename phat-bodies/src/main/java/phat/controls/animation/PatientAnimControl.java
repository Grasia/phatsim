/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.controls.animation;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.Bone;
import com.jme3.animation.LoopMode;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 *
 * @author Pablo
 */
public class PatientAnimControl extends BasicCharacterAnimControl {
    
    protected AnimChannel leftArmChannel;
    
    Bone rightHand;
    Bone leftHand;
    Vector3f position = new Vector3f();
    Quaternion rotation = new Quaternion();
    float [] angles = new float[3];
    int index = 2;
    
    float minAngle = -FastMath.QUARTER_PI/4f;
    float maxAngle = FastMath.QUARTER_PI/4f;
    float angular = FastMath.PI;
    boolean min = true;
    
    public PatientAnimControl() {
        super();        
    }
    
    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);

        
        leftHand = animControl.getSkeleton().getBone("LeftHand");
        leftHand.setUserControl(true);        
        
        rightHand = animControl.getSkeleton().getBone("RightHand");
        rightHand.setUserControl(true); 
        
        if(channel != null && leftArmChannel == null) {            
            leftArmChannel = animControl.createChannel();
            leftArmChannel.setLoopMode(LoopMode.Loop);
            leftArmChannel.addFromRootBone("LeftShoulder");
            leftArmChannel.setAnim("IdleStanding");
        }
        
        encorbar();
        
        rotation = new Quaternion();
    }
    
    public void encorbar() {
        Bone spine1 = animControl.getSkeleton().getBone("Spine1");
        spine1.getCombinedTransform(position, rotation);
        spine1.setUserControl(true);
        
        rotation.toAngles(angles);
        angles[0] = FastMath.QUARTER_PI/4f;
        rotation.fromAngles(angles);
        
        spine1.setUserTransforms(position, rotation, Vector3f.UNIT_XYZ);
    }
    
    private void setUserControlFrom(Bone bone, boolean userControl) {
        bone.setUserControl(userControl);
        for(Bone b: bone.getChildren()) {
            setUserControlFrom(b, userControl);
        }
    }
    
    @Override
    public void controlUpdate(float tpf) {
        super.controlUpdate(tpf);
        
        setUserControlFrom(leftHand, true);
        setUserControlFrom(rightHand, true);
        
        a(leftHand, tpf);
        a(rightHand, tpf);
        updateBonePositions(leftHand);
        
        //leftHand.setBindTransforms(position, rotation, Vector3f.UNIT_XYZ);
        
        /*
        if (characterControl.getWalkDirection().length() > 0) {
            if (!"WalkForward".equals(previousAnimName)) {
                applyLimitationsToBones();
            }
        }*/
        //setUserControlFrom(leftHand, false);
    }
    
    private void b(float tpf) {
        Quaternion rot = leftHand.getModelSpaceRotation().clone();
        Vector3f pos = leftHand.getModelSpacePosition().clone();
        
        updateRotation(rotation, tpf);
        
        rot.addLocal(rotation);
        
        leftHand.setUserTransformsWorld(pos, rot);
        
    }
    
    private void a(Bone bone, float tpf) {
        bone.getCombinedTransform(position, rotation);
        
        updateRotation(rotation, tpf);
        
        bone.setUserTransforms(position, rotation, Vector3f.UNIT_XYZ);
    }
    
    private void updateRotation(Quaternion rotation, float tpf) {
        rotation.toAngles(angles);
        
        //System.out.println("Angles = "+angles[0]+", "+angles[1]+", "+angles[2]);
        float angle = angles[index];
        
        if(min) {
            if(angle < minAngle) {
                min = false;
            } else {
                angle -= angular*tpf;
            }
        } else {
            if(angle > maxAngle) {
                min = true;
            } else {
                angle += angular*tpf;
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
    
    @Override
    public void onAnimCycleDone(AnimControl ac, AnimChannel ac1, String string) {        
        super.onAnimCycleDone(ac, ac1, string);
        if(string.equals("StandUpGround")) {
        }
    }
    
    @Override
    public void onAnimChange(AnimControl ac, AnimChannel ac1, String string) {
        super.onAnimChange(ac, ac1, string);
        /*if(string.equals("IdleStanding")) {
         Vector3f center = animControl.getSpatial().getParent().getWorldBound().getCenter();
         animControl.getSpatial().setLocalTranslation(new Vector3f(0f, -center.getY(), 0f)); 
         }*/
    }
}
