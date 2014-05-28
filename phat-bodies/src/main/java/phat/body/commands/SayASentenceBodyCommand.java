/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.body.commands;

import com.jme3.app.Application;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioSource;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import java.util.logging.Level;

import phat.audio.AudioAppState;
import phat.audio.AudioFactory;
import phat.audio.AudioSpeakerSource;
import phat.audio.controls.AudioSourceStatusListener;
import phat.audio.controls.AudioStatusNotifierControl;
import phat.body.BodiesAppState;
import phat.body.control.physics.PHATCharacterControl;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.commands.PHATCommand.State;

/**
 *
 * @author pablo
 */
public class SayASentenceBodyCommand extends PHATCommand implements AudioSourceStatusListener {

    private String bodyId;
    private String message;
    private float volume = 1.0f;
    AudioSpeakerSource speaker;
    
    public SayASentenceBodyCommand(String bodyId, String message, PHATCommandListener listener) {
        super(listener);
        this.bodyId = bodyId;
        this.message = message;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }
    
    public SayASentenceBodyCommand(String bodyId, String message) {
        this(bodyId, message, null);
    }

    @Override
    public void runCommand(Application app) {
        if (app.getStateManager().getState(AudioAppState.class) != null) {

            BodiesAppState bodiesAppState = app.getStateManager().getState(BodiesAppState.class);
            Node body = bodiesAppState.getAvailableBodies().get(bodyId);

            if (body != null && body.getParent() != null) {
                removeAudioSpeakerSource(body);

                speaker =
                        AudioFactory.getInstance().makeAudioSpeakerSource(message, message, Vector3f.ZERO);
                speaker.setVolume(volume);
                speaker.setLooping(false);
                speaker.setRefDistance(2f);

                body.attachChild(speaker);

                speaker.play();
                
                AudioStatusNotifierControl asnc = new AudioStatusNotifierControl();
                asnc.setAudioSourceStateListener(this);
                speaker.addControl(asnc);
                
                return;
            }
        }
        setState(State.Fail);
    }

    @Override
	public void interruptCommand(Application app) {
        if (speaker != null) {
        	speaker.removeFromParent();
        	speaker.getControl(AudioStatusNotifierControl.class).setAudioSourceStateListener(null);
        	speaker.stop();
        	setState(State.Interrupted);
        	return;
        }
        setState(State.Fail);
	}
    
    private void removeAudioSpeakerSource(Node body) {
        for (Spatial s : body.getChildren()) {
            if (s instanceof AudioSpeakerSource) {
                s.removeFromParent();
            }
        }
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }
    
    public String getMessage() {
    	return message;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + bodyId + ",message=" + message + ")";
    }

    @Override
    public void AudioStatusChanged(AudioNode audioNode) {
        if(audioNode.getStatus() == AudioSource.Status.Stopped) {
            audioNode.removeFromParent();
            setState(State.Success);
        }
    }
}
