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
package phat.body.commands;

import com.jme3.app.Application;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioSource;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import java.util.logging.Level;

import phat.audio.AudioFactory;
import phat.audio.AudioSpeakerSource;
import phat.audio.PHATAudioAppState;
import phat.audio.controls.AudioSourceStatusListener;
import phat.audio.controls.AudioStatusNotifierControl;
import phat.body.BodiesAppState;
import phat.commands.PHATCommParam;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.commands.PHATCommand.State;
import phat.commands.PHATCommandAnn;

/**
 *
 * @author pablo
 */
@PHATCommandAnn(name = "SayASentence", type = "body", debug = false)
public class SayASentenceBodyCommand extends PHATCommand implements AudioSourceStatusListener {

    private String bodyId;
    private String message;
    private float volume = 1.0f;
    AudioSpeakerSource speaker;
    SetTextOnTopOfBody textBalloon;
    BodiesAppState bodiesAppState;

    public SayASentenceBodyCommand() {
    }

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
        if (app.getStateManager().getState(PHATAudioAppState.class) != null) {

            bodiesAppState = app.getStateManager().getState(BodiesAppState.class);
            Node body = bodiesAppState.getBody(bodyId);

            if (body != null && body.getParent() != null) {
                removeAudioSpeakerSource(body);

                speaker = AudioFactory.getInstance().makeAudioSpeakerSource(message, message, Vector3f.ZERO);
                speaker.setVolume(volume);
                speaker.setLooping(false);
                speaker.setRefDistance(2f);

                body.attachChild(speaker);

                speaker.play();

                AudioStatusNotifierControl asnc = new AudioStatusNotifierControl();
                asnc.setAudioSourceStateListener(this);
                speaker.addControl(asnc);

                textBalloon = new SetTextOnTopOfBody(bodyId, message, true);
                textBalloon.setSeconds(5f);
                bodiesAppState.runCommand(textBalloon);

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

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + bodyId + ",message=" + message + ")";
    }

    @Override
    public void AudioStatusChanged(AudioNode audioNode) {
        if (audioNode.getStatus() == AudioSource.Status.Stopped) {
            audioNode.removeFromParent();
            
            setState(State.Success);
        }
    }

    @PHATCommParam(mandatory = true, order = 1)
    public void setBodyId(String bodyId) {
        this.bodyId = bodyId;
    }

    @PHATCommParam(mandatory = true, order = 2)
    public void setMessage(String message) {
        this.message = message;
    }

    @PHATCommParam(mandatory = false, order = 3)
    public void setVolume(float volume) {
        this.volume = volume;
    }
}
