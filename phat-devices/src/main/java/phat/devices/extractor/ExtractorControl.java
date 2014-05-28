/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.devices.extractor;

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
public class ExtractorControl extends AbstractControl {

    public enum State {
        LOW, MEDIUM, HIGH, OFF
    };
    
    private PHATAudioSource sound;
    private State state = State.OFF;
    private State newState = State.OFF;
    private boolean enabledRange = false;
    
    public void switchTo(State s) {
        newState = s;
    }

    public void showRange(boolean enabled) {
        enabledRange = enabled;
    }
    
    @Override
    protected void controlUpdate(float f) {
        if (state != newState) {
            switch (newState) {
                case LOW:
                    play("Sound/Devices/Extractor/extractor-pow1.ogg");
                    break;
                case MEDIUM:
                    play("Sound/Devices/Extractor/extractor-pow2.ogg");
                    break;
                case HIGH:
                    play("Sound/Devices/Extractor/extractor-pow3.ogg");
                    break;
                case OFF:
                    removeSound();
                    break;
            }
            state = newState;
        }
    }

    private void removeSound() {
        if(sound != null) {
            sound.stop();
            sound.removeFromParent();
        }
    }
    
    private void play(String resource) {
        removeSound();
        
        sound = AudioFactory.getInstance().makeAudioSource("ExtractorAudio", resource, Vector3f.ZERO, true);
        sound.setLooping(true);
        sound.setShowRange(true);

        ((Node)getSpatial()).attachChild(sound);
        //audioRenderer.playSource(music);
        sound.setPositional(true);
        sound.setVolume(0.7f);
        sound.setReverbEnabled(false);
        sound.setDirectional(false);
        sound.setMaxDistance(Float.MAX_VALUE);
        sound.setRefDistance(1f);
        //music.setRolloffFactor(1f);
        //music.setLooping(false);
        
        sound.setShowRange(enabledRange);
        sound.play();
    }
    
    public PHATAudioSource getAudioSource() {
        return sound;
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    @Override
    public Control cloneForSpatial(Spatial sptl) {
        ExtractorControl ec = new ExtractorControl();
        ec.state = state;
        ec.newState = newState;
        
        return ec;
    }
}
