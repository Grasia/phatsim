/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.audio;

import com.aurellem.capture.IsoTimer;
import com.jme3.audio.AudioSource;
import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.events.AbstractCinematicEvent;
import com.jme3.cinematic.events.MotionTrack;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeSystem;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import phat.audio.listeners.AudioSourceWaveFileWriter;
import phat.audio.listeners.XYRMSAudioChart;
import phat.audio.util.AudioSimpleScenario;
import phat.sensors.Sensor;
import phat.sensors.microphone.MicrophoneControl;

/**
 *
 * @author pablo
 */
public class TestAurellemAudio extends AudioSimpleScenario {

    private Geometry bell;
    private AudioSpeakerSource music;
    private IsoTimer motionTimer = new IsoTimer(isoTimer.getTimePerFrame());
    private MotionTrack motionControl;
    private MicrophoneControl micControl;
    
    public static void main(String[] args) {
        TestAurellemAudio app = new TestAurellemAudio();

        /*
         try {
         //Capture.captureVideo(app, File.createTempFile("advanced", ".avi"));
         Capture.captureAudio(app, File.createTempFile("advanced", ".wav"));
         } catch (IOException e) {
         e.printStackTrace();
         }*/

        app.start();
    }
    
    @Override
    public void createTerrain() {
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        bell = new Geometry("sound-emitter", new Sphere(15, 15, 1));
        mat.setColor("Color", ColorRGBA.Blue);
        bell.setMaterial(mat);
        rootNode.attachChild(bell);

        MotionPath track = new MotionPath();

        for (Vector3f v : path) {
            track.addWayPoint(v);
        }
        track.setCurveTension(0.80f);

        motionControl = new MotionTrack(bell, track);
        // for now, use reflection to change the timer... 
        // motionControl.setTimer(new IsoTimer(60));


        try {
            Field timerField;
            timerField = AbstractCinematicEvent.class.getDeclaredField("timer");
            timerField.setAccessible(true);
            try {
                timerField.set(motionControl, motionTimer);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }


        motionControl.setDirectionType(MotionTrack.Direction.PathAndRotation);
        motionControl.setRotation(new Quaternion().fromAngleNormalAxis(-FastMath.HALF_PI, Vector3f.UNIT_Y));
        motionControl.setInitialDuration(20f);
        motionControl.setSpeed(0.1f);

        track.enableDebugShape(assetManager, rootNode);
    }

    @Override
    public void createOtherObjects() {
        Geometry geo1 = makeEar(rootNode, new Vector3f(0, 0, -20));

        micControl = new MicrophoneControl("Micro1", 10000, audioRenderer);
        geo1.addControl(micControl);

        XYRMSAudioChart chart = new XYRMSAudioChart("RMS");
        micControl.add(chart);
        chart.showWindow();

        try {
            AudioSourceWaveFileWriter aswfw = 
                    new AudioSourceWaveFileWriter(new File("sound.wav"));
            micControl.add(aswfw);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        /*PCSpeaker pcSpeaker = new PCSpeaker();
        micControl.add(pcSpeaker);*/   

        motionControl.play();
    }
    
    private Geometry makeEar(Node root, Vector3f position) {
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Geometry ear = new Geometry("ear", new Box(.2f, .2f, .2f));
        ear.setLocalTranslation(position);
        mat.setColor("Color", ColorRGBA.Green);
        ear.setMaterial(mat);
        root.attachChild(ear);
        return ear;
    }
    private Vector3f[] path = new Vector3f[]{
        // loop 1
        new Vector3f(0, 0, 0),
        new Vector3f(0, 0, -10),
        new Vector3f(-2, 0, -14),
        new Vector3f(-6, 0, -20),
        new Vector3f(0, 0, -26),
        new Vector3f(6, 0, -20),
        new Vector3f(0, 0, -14),
        new Vector3f(-6, 0, -20),
        new Vector3f(0, 0, -26),
        new Vector3f(6, 0, -20),
        // loop 2
        new Vector3f(5, 0, -5),
        new Vector3f(7, 0, 1.5f),
        new Vector3f(14, 0, 2),
        new Vector3f(20, 0, 6),
        new Vector3f(26, 0, 0),
        new Vector3f(20, 0, -6),
        new Vector3f(14, 0, 0),
        new Vector3f(20, 0, 6),
        new Vector3f(26, 0, 0),
        new Vector3f(20, 0, -6),
        new Vector3f(14, 0, 0),
        // loop 3
        new Vector3f(8, 0, 7.5f),
        new Vector3f(7, 0, 10.5f),
        new Vector3f(6, 0, 20),
        new Vector3f(0, 0, 26),
        new Vector3f(-6, 0, 20),
        new Vector3f(0, 0, 14),
        new Vector3f(6, 0, 20),
        new Vector3f(0, 0, 26),
        new Vector3f(-6, 0, 20),
        new Vector3f(0, 0, 14),
        // begin ellipse
        new Vector3f(16, 5, 20),
        new Vector3f(0, 0, 26),
        new Vector3f(-16, -10, 20),
        new Vector3f(0, 0, 14),
        new Vector3f(16, 20, 20),
        new Vector3f(0, 0, 26),
        new Vector3f(-10, -25, 10),
        new Vector3f(-10, 0, 0),
        // come at me!
        new Vector3f(-28.00242f, 48.005623f, -34.648228f),
        new Vector3f(0, 0, -20),};


    @Override
    protected void createCameras() {
        super.createCameras();
        this.cam.setLocation(new Vector3f(-28.00242f, 48.005623f, -34.648228f));
        this.cam.setRotation(new Quaternion(0.3359635f, 0.34280345f, -0.13281013f, 0.8671653f));
    }

    @Override
    public void simpleUpdate(float tpf) {
        super.simpleUpdate(tpf);
        
        motionTimer.update();
        if (music.getStatus() != AudioSource.Status.Playing) {
            music.play();
        }
        Vector3f loc = cam.getLocation();
        Quaternion rot = cam.getRotation();
        listener.setLocation(loc);
        listener.setRotation(rot);
        music.setLocalTranslation(bell.getLocalTranslation());
    }
    
    @Override
    public void destroy() {
        super.destroy();
        
        System.out.println("destroy()");
        ((Sensor)micControl).cleanUp();
    }

    @Override
    protected void createAudio() {
        music = AudioFactory.getInstance().makeAudioSpeakerSource("Speaker1", "Help, I am hurt!", new Vector3f(15f, 1f, 2f));
        music.setLooping(true);
        music.setShowRange(true);
        rootNode.attachChild(music);
        //audioRenderer.playSource(music);
        music.setPositional(true);
        music.setVolume(1f);
        music.setReverbEnabled(false);
        music.setDirectional(false);
        music.setMaxDistance(200.0f);
        music.setRefDistance(1f);
        //music.setRolloffFactor(1f);
        //music.setLooping(false);
        //v1.play();
        audioRenderer.pauseSource(music);
    }
    
}
