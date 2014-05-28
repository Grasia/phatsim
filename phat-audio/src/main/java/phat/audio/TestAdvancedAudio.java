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
import java.lang.reflect.Field;
import phat.audio.listeners.PCSpeaker;
import phat.audio.listeners.XYRMSAudioChart;
import phat.sensors.Sensor;
import phat.sensors.microphone.MicrophoneControl;

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
public class TestAdvancedAudio extends SimpleAudioScenario {

    /**
     * You will see three grey cubes, a blue sphere, and a path which circles
     * each cube. The blue sphere is generating a constant monotone sound as it
     * moves along the track. Each cube is listening for sound; when a cube
     * hears sound whose intensity is greater than a certain threshold, it
     * changes its color from grey to green.
     *
     * Each cube is also saving whatever it hears to a file. The scene from the
     * perspective of the viewer is also saved to a video file. When you listen
     * to each of the sound files alongside the video, the sound will get louder
     * when the sphere approaches the cube that generated that sound file. This
     * shows that each listener is hearing the world from its own perspective.
     *
     */
    public static void main(String[] args) {
        TestAdvancedAudio app = new TestAdvancedAudio();
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
    private Geometry bell;
    private AudioSpeakerSource music;
    private MotionTrack motionControl;
    private IsoTimer motionTimer = new IsoTimer(60);
    private MicrophoneControl micControl;

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

    private void createScene() {
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
    public void createCameras() {        
        this.cam.setLocation(new Vector3f(-28.00242f, 48.005623f, -34.648228f));
        this.cam.setRotation(new Quaternion(0.3359635f, 0.34280345f, -0.13281013f, 0.8671653f));
    }

    @Override
    public void initAudio() {        
        music = AudioFactory.getInstance().makeAudioSpeakerSource("Speaker1", "Help, I am hurt!", new Vector3f(15f, 1f, 2f));
        rootNode.attachChild(music);
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
    public void createTerrain() {
        createScene();
    }

    @Override
    public void createOtherObjects() {
        //SmartPhone sp1 = startSmartPhone("Smartphone1", new Vector3f(0, 0, -20), "emulator-5554");
        //SmartPhone sp2 = startSmartPhone("Smartphone2", new Vector3f(0, 0, 20), "emulator-5556");
        //SmartPhone sp3 = startSmartPhone("Smartphone3", new Vector3f(20, 0, 0), "emulator-5558");
        
        Geometry geo1 = makeEar(rootNode, new Vector3f(0, 0, -20));

        micControl = new MicrophoneControl("Micro1", 10000, audioRenderer);
        geo1.addControl(micControl);

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

        motionControl.play();
    }
}