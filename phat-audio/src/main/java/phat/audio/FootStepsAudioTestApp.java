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
package phat.audio;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.control.CameraControl;
import phat.audio.listeners.PCSpeaker;
import phat.audio.listeners.XYRMSAudioChart;
import phat.audio.util.AudioSimpleScenario;
import phat.sensors.microphone.MicrophoneControl;
import phat.util.Debug;

/**
 * Scenario: Four audio sources playing feet steps.
 * The audio listener is attached to the camera and
 * volume changes are perceived by moving the camera.
 * Also, the audio level intesity is shown in a chart.
 * 
 * @author pablo
 */
public class FootStepsAudioTestApp extends AudioSimpleScenario{

    public static void main(String[] args) {
        FootStepsAudioTestApp app = new FootStepsAudioTestApp();
        app.setDisplayFps(false);
        //app.setShowSettings(false);
        app.setPauseOnLostFocus(false);
        app.setDisplayStatView(false);

        //recordVideoAndAudio(app);

        /*
         try {
         File video = new File("video.avi");
         File audio = new File("audio.wav");
        
         Capture.captureVideo(app, video);
         Capture.captureAudio(app, audio);
         } catch (IOException ex) {
         Logger.getLogger(FootStepsControlTestApp.class.getName()).log(Level.SEVERE, null, ex);
         }*/

        app.start();
    }
    
    private void speak() {
        AudioSpeakerSource sound = AudioFactory.getInstance().makeAudioSpeakerSource("Hello!","Hello!", Vector3f.ZERO);
        sound.setLooping(true);
        sound.setShowRange(true);

        rootNode.attachChild(sound);
        //audioRenderer.playSource(music);
        sound.setPositional(true);
        sound.setVolume(0.5f);
        sound.setReverbEnabled(false);
        sound.setDirectional(false);
        sound.setMaxDistance(Float.MAX_VALUE);
        sound.setRefDistance(2f);
        sound.play();
    }
    
    private void sound(String resource, float x, float y, float z) {
        PHATAudioSource sound = AudioFactory.getInstance().makeAudioSource(
                "ExtractorAudio", 
                resource, 
                Vector3f.ZERO, true);
        sound.setLooping(true);
        sound.setShowRange(true);
        sound.setLocalTranslation(x, y, z);

        rootNode.attachChild(sound);
        
        sound.setPositional(true);
        sound.setVolume(0.5f);
        sound.setReverbEnabled(false);
        sound.setDirectional(false);
        sound.setMaxDistance(Float.MAX_VALUE);
        sound.setRefDistance(2f);
        sound.play();
    }
    @Override
    protected void createAudio() {
        sound("Sound/HumanEffects/FootSteps/footsteps1.ogg", -5f, 0f, -5f);
        sound("Sound/HumanEffects/FootSteps/footsteps1.ogg", -5f, 0f, 5f);
        sound("Sound/HumanEffects/FootSteps/footsteps1.ogg", 5f, 0f, -5f);
        sound("Sound/HumanEffects/FootSteps/footsteps1.ogg", 5f, 0f, 5f);
        //as.setShowRange(true);
        
        flyCam.setMoveSpeed(10f);
        
        Node camFollower = new Node();
        // means that the Camera's transform is "copied" to the Transform of the Spatial.
        CameraControl cc = new CameraControl(cam, CameraControl.ControlDirection.CameraToSpatial);
        camFollower.addControl(cc);
        rootNode.attachChild(camFollower);

        MicrophoneControl micControl = new MicrophoneControl("Micro1", 10000, audioRenderer);
        camFollower.addControl(micControl);

        PCSpeaker pcSpeaker = new PCSpeaker();
        micControl.add(pcSpeaker);
        
        XYRMSAudioChart chart = new XYRMSAudioChart("RMS");
        micControl.add(chart);
        chart.showWindow();
    }

    @Override
    public void createTerrain() {
        Debug.enableDebugGrid(10, assetManager, rootNode);
    }

    @Override
    public void createOtherObjects() {
        
    }
    
}
