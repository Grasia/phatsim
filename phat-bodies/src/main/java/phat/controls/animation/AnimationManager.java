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
import com.jme3.animation.Animation;
import com.jme3.animation.Bone;
import com.jme3.animation.BoneTrack;
import com.jme3.animation.LoopMode;
import com.jme3.animation.Track;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import phat.agents.actors.ActorFactory;

/**
 *
 * @author Pablo
 */
public class AnimationManager extends AbstractControl implements AnimEventListener {
    private AnimControl animControl;
    protected CharacterControl characterControl;
    
    private AnimChannel fullBodyChannel;
    private AnimChannel headChannel;
    private AnimChannel leftArmChannel;
    private AnimChannel rightArmChannel;
    private AnimChannel leftLegChannel;
    private AnimChannel rightLegChannel;
    
    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);

        if (spatial != null) {
            animControl = ActorFactory.findControl(spatial, AnimControl.class);
            characterControl = ActorFactory.findControl(spatial, CharacterControl.class);
            if (animControl != null && characterControl != null) {
                initChannels();
                animControl.addListener(this);                
            }            
        }
    }
    
    public void standUpAnimation() {
        setAnimationFullBody("StandUpGround", 0.5f, LoopMode.DontLoop);
        animControl.getSpatial().getParent().setLocalTranslation(animControl.getSpatial().getWorldTranslation().add(0f, 0.1f, 0f));
        animControl.getSpatial().setLocalTranslation(Vector3f.ZERO);
    }

    public String getCurrentAnimationName() {
        return headChannel.getAnimationName();
    }
    
    @Override
    public void controlUpdate(float tpf) {
        if (characterControl == null) {
            return;
        }
        String currentAnim = getCurrentAnimationName();
        if(currentAnim != null && currentAnim.equals("StandUpGround"))
            return;
        
        if (!characterControl.onGround()) {
            /*if(!"JumpLoop".equals(torsoChannel.getAnimationName()))
             torsoChannel.setAnim("JumpLoop");
             if(!"JumpLoop".equals(feetChannel.getAnimationName()))
             feetChannel.setAnim("JumpLoop");
             return;*/
        }
        
        if (characterControl.getWalkDirection().length() > 0) {
            if (!"WalkForward".equals(headChannel.getAnimationName())) {
                headChannel.setAnim("WalkForward", 0.5f);
            }
        } else {
            if (!"IdleStanding".equals(headChannel.getAnimationName())) {
                headChannel.setAnim("IdleStanding", 0.5f);
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

    @Override
    public void onAnimCycleDone(AnimControl ac, AnimChannel ac1, String string) {
        if(string.equals("StandUpGround")) {
            characterControl.setWalkDirection(Vector3f.ZERO);
            characterControl.setEnabled(true);            
            headChannel.setAnim("IdleStanding", 0.5f);
            //Vector3f center = animControl.getSpatial().getWorldBound().getCenter();
            animControl.getSpatial().setLocalTranslation(new Vector3f(0f, -1f, 0f));
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
    
    public static void setBonesSuchAs(AnimControl animControl, String animationName) {
        Animation animation = animControl.getAnim(animationName);
        int size = animControl.getSkeleton().getBoneCount();
        for(int index = 0; index < size; index++) {
            Bone bone = animControl.getSkeleton().getBone(index);
            BoneTrack boneTrack = getFirstTrackForBone(animation, index);
            if(boneTrack != null) {
                Quaternion rotation = boneTrack.getRotations()[0];
                Vector3f translation = boneTrack.getTranslations()[0];
                Vector3f scale = boneTrack.getScales()[0];
                System.out.println("Bone = "+bone.getName());
                System.out.println("\tRotation = "+rotation);
                System.out.println("\tTranslation = "+translation);
                System.out.println("\tScale = "+scale);
                bone.setUserControl(true);
                bone.setUserTransforms(translation, rotation, scale);
                bone.updateWorldVectors();
                bone.setUserControl(false);
            }
        }
    }
    
    private static BoneTrack getFirstTrackForBone(AnimControl animControl, Animation animation, String boneName) {
        int boneIndex = animControl.getSkeleton().getBoneIndex(boneName);
        return getFirstTrackForBone(animation, boneIndex);
    }
    
    private static BoneTrack getFirstTrackForBone(Animation animation, int boneIndex) {
        BoneTrack result = null;
        for(Track track: animation.getTracks()) {            
            if(track instanceof BoneTrack) {
                BoneTrack boneTrack = (BoneTrack) track;
                if(boneIndex == boneTrack.getTargetBoneIndex()) {
                    return boneTrack;
                }
            }
        }
        return result;
    }
    
    private void initChannels() {
        fullBodyChannel = animControl.createChannel();
        headChannel.setLoopMode(LoopMode.Loop);
        
        headChannel = animControl.createChannel();
        headChannel.setLoopMode(LoopMode.Loop);
        
        leftArmChannel = animControl.createChannel();
        leftArmChannel.setLoopMode(LoopMode.Loop);
        
        rightArmChannel = animControl.createChannel();
        rightArmChannel.setLoopMode(LoopMode.Loop);
        
        leftLegChannel = animControl.createChannel();
        leftLegChannel.setLoopMode(LoopMode.Loop);
        
        rightLegChannel = animControl.createChannel();
        rightLegChannel.setLoopMode(LoopMode.Loop);
        
    }
    
    private void setAnimationFullBody(String animationName, float blendTime, LoopMode loopMode) {
        animControl.clearChannels();
        fullBodyChannel = animControl.createChannel();
        fullBodyChannel.setLoopMode(loopMode);
        fullBodyChannel.setAnim(animationName, blendTime);
    }
    
    private AnimChannel createNeckChannel() {
        AnimChannel channel = animControl.createChannel();
        channel.addFromRootBone("Neck");
        return channel;
    }
    
    private void createArmChannels() {
        rightArmChannel = createRightArmChannels();
        leftArmChannel = createLeftArmChannels();
    }
    
    private AnimChannel createLeftArmChannels() {
        AnimChannel channel = animControl.createChannel();
        channel.addFromRootBone("LeftShoulder");
        return channel;
    }
    
    private AnimChannel createRightArmChannels() {
        AnimChannel channel = animControl.createChannel();
        channel.addFromRootBone("RightShoulder");
        return channel;
    }
    
    private void addAllBonesButNotArms(AnimChannel channel) {
        channel.addBone("Root");
        channel.addFromRootBone("RightUpLeg");
        channel.addFromRootBone("LeftUpLeg");
        channel.addToRootBone("RightShoulder");
        channel.addToRootBone("LeftShoulder");
        channel.addToRootBone("Neck");
    }
    
    private void addAllBonesButNotArmsNotHead(AnimChannel channel) {
        channel.addBone("Root");
        channel.addFromRootBone("RightUpLeg");
        channel.addFromRootBone("LeftUpLeg");
        channel.addToRootBone("RightShoulder");
        channel.addToRootBone("LeftShoulder");
    }
}
