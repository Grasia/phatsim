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
