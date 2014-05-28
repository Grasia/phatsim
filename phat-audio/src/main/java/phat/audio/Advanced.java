/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.audio;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;

import org.tritonus.share.sampled.FloatSampleTools;

import com.aurellem.capture.AurellemSystemDelegate;
import com.aurellem.capture.Capture;
import com.aurellem.capture.IsoTimer;
import com.aurellem.capture.audio.CompositeSoundProcessor;
import com.aurellem.capture.audio.MultiListener;
import com.aurellem.capture.audio.SoundProcessor;
import com.aurellem.capture.audio.WaveFileWriter;
import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioSource;
import com.jme3.audio.Listener;
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

/**
 * 
 * Demonstrates advanced use of the audio capture and recording
 * features.  Multiple perspectives of the same scene are
 * simultaneously rendered to different sound files.
 * 
 * A key limitation of the way multiple listeners are implemented is
 * that only 3D positioning effects are realized for listeners other
 * than the main LWJGL listener.  This means that audio effects such
 * as environment settings will *not* be heard on any auxiliary
 * listeners, though sound attenuation will work correctly.
 * 
 * Multiple listeners as realized here might be used to make AI
 * entities that can each hear the world from their own perspective.
 * 
 * @author Robert McIntyre
 */

public class Advanced extends SimpleApplication {

    /**
     * You will see three grey cubes, a blue sphere, and a path which
     * circles each cube.  The blue sphere is generating a constant
     * monotone sound as it moves along the track.  Each cube is
     * listening for sound; when a cube hears sound whose intensity is
     * greater than a certain threshold, it changes its color from
     * grey to green.
     * 
     *  Each cube is also saving whatever it hears to a file.  The
     *  scene from the perspective of the viewer is also saved to a
     *  video file.  When you listen to each of the sound files
     *  alongside the video, the sound will get louder when the sphere
     *  approaches the cube that generated that sound file.  This
     *  shows that each listener is hearing the world from its own
     *  perspective.
     * 
     */
    public static void main(String[] args) {
        Advanced app = new Advanced();
        AppSettings settings = new AppSettings(true);
        settings.setAudioRenderer(AurellemSystemDelegate.SEND);
        JmeSystem.setSystemDelegate(new AurellemSystemDelegate());
        app.setSettings(settings);
        app.setShowSettings(false);
        app.setPauseOnLostFocus(false);
                
        /*try {
            //Capture.captureVideo(app, File.createTempFile("advanced",".avi"));
            Capture.captureAudio(app, File.createTempFile("advanced",".wav"));
        }
        catch (IOException e) {e.printStackTrace();}*/
                 
        app.start();
    }

    private Geometry bell;
    private Geometry ear1;
    private Geometry ear2;
    private Geometry ear3;
    private AudioNode music;
    private MotionTrack motionControl;
    private IsoTimer motionTimer = new IsoTimer(60);

    private Geometry makeEar(Node root, Vector3f position){
        Material mat = new Material(assetManager, 
                                    "Common/MatDefs/Misc/Unshaded.j3md");
        Geometry ear = new Geometry("ear", new Box(1.0f, 1.0f, 1.0f));
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
        new Vector3f(0, 0 , -20),
    };

    private void createScene() {
        Material mat = new Material(assetManager,
                                    "Common/MatDefs/Misc/Unshaded.j3md");
        bell = new Geometry( "sound-emitter" , new Sphere(15,15,1));
        mat.setColor("Color", ColorRGBA.Blue);
        bell.setMaterial(mat);
        rootNode.attachChild(bell);

        ear1 = makeEar(rootNode, new Vector3f(0, 0 ,-20));
        ear2 = makeEar(rootNode, new Vector3f(0, 0 ,20));
        ear3 = makeEar(rootNode, new Vector3f(20, 0 ,0));

        MotionPath track = new MotionPath();

        for (Vector3f v : path){
            track.addWayPoint(v);
        }
        track.setCurveTension(0.80f);

        motionControl = new MotionTrack(bell,track);
        // for now, use reflection to change the timer... 
        // motionControl.setTimer(new IsoTimer(60));
                
        try {
            Field timerField;
            timerField = 
                AbstractCinematicEvent.class.getDeclaredField("timer");
            timerField.setAccessible(true);
            try {timerField.set(motionControl, motionTimer);} 
            catch (IllegalArgumentException e) {e.printStackTrace();} 
            catch (IllegalAccessException e) {e.printStackTrace();}
        } 
        catch (SecurityException e) {e.printStackTrace();} 
        catch (NoSuchFieldException e) {e.printStackTrace();}


        motionControl.setDirectionType
            (MotionTrack.Direction.PathAndRotation);
        motionControl.setRotation
            (new Quaternion().fromAngleNormalAxis
             (-FastMath.HALF_PI, Vector3f.UNIT_Y));
        motionControl.setInitialDuration(20f);
        motionControl.setSpeed(1f);

        track.enableDebugShape(assetManager, rootNode);
        positionCamera();
    }

