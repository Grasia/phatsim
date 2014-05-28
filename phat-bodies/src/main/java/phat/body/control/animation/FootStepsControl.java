/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.body.control.animation;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import phat.audio.AudioFactory;
import phat.audio.PHATAudioSource;

/**
 *
 * @author pablo
 */
public class FootStepsControl extends AbstractControl {

    private int step = -1;

    @Override
    public void setSpatial(Spatial spatial) {
        if(spatial == null) {
            AudioNode audioSource = getAudioSource();
            if(audioSource != null) {
                audioSource.stop();
            }
        }
        super.setSpatial(spatial);
    }

    /*private AudioSource createAudioFootSteps() {
        AudioSource as = AudioFactory.getInstance().makeAudioSource("FootSteps", footSteps1, Vector3f.ZERO);
        as.setLooping(false);
        as.setPositional(true);
        as.setDirectional(false);
        as.setVolume(0.2f);
        as.setMaxDistance(Float.MAX_VALUE);   
        as.setRefDistance(0.5f);

        //as.setShowRange(true);
        
        return as;
    }*/

    private AudioNode getAudioSource() {
        Spatial s = ((Node)spatial).getChild("FeetAudioNode");
        AudioNode audioSource = null;
        if(s != null && s instanceof AudioNode) {
            audioSource = (AudioNode)s;
        }
        return audioSource;
    }
    
    @Override
    protected void controlUpdate(float f) {
        AudioNode audioSource = getAudioSource();
        if(audioSource == null) {
            return;
        }
        BasicCharacterAnimControl basicAnimControl = spatial.getControl(BasicCharacterAnimControl.class);
        if (basicAnimControl.getDownAnimationName().equals(
                BasicCharacterAnimControl.AnimName.WalkForward.name())) {
            if (step == -1) {
                step = 1;
            }
            switch (step) {
                case 1:
                    if (basicAnimControl.getAnimTime() < 1.12f && basicAnimControl.getAnimTime() > 0.25f) {                        
                        audioSource.playInstance();
                        step = 2;
                    }
                    break;
                case 2:
                    if (basicAnimControl.getAnimTime() > 1.12f) {
                        audioSource.playInstance();
                        step = 1;
                    }
                    break;
            }
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    @Override
    public Control cloneForSpatial(Spatial sptl) {
        return new FootStepsControl();
    }
}
