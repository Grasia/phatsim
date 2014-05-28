/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.devices.extractor;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.control.CameraControl;
import com.jme3.scene.shape.Sphere;
import phat.audio.SimpleAudioScenario;
import phat.audio.listeners.PCSpeaker;
import phat.audio.listeners.XYRMSAudioChart;
import phat.sensors.Sensor;
import phat.sensors.microphone.MicrophoneControl;
import phat.util.SpatialFactory;

/**
 *
 * Demonstrates advanced use of the audio capture and recording features.
 * Multiple perspectives of the same scene are simultaneously rendered to
 * different sound files.
 *
 * A key limitation of the way multiple listeners are implemented is that only
 * 3D positioning effects are realized for listeners other than the main LWJGL
 * listener. This means that audio effects such as environment settings will
 * *not* be heard on any auxiliary listeners, though sound attenuation will work
 * correctly.
 *
 * Multiple listeners as realized here might be used to make AI entities that
 * can each hear the world from their own perspective.
 *
 * @author Robert McIntyre
 */
public class TestAudioExtractor extends SimpleAudioScenario {

    public static void main(String[] args) {
        TestAudioExtractor app = new TestAudioExtractor();
        app.setShowSettings(false);
        app.setPauseOnLostFocus(false);

        /*
         try {
         //Capture.captureVideo(app, File.createTempFile("advanced", ".avi"));
         Capture.captureAudio(app, File.createTempFile("advanced", ".wav"));
         } catch (IOException e) {
         e.printStackTrace();
         }*/

        app.start();
    }
    private MicrophoneControl micControl;
    
    private void createScene() {
        phat.util.Debug.enableDebugGrid(10f, assetManager, rootNode);
        
        
    }
    

    @Override
    public void createCameras() {
        this.cam.setLocation(new Vector3f(-28.00242f, 48.005623f, -34.648228f));
        this.cam.setRotation(new Quaternion(0.3359635f, 0.34280345f, -0.13281013f, 0.8671653f));
        
        super.flyCam.setMoveSpeed(20f);
    }

    @Override
    public void initAudio() {
        
        
        
    }

    private void createExtractor(float x, float y, float z) {
        Extractor extractor = new Extractor();
        extractor.getExtractorControl().switchTo(ExtractorControl.State.HIGH);
        extractor.attachChild(SpatialFactory.createShape("ExtractorGeo", new Sphere(15, 15, 1), ColorRGBA.Blue));
        extractor.setLocalTranslation(x, y, z);
        rootNode.attachChild(extractor);
    }
    
    public void setCameraAsListener() {
        Node camFollower = new Node();
        // means that the Camera's transform is "copied" to the Transform of the Spatial.
        CameraControl cc = new CameraControl(cam, CameraControl.ControlDirection.CameraToSpatial);
        camFollower.addControl(cc);
        rootNode.attachChild(camFollower);
        camFollower.addControl(micControl);
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        super.simpleUpdate(tpf);
        
        //Vector3f loc = cam.getLocation();
        //Quaternion rot = cam.getRotation();
        //listener.setLocation(loc);
        //listener.setRotation(rot);
    }
    
    @Override
    public void destroy() {
        super.destroy();
        
        System.out.println("destroy()");
        ((Sensor)micControl).cleanUp();
    }

    @Override
    public void createTerrain() {
        createScene();
    }

    @Override
    public void createOtherObjects() {  
        SpatialFactory.init(assetManager, rootNode);
        
        createExtractor(-20, 0, -20);
        createExtractor(-20, 0, 20);
        createExtractor(20, 0, -20);
        createExtractor(20, 0, 20);
        
        micControl = new MicrophoneControl("Micro1", 10000, audioRenderer);

        XYRMSAudioChart chart = new XYRMSAudioChart("RMS");
        micControl.add(chart);
        chart.showWindow();
        
        /*
        try {
            AudioSourceWaveFileWriter aswfw = 
                    new AudioSourceWaveFileWriter(new File("sound.wav"));
            micControl.add(aswfw);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/


        PCSpeaker pcSpeaker = new PCSpeaker();
        micControl.add(pcSpeaker);
        
        setCameraAsListener();
    }
}