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
package phat.controls.animation;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.Bone;
import com.jme3.animation.LoopMode;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import java.util.Collection;
import java.util.Vector;
import phat.agents.actors.ActorFactory;

/**
 * Basic animation handler for an character who has a CharacterControl. This
 * monitorizes the character using its CharacterControl. If the character is
 * moving the controler sets WalkForward animation else sets IdleStanding
 * animation.
 *
 * @author Pablo
 */
public class BasicCharacterAnimControl extends AbstractControl implements AnimEventListener {

    protected AnimControl animControl;
    protected CharacterControl characterControl;
    protected AnimChannel channel;
    protected String manualAnimation = null;

    public BasicCharacterAnimControl() {
        super();
    }

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);

        if (spatial != null) {
            init();
        }
    }

    private void init() {
        if(channel != null) {
            return;
        }
        if (animControl == null) {
            animControl = ActorFactory.findControl(spatial, AnimControl.class);
        }
        if (characterControl == null) {
            characterControl = ActorFactory.findControl(spatial, CharacterControl.class);
        }
        if (animControl != null && characterControl != null) {
            channel = animControl.createChannel();
            channel.setLoopMode(LoopMode.Loop);

            animControl.addListener(this);
        }
    }

    public void standUpAnimation() {
        channel.setAnim("StandUpGround", 0.5f);
        channel.setLoopMode(LoopMode.DontLoop);
        Vector3f extent = ((BoundingBox) animControl.getSpatial().getParent().getWorldBound()).getExtent(new Vector3f());
        Vector3f center = animControl.getSpatial().getParent().getWorldBound().getCenter();
        animControl.getSpatial().getParent().setLocalTranslation(center.setY(0.0f));
        animControl.getSpatial().setLocalTranslation(Vector3f.ZERO);
    }

    public String getCurrentAnimationName() {
        return channel.getAnimationName();
    }
    
    public float getAnimTime() {
        return channel.getTime();
    }
    
    public boolean hasAnimation(String animationName) {
        return animControl.getAnim(animationName) != null;
    }
    
    public Collection<String> getAnimations() {
        return animControl.getAnimationNames();
    }

    @Override
    public void controlUpdate(float tpf) {
        init();
        
        String currentAnim = getCurrentAnimationName();
        if (currentAnim != null && currentAnim.equals("StandUpGround")) {
            return;
        }

        if (!characterControl.onGround()) {
            /*if(!"JumpLoop".equals(torsoChannel.getAnimationName()))
             torsoChannel.setAnim("JumpLoop");
             if(!"JumpLoop".equals(feetChannel.getAnimationName()))
             feetChannel.setAnim("JumpLoop");
             return;*/
        }

        if (characterControl.getWalkDirection().length() > 0) {
            if (!"WalkForward".equals(channel.getAnimationName())) {
                channel.setAnim("WalkForward");
            }
        } else {
            if ((channel.getAnimationName() == null || 
                    channel.getAnimationName().equals("WalkForward")) &&
                !"IdleStanding".equals(channel.getAnimationName())) {
                channel.setAnim("IdleStanding");
            }
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    @Override
    public Control cloneForSpatial(Spatial spatial) {
        BasicCharacterAnimControl cac = new BasicCharacterAnimControl();
        cac.setSpatial(spatial);
        return cac;
    }
    
    public void setAnimation(String name) {
        channel.setAnim(name);
        channel.setLoopMode(LoopMode.DontLoop);
        manualAnimation = name;
    }
    
    @Override
    public void onAnimCycleDone(AnimControl ac, AnimChannel ac1, String string) {
        System.out.println("onAnimCycleDone = "+string);
        if(manualAnimation != null && manualAnimation.equals(string)) {
            channel.setAnim("IdleStanding");
        }
        
        if (string.equals("StandUpGround")) {
            characterControl.setWalkDirection(Vector3f.ZERO);
            characterControl.setEnabled(true);
            channel.setAnim("IdleStanding");
            //Vector3f center = animControl.getSpatial().getWorldBound().getCenter();
            Vector3f extent = ((BoundingBox) animControl.getSpatial().getWorldBound()).getExtent(new Vector3f());
            animControl.getSpatial().setLocalTranslation(new Vector3f(0f, -extent.getY(), 0f));
            //animControl.getSpatial().getParent().setLocalTranslation(animControl.getSpatial().getParent().getLocalTranslation().add(0f,0.1f,0f));
        }
    }

    @Override
    public void onAnimChange(AnimControl ac, AnimChannel ac1, String string) {
        /*if(string.equals("IdleStanding")) {
         Vector3f center = animControl.getSpatial().getParent().getWorldBound().getCenter();
         animControl.getSpatial().setLocalTranslation(new Vector3f(0f, -center.getY(), 0f)); 
         }*/
    }
    
}
