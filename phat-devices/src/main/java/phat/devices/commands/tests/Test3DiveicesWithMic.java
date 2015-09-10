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
package phat.devices.commands.tests;

import com.aurellem.capture.AurellemSystemDelegate;
import com.aurellem.capture.IsoTimer;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioRenderer;
import com.jme3.audio.AudioSource;
import com.jme3.bullet.BulletAppState;
import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.events.AbstractCinematicEvent;
import com.jme3.cinematic.events.MotionTrack;
import com.jme3.font.BitmapText;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
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
import phat.app.PHATApplication;
import phat.app.PHATInitAppListener;
import phat.audio.AudioAppState;
import phat.audio.AudioFactory;
import phat.audio.AudioSpeakerSource;
import phat.audio.listeners.AudioSourceWaveFileWriter;
import phat.audio.listeners.PCSpeaker;
import phat.audio.listeners.XYRMSAudioChart;
import phat.body.BodiesAppState;
import phat.body.commands.RandomWalkingCommand;
import phat.body.commands.SetBodyInCoordenatesCommand;
import phat.body.commands.SetCameraToBodyCommand;
import phat.body.commands.SetSpeedDisplacemenetCommand;
import phat.body.commands.tests.CreateBodyCommandTest;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.devices.DevicesAppState;
import phat.devices.commands.CreateSmartphoneCommand;
import phat.devices.commands.SetDeviceInCoordenatesCommand;
import phat.devices.commands.SetDeviceOnPartOfBodyCommand;
import phat.devices.smartphone.SmartPhone;
import phat.sensors.Sensor;
import phat.sensors.microphone.MicrophoneControl;
import phat.util.Debug;
import phat.util.SpatialFactory;
import phat.util.SpatialUtils;
import phat.world.WorldAppState;

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
public class Test3DiveicesWithMic implements PHATInitAppListener, PHATCommandListener {

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
        Test3DiveicesWithMic test = new Test3DiveicesWithMic();
        PHATApplication phat = new PHATApplication(test);
        phat.setDisplayFps(true);
        phat.setDisplayStatView(false);
        AppSettings settings = new AppSettings(true);
        settings.setAudioRenderer(AurellemSystemDelegate.SEND);
        JmeSystem.setSystemDelegate(new AurellemSystemDelegate());
        settings.setTitle("PHAT");
        settings.setWidth(640);
        settings.setHeight(480);
        phat.setSettings(settings);
        phat.start();
    }
    SimpleApplication app;
    AudioRenderer audioRenderer;
    AssetManager assetManager;
    AppStateManager stateManager;
    Node rootNode;
    private Geometry bell;
    private AudioSpeakerSource music;
    private MotionTrack motionControl;
    private IsoTimer motionTimer = new IsoTimer(60);
    DevicesAppState devicesAppState;

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
        positionCamera();
    }

    private void positionCamera() {
        app.getCamera().setLocation(new Vector3f(-28.00242f, 48.005623f, -34.648228f));
        app.getCamera().setRotation(new Quaternion(0.3359635f, 0.34280345f, -0.13281013f, 0.8671653f));
    }

    private void initAudio() {
        org.lwjgl.input.Mouse.setGrabbed(false);
        //music = new AudioNode(assetManager, "Sound/Effects/Beep.ogg", false);
        assetManager.registerLocator("assets", FileLocator.class);

        AudioFactory.init(audioRenderer, assetManager, rootNode);
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

    @Override
    public void init(SimpleApplication app) {
        SpatialFactory.init(app.getAssetManager(), app.getRootNode());

        this.app = app;
        this.audioRenderer = app.getAudioRenderer();
        this.stateManager = app.getStateManager();
        this.assetManager = app.getAssetManager();
        this.rootNode = app.getRootNode();
    
        AudioAppState audioAppState = new AudioAppState();
        stateManager.attach(audioAppState);
        
        WorldAppState worldAppState = new WorldAppState();
        worldAppState.setGravity(Vector3f.ZERO);
        worldAppState.setLandType(WorldAppState.LandType.Basic);
        app.getStateManager().attach(worldAppState);
        worldAppState.setCalendar(2013, 1, 1, 12, 0, 0);


        devicesAppState = new DevicesAppState();
        stateManager.attach(devicesAppState);
        
        devicesAppState.runCommand(new CreateSmartphoneCommand("SP1", this).setDimensions(2f, 1f, 0.1f));
        devicesAppState.runCommand(new SetDeviceInCoordenatesCommand("SP1", new Vector3f(0, 0.5f, -20)));
        devicesAppState.runCommand( new CreateSmartphoneCommand("SP2", this).setDimensions(2f, 1f, 0.1f));
        devicesAppState.runCommand(new SetDeviceInCoordenatesCommand("SP2", new Vector3f(0, 0.5f, 20)));
        devicesAppState.runCommand(new CreateSmartphoneCommand("SP3", this).setDimensions(2f, 1f, 0.1f));
        devicesAppState.runCommand(new SetDeviceInCoordenatesCommand("SP3", new Vector3f(20, 0.5f, 0)));
        //devicesAppState.runCommand();
        
        app.setDisplayStatView(false);
        app.setDisplayFps(false);

        app.setTimer(new IsoTimer(60));

        initAudio();

        createScene();


        stateManager.attach(new AbstractAppState() {
            PHATApplication app;

            @Override
            public void initialize(AppStateManager asm, Application aplctn) {
                app = (PHATApplication) aplctn;

            }
            boolean standUp = false;
            boolean washingHands = false;
            boolean havingShower = false;
            float cont = 0f;
            boolean fall = false;
            float timeToFall = 10f;
            boolean init = false;

            @Override
            public void update(float f) {
                motionTimer.update();
                if (music.getStatus() != AudioSource.Status.Playing) {
                    music.play();
                }
                Vector3f loc = app.getCamera().getLocation();
                Quaternion rot = app.getCamera().getRotation();
                app.getListener().setLocation(loc);
                app.getListener().setRotation(rot);
                music.setLocalTranslation(bell.getLocalTranslation());
            }
        });

        motionControl.play();
    }

    private void createLight() {
        // We add light so we see the scene
        AmbientLight al = new AmbientLight();
        //al.setColor(ColorRGBA.White.mult(1.3f));
        al.setColor(ColorRGBA.White.mult(0.5f));
        rootNode.addLight(al);

        DirectionalLight dl = new DirectionalLight();
        dl.setColor(ColorRGBA.White);
        dl.setDirection(new Vector3f(2.8f, -2.8f, -2.8f).normalizeLocal());
        rootNode.addLight(dl);
    }

    @Override
    public void commandStateChanged(PHATCommand command) {
        if(command.getState().equals(PHATCommand.State.Success) && 
                command instanceof CreateSmartphoneCommand) {
            CreateSmartphoneCommand csc = (CreateSmartphoneCommand) command;
            System.out.println(csc.getSmartphoneId()+" -> "+devicesAppState.getDevice(csc.getSmartphoneId()));
            Node device = devicesAppState.getDevice(csc.getSmartphoneId());
            BitmapText bt = SpatialFactory.attachAName(device);
            bt.setColor(ColorRGBA.Black);
            bt.setLocalTranslation(0f, 4f, 0f);
            bt.scale(5f);
            
            MicrophoneControl c = device.getControl(MicrophoneControl.class);
            XYRMSAudioChart rmsListener = new XYRMSAudioChart("Audio of "+csc.getSmartphoneId());
            c.add(rmsListener);
            rmsListener.showWindow();
        }
    }
}