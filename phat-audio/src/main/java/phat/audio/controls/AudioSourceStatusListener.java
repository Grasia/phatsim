/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.audio.controls;

import com.jme3.audio.AudioNode;

/**
 *
 * @author pablo
 */
public interface AudioSourceStatusListener {
    public void AudioStatusChanged(AudioNode audioSource);
}
