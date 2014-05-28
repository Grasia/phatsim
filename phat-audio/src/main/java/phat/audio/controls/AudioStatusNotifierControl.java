/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
