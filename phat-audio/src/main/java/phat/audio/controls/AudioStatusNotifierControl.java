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
package phat.audio.controls;

import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioSource;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 * Checks if the AudioSource that controls has changed its status and notifies
 * to the listener.
 * 
 * @author pablo
 */
public class AudioStatusNotifierControl extends AbstractControl {
    protected AudioSource.Status audioStatus;
    protected AudioSourceStatusListener audioSourceStateListener;    

    public void setAudioSourceStateListener(AudioSourceStatusListener audioSourceStateListener) {
        this.audioSourceStateListener = audioSourceStateListener;
    }
    
    @Override
    protected void controlUpdate(float f) {
        if(audioSourceStateListener != null && spatial != null) {
            if(spatial instanceof AudioNode) {
                AudioNode as = (AudioNode) spatial;
                if(hasChanged(as)) {
                    audioSourceStateListener.AudioStatusChanged(as);
                }
            }
        }
    }
    
    private boolean hasChanged(AudioSource as) {
        if(audioStatus == null) {
            audioStatus = as.getStatus();
            return false;
        } else if(audioStatus != as.getStatus()) {
            audioStatus = as.getStatus();
            return true;
        }
        return false;
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        
    }
    
}
