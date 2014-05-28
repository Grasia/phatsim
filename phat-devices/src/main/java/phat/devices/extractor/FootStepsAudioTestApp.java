/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.devices.extractor;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.control.CameraControl;
import phat.audio.AudioFactory;
import phat.audio.PHATAudioSource;
import phat.audio.AudioSpeakerSource;
import phat.audio.listeners.PCSpeaker;
import phat.audio.listeners.XYRMSAudioChart;
import phat.audio.util.AudioSimpleScenario;
import phat.sensors.microphone.MicrophoneControl;
import phat.util.Debug;

/**
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
        AudioSpeakerSource sound = AudioFactory.getInstance().makeAudioSpeakerSource("Hello!", "Hello!", Vector3f.ZERO);
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
    
    private void sound(String resource) {
        PHATAudioSource sound = AudioFactory.getInstance().makeAudioSource(
                "ExtractorAudio", 
                resource, 
                Vector3f.ZERO, true);
        sound.setLooping(true);
        sound.setShowRange(true);

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
        sound("Sound/HumanEffects/FootSteps/footsteps2.ogg");
        sound("Sound/Devices/Extractor/extractor-pow3.ogg");
        speak();
        //as.setShowRange(true);
        
        flyCam.setMoveSpeed(100f);
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
