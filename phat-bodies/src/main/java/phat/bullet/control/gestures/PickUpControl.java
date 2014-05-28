/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.bullet.control.gestures;

import com.jme3.animation.Bone;
import com.jme3.animation.Skeleton;
import com.jme3.animation.SkeletonControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import java.util.ArrayList;
import phat.agents.actors.ActorFactory;
import phat.controls.animation.BasicCharacterAnimControl;

/**
 *
 * @author pablo
 */
public class PickUpControl extends AbstractControl {

    Spatial target;
    Node handPosition = new Node();
    
    float speed = 0.1f;
    float margin = 0.1f;
    
    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);

        if (spatial != null) {
            init();
        }
    }

    private void init() {
        Skeleton skeleton = ActorFactory.findControl(spatial, SkeletonControl.class).getSkeleton();
        
        handPosition.setLocalTranslation(skeleton.getBone("LeftHand").getModelSpacePosition());
        
        IkControl ikControl = new IkControl(skeleton);
        spatial.addControl(ikControl);
        Node n = new Node();
        n.setLocalTranslation(target.getLocalTranslation());
        ikControl.setTarget(n);
        ikControl.setFirstBone(skeleton.getBone("LeftShoulder"));
        ikControl.setMaxChain(2);
        ikControl.setIterations(50);
        ikControl.setTargetBone(skeleton.getBone("LeftHand"));
        
        skeleton.reset();
        skeleton.updateWorldVectors();
    }
    
    public void setTarget(Spatial target) {
        this.target = target;
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        if(condition()) {
            updateHandPos(tpf);
        }
    }

    private boolean condition() {
        return target != null &&
                target.getLocalTranslation().distance(handPosition.getLocalTranslation()) > margin;
    }
    
    private void updateHandPos(float tpf) {
        Vector3f targetLoc = target.getLocalTranslation();
        Vector3f handLoc = handPosition.getLocalTranslation();        
        System.out.println("Hand location = "+handLoc);
        System.out.println("Target location = "+targetLoc);
        
        Vector3f distance = targetLoc.subtract(handLoc);
        Vector3f displazament = distance.normalize().mult(speed*tpf);
        
        handPosition.getLocalTranslation().addLocal(displazament);
    }
    
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        
    }

    @Override
    public Control cloneForSpatial(Spatial sptl) {
        PickUpControl control = new PickUpControl();
        
        return control;
    }
    
}
