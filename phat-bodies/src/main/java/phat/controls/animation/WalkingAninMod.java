/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.controls.animation;

import com.jme3.animation.Bone;
import com.jme3.animation.SkeletonControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import java.util.ArrayList;
import java.util.List;
import phat.agents.actors.ActorFactory;

/**
 *
 * @author pablo
 */
public class WalkingAninMod extends AbstractControl {

    BasicCharacterAnimControl basicChAnimControl;
    SkeletonControl skeletonControl;
    List<Bone> bones;
    Vector3f position = new Vector3f();
    Quaternion rotation = new Quaternion();
    float [] angles = new float[3];

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);

        if (spatial != null) {
            init();
        }
    }

    private void init() {
        bones = new ArrayList<Bone>();
        basicChAnimControl = ActorFactory.findControl(spatial, BasicCharacterAnimControl.class);
        skeletonControl = ActorFactory.findControl(spatial, SkeletonControl.class);

        Bone leftUpLeg = skeletonControl.getSkeleton().getBone("LeftUpLeg");
        Bone rightUpLeg = skeletonControl.getSkeleton().getBone("RightUpLeg");
    
        getAllBonesFrom(leftUpLeg, bones);
        getAllBonesFrom(rightUpLeg, bones);
       
        skeletonControl.getSkeleton().reset();
        skeletonControl.getSkeleton().updateWorldVectors();
    }

    public void getAllBonesFrom(Bone bone, List<Bone> list) {
        list.add(bone);
        if (bone.getChildren() != null && bone.getChildren().size() > 0) {
            for (Bone b : bone.getChildren()) {
                getAllBonesFrom(b, list);
            }
        }
    }

    @Override
    protected void controlUpdate(float f) {
        if (basicChAnimControl.getCurrentAnimationName().equals("WalkForward")) {
            for (Bone b : bones) {
                b.setUserControl(true);                
            }
            
            System.out.println("Bones");
            for (Bone b : bones) {
                //b.getCombinedTransform(position, rotation);
                position = b.getModelSpacePosition().clone();
                rotation = b.getModelSpaceRotation().clone();
                
                //rotation.fromAngles(angles);
                
                b.setUserTransformsWorld(position, rotation.mult(0.1f));
                updateBonePositions(b);
                System.out.println("\t"+b.getName()+" -> ("+angles[0]+", "+angles[1]+", "+angles[2]+") "+position);
            }
            
            for (Bone b : bones) {
                b.setUserControl(false);                
            }
        }
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
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    @Override
    public Control cloneForSpatial(Spatial sptl) {
        WalkingAninMod control = new WalkingAninMod();
        control.setSpatial(spatial);
        return control;
    }
}