    private void positionCamera(){
        this.cam.setLocation
            (new Vector3f(-28.00242f, 48.005623f, -34.648228f));
        this.cam.setRotation
            (new Quaternion
             (0.3359635f, 0.34280345f, -0.13281013f, 0.8671653f));
    }

    private void initAudio() {
        org.lwjgl.input.Mouse.setGrabbed(false);        
        music = new AudioNode(assetManager, 
                              "Sound/Effects/Beep.ogg", false);
        rootNode.attachChild(music);
        audioRenderer.playSource(music);
        music.setPositional(true);
        music.setVolume(1f);
        music.setReverbEnabled(false);
        music.setDirectional(false);
        music.setMaxDistance(200.0f);
        music.setRefDistance(1f);
        //music.setRolloffFactor(1f);
        music.setLooping(false);
        audioRenderer.pauseSource(music); 
    }

    public class Dancer implements SoundProcessor {
        Geometry entity;
        float scale = 2;
        public Dancer(Geometry entity){
            this.entity = entity;
        }

        /**
         * this method is irrelevant since there is no state to cleanup.
         */
        public void cleanup() {}


        /**
         * Respond to sound!  This is the brain of an AI entity that 
         * hears its surroundings and reacts to them.
         */
        public void process(ByteBuffer audioSamples, 
                            int numSamples, AudioFormat format) {
            audioSamples.clear();
            byte[] data = new byte[numSamples];
            float[] out = new float[numSamples];
            audioSamples.get(data);
            FloatSampleTools.
                byte2floatInterleaved
                (data, 0, out, 0, numSamples/format.getFrameSize(), format);

            float max = Float.NEGATIVE_INFINITY;
            for (float f : out){if (f > max) max = f;}
            audioSamples.clear();

            if (max > 0.1){
                entity.getMaterial().setColor("Color", ColorRGBA.Green);
            }
            else {
                entity.getMaterial().setColor("Color", ColorRGBA.Gray);
            }
        }
    }

    private void prepareEar(Geometry ear, int n){
        if (this.audioRenderer instanceof MultiListener){
            MultiListener rf = (MultiListener)this.audioRenderer;

            Listener auxListener = new Listener();
            auxListener.setLocation(ear.getLocalTranslation());

            rf.addListener(auxListener);
            WaveFileWriter aux = null;

            try {
                aux = new WaveFileWriter
                    (new File("advanced-audio-" + n + ".wav"));} 
            catch (IOException e) {e.printStackTrace();}

            rf.registerSoundProcessor
                (auxListener, 
                 new CompositeSoundProcessor(new Dancer(ear), aux));
        }   
    }

    public void simpleInitApp() {
        this.setTimer(new IsoTimer(60));
        initAudio();

        createScene();

        prepareEar(ear1, 1);
        prepareEar(ear2, 2);
        prepareEar(ear3, 3);

        motionControl.play();
    }

    public void simpleUpdate(float tpf) {
        motionTimer.update();
        if (music.getStatus() != AudioSource.Status.Playing){
            music.play();
        }
        Vector3f loc = cam.getLocation();
        Quaternion rot = cam.getRotation();
        listener.setLocation(loc);
        listener.setRotation(rot);
        music.setLocalTranslation(bell.getLocalTranslation());
    }
}
