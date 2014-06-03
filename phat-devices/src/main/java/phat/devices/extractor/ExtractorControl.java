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
