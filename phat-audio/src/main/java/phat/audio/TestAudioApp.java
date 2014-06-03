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

import com.aurellem.capture.Capture;
import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioNode;
import com.jme3.audio.Listener;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.control.CameraControl;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import phat.audio.listeners.PCSpeaker;
import phat.audio.util.AudioSimpleScenario;
import phat.sensors.microphone.MicrophoneControl;
import phat.util.Debug;

/**
 *
 * @author pablo
 */
public class TestAudioApp extends AudioSimpleScenario {

    private static String footSteps1 = "Sound/HumanEffects/FootSteps/footsteps1.ogg";
    private static String fallingDown2 = "Sound/HumanEffects/FallingDown/fallingDown2.ogg";
    
    private AudioNode audioNode;
    private MicrophoneControl micControl;
    
    public static void main(String[] args) {
        TestAudioApp app = new TestAudioApp();
                
        //app.setDisplayFps(false);
        app.setShowSettings(false);
        app.setPauseOnLostFocus(false);
        
        //recordVideoAndAudio(app);
        
       
        /*try {
            File video = new File("video.avi");
            File audio = new File("audio.wav");
        
            Capture.captureVideo(app, video);
            Capture.captureAudio(app, audio);
        } catch (IOException ex) {
            Logger.getLogger(TestAudioApp.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        
        app.start();
    }
    
    float acumulative = 0f;
    float seconds = 0f;
    int numSeconds = 0;
    
    @Override
    public void simpleUpdate(float tpf) {
        super.simpleUpdate(tpf);
        
        acumulative+=tpf;
        seconds += tpf;
        
        if(seconds > 1f) {
            System.out.println(numSeconds++);
            seconds = 0f;
        }
        
        if(numSeconds > 10) {
            System.out.println("Play!");
            audioNode.play();
            numSeconds = 0;
        }
        
    }
    
    @Override
    protected void createAudio() {
        audioNode = new AudioNode(assetManager, fallingDown2);
        audioNode.setLooping(false);
        audioNode.setVolume(1);
        audioNode.setRefDistance(10f);
        audioNode.setPositional(true);
        audioNode.setMaxDistance(10000000);
        audioNode.setLocalTranslation(Vector3f.ZERO);
        
        Box box1 = new Box(1, 1, 1);
        Geometry player = new Geometry("Player", box1);
        Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", ColorRGBA.Blue);
        player.setMaterial(mat1);
        audioNode.attachChild(player);
        
        rootNode.attachChild(audioNode);
        
        
        Node camFollower = new Node();
        // means that the Camera's transform is "copied" to the Transform of the Spatial.
        CameraControl cc = new CameraControl(cam, CameraControl.ControlDirection.CameraToSpatial);
        camFollower.addControl(cc);
        rootNode.attachChild(camFollower);
        
        micControl = new MicrophoneControl("Micro1", 10000, audioRenderer);
        camFollower.addControl(micControl);
        
        PCSpeaker pcSpeaker = new PCSpeaker();
        micControl.add(pcSpeaker);
    }

    @Override
    public void createTerrain() {        
        flyCam.setMoveSpeed(40);
        Debug.enableDebugGrid(10f, assetManager, rootNode);        
    }


    @Override
    public void createOtherObjects() {
        
    }
}
