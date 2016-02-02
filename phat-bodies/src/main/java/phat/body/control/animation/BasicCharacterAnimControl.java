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
package phat.body.control.animation;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.jme3.bullet.control.KinematicRagdollControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import phat.body.control.physics.PHATCharacterControl;

/**
 * Basic animation handler for an character who has a PHATCharacterControl. This
 * monitorizes the character using its PHATCharacterControl. If the character is
 * moving the controler sets WalkForward animation else sets IdleStanding
 * animation.
 *
 * @author Pablo
 */
public class BasicCharacterAnimControl extends AbstractControl implements
        AnimEventListener {

    private AnimControl animControl;
    private AnimChannel upperBodyChannel;
    private AnimChannel downBodyChannel;
    private Map<AnimName, AnimFinishedListener> listeners = new HashMap<>();
    float blend = 0.5f;

    public enum AnimName {

        WalkForward, WaveAttention, LookBehindL, SwimTreadwater, SittingOnGround, LeverPole, LookBehindR, IdleStanding, SawGround, ScratchArm, StandUpGround, Yawn, RunForward, SpinSpindle, EatStanding, DrinkStanding, SitDownGround, Wave, Hand2Belly, Sweeping, Sweeping1, Hands2Hips
    }
    protected BasicCharacterAnimControl.AnimName manualAnimation = null;

    private PHATCharacterControl getCharacterControl() {
        return spatial.getControl(PHATCharacterControl.class);
    }

    private boolean isRagdollEnabled() {
        KinematicRagdollControl krc = spatial.getControl(KinematicRagdollControl.class);
        if (krc != null && krc.getMode().equals(KinematicRagdollControl.Mode.Ragdoll)) {
            return true;
        }
        return false;
    }

    private void initChannels() {
        animControl = spatial.getControl(AnimControl.class);
        upperBodyChannel = animControl.createChannel();
        upperBodyChannel.setLoopMode(LoopMode.DontLoop);
        upperBodyChannel.addFromRootBone("LowerBack");

        downBodyChannel = animControl.createChannel();
        downBodyChannel.setLoopMode(LoopMode.DontLoop);
        downBodyChannel.addFromRootBone("RightUpLeg");
        downBodyChannel.addFromRootBone("LeftUpLeg");
        downBodyChannel.addBone("Hips");
        downBodyChannel.addBone("Root");

        animControl.addListener(this);
    }

    public void standUpAnimation(AnimFinishedListener listener) {
        listeners.put(AnimName.StandUpGround, listener);
        spatial.updateModelBound();
        Vector3f center = animControl.getSpatial().getWorldBound().getCenter();

        Vector3f v = new Vector3f();
        v.set(center/* patial.getLocalTranslation() */);
        // v.y = 0;
        spatial.setLocalTranslation(v);
        Quaternion q = new Quaternion();
        float[] angles = new float[3];
        spatial.getLocalRotation().toAngles(angles);
        q.fromAngleAxis(angles[1], Vector3f.UNIT_Y);
        spatial.setLocalRotation(q);

        this.manualAnimation = AnimName.StandUpGround;

        upperBodyChannel.setAnim(manualAnimation.name(), 1f);
        upperBodyChannel.setLoopMode(LoopMode.DontLoop);
        downBodyChannel.setAnim(manualAnimation.name(), 1f);
        downBodyChannel.setLoopMode(LoopMode.DontLoop);
    }

    public String getDownAnimationName() {
        String name = downBodyChannel.getAnimationName();
        if (name != null) {
            return name;
        } else {
            return "";
        }
    }

    private void setAnim(String animName, AnimChannel bodyChannel) {
        setAnim(animName, bodyChannel, 0.5f);
    }

    private void setAnim(String animName, AnimChannel bodyChannel, float blend) {
        if (bodyChannel.getAnimationName() == null
                || !bodyChannel.getAnimationName().equals(animName)) {
            bodyChannel.setAnim(animName, blend);
        }
    }

    public float getAnimTime() {
        return upperBodyChannel.getTime();
    }

    public boolean hasAnimation(String animationName) {
        return animControl.getAnim(animationName) != null;
    }

    public Collection<String> getAnimations() {
        return animControl.getAnimationNames();
    }

    @Override
    public void controlUpdate(float tpf) {
        if (upperBodyChannel == null) {
            initChannels();
        }
        if (isRagdollEnabled()) {
            return;
        }
        float speed = 0f;
        // If is ragdoll activated this control should be desactivated
        if (getCharacterControl().isEnabled()) {
            speed = getCharacterControl().getWalkDirection().length();
            if (speed > 0.1f) {
                if (speed < 2f) {
                    setAnim("WalkForward", downBodyChannel);
                    setAnim("WalkForward", upperBodyChannel);
                    downBodyChannel.setSpeed(speed);
                    upperBodyChannel.setSpeed(speed);
                } else {
                    setAnim("RunForward", downBodyChannel);
                    setAnim("RunForward", upperBodyChannel);
                    downBodyChannel.setSpeed(speed);
                    upperBodyChannel.setSpeed(speed);
                }
            } else {
                setAnim("IdleStanding", downBodyChannel);
                setAnim("IdleStanding", upperBodyChannel);
                downBodyChannel.setSpeed(1f);
                upperBodyChannel.setSpeed(1f);
            }

        }
        if (manualAnimation != null && !manualAnimation.name().equals(upperBodyChannel.getAnimationName())) {
            upperBodyChannel.setSpeed(1f);
            setAnim(manualAnimation.name(), upperBodyChannel);
            if (getCharacterControl().isEnabled() || getCharacterControl().getWalkDirection().length() <= 0) {
                downBodyChannel.setSpeed(1f);
                setAnim(manualAnimation.name(), downBodyChannel);
            }
        } else if (manualAnimation == null && speed < 0.1f) {
            setAnim("IdleStanding", downBodyChannel);
            setAnim("IdleStanding", upperBodyChannel);
            downBodyChannel.setSpeed(1f);
            upperBodyChannel.setSpeed(1f);
        }
    }

    public BasicCharacterAnimControl.AnimName getManualAnimation() {
        return this.manualAnimation;
    }

    public void setManualAnimation(
            BasicCharacterAnimControl.AnimName manualAnimation,
            AnimFinishedListener listener) {
        listeners.put(manualAnimation, listener);
        System.out.println("SetManualAnimationName = "+manualAnimation);
        this.manualAnimation = manualAnimation;
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
        if (manualAnimation != null && manualAnimation.name().equals(string)) {
            AnimName anim = AnimName.valueOf(string);
            AnimFinishedListener listener = listeners.get(anim);
            if (listener != null) {
                listeners.remove(anim);
                listener.animFinished(manualAnimation);
            }
            manualAnimation = null;
        }
    }

    @Override
    public void onAnimChange(AnimControl ac, AnimChannel ac1, String string) {
        /*
         * if(string.equals("IdleStanding")) { Vector3f center =
         * animControl.getSpatial().getParent().getWorldBound().getCenter();
         * animControl.getSpatial().setLocalTranslation(new Vector3f(0f,
         * -center.getY(), 0f)); }
         */
    }
}
