/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.bullet.control.ragdoll;

import com.aurellem.capture.Capture;
import com.aurellem.capture.IsoTimer;
import phat.controls.animation.*;
import phat.controls.animation.*;
import com.jme3.animation.AnimControl;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.system.JmeContext;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import phat.agents.actors.ActorFactory;
import phat.agents.actors.BasicActor;
import phat.util.SpatialFactory;

/**
 *
 * @author pablo
 */
public class PushTestApp extends SimpleApplication implements PhysicsCollisionListener {

    private BulletAppState bulletAppState;
    private Node model;
    private boolean initialized = false;
    private BasicActor basicActor;
    private float TIME_UNTIL_STANDUP = 5f;
    private float counter = -1f;
    private boolean pause = true;

    public boolean isInitialized() {
        return initialized;
    }

    public static void main(String[] args) {
        PushTestApp app = new PushTestApp();
        //app.setDisplayFps(false);
        app.setShowSettings(false);
        
       // recordVideoAndAudio(app);
        
        //app.setPauseOnLostFocus(false);
        app.start();
    }

    private static void recordVideoAndAudio(SimpleApplication app) {
        File video = new File("video.avi");
        File audio = new File("audio.wav");
        
	app.setTimer(new IsoTimer(60));
        try {
            Capture.captureVideo(app, video);
            Capture.captureAudio(app, audio);
        } catch (IOException ex) {
            Logger.getLogger(app.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static PushTestApp startAndWait(boolean visible, int MAX_INITIALIZATION_TIMEOUT) {
        PushTestApp app = new PushTestApp();
        app.setShowSettings(false);
        app.setPauseOnLostFocus(false);
        app.setDisplayStatView(false);
        app.setDisplayFps(false);

        if (visible) {
            app.start();
        } else {
            app.start(JmeContext.Type.Headless);
        }

        int counter = 0;
        while (!app.isInitialized()) {
            try {
                counter++;
                if (counter > MAX_INITIALIZATION_TIMEOUT) {
                    return null;
                }
                Thread.sleep(200);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return app;
    }

    /**
     * App initialisation.It is invoked by JME3 to create the world
     */
    @Override
    public void simpleInitApp() {
        SpatialFactory.init(assetManager, rootNode);
        ActorFactory.setScale(1f);
        
        initKeys();
        createPhysicsEngineAndAttachItToScene();
        createTerrain();
        createCameras();
        createLight();

        createOtherObjectInScene(rootNode, assetManager);

        
        ActorFactory.init(rootNode, assetManager, bulletAppState);
        basicActor = ActorFactory.createBasicActor("Patient", "Models/People/Elder/Elder.j3o", new Vector3f(0f, 2.0f, 0f), 0.2f, 5f, 0.5f, PatientAnimControl.class);
        basicActor.showName(true);
        //basicActor.moveTo(new Vector3f(20f, 1f, 20f));
    }

    public Node getActorModel() {
        return model;
    }

    public Collection<String> getAnimationNames() {
        AnimControl ac = model.getControl(AnimControl.class);
        return ac.getAnimationNames();
    }

    public boolean existsAnimation(String name) {
        AnimControl ac = model.getControl(AnimControl.class);
        return ac.getAnim(name) != null;
    }

    /**
     * Main simulation loop. This is where results of actions of different
     * participants are controlled
     *
     */
    @Override
    public void simpleUpdate(float tpf) {
        if (!pause) {
            super.simpleUpdate(tpf);
            initialized = true;
            if (counter >= 0f && counter < TIME_UNTIL_STANDUP) {
                counter += tpf;
            }
            if (counter >= TIME_UNTIL_STANDUP) {
                basicActor.standUp();
                counter = -1f;
            }
        }
    }

    @Override
    public void collision(PhysicsCollisionEvent event) {
        if (event == null || event.getNodeA() == null || event.getNodeB() == null) {
            return;
        }

        if (event.getNodeA().getName().equals("Obstacle") && event.getNodeB().getName().equals(basicActor.getName())) {
            System.out.println("Tropiezo con Box " + event.getAppliedImpulse());
            if (event.getAppliedImpulse() >= 0 && counter < 0) {
                counter = 0f;
                basicActor.tripOver();
            }
        }
    }

    private void createCameras() {
        flyCam.setMoveSpeed(10f*ActorFactory.getScale());
        cam.setLocation(new Vector3f(12.4013605f, 10.488437f, 17.843031f));
        cam.setRotation(new Quaternion(-0.060740203f, 0.93925786f, -0.2398315f, -0.2378785f));
    }

    private void createTerrain() {
        Box b = new Box(50f*ActorFactory.getScale(), 0.1f*ActorFactory.getScale(), 50f*ActorFactory.getScale()); // create cube shape at the origin
        Geometry base = new Geometry("Terrain", b);  // create cube geometry from the shape
        Material mat = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");  // create a simple material
        mat.setColor("Color", ColorRGBA.Gray);   // set color of material to blue
        base.setMaterial(mat);
        base.move(-5f*ActorFactory.getScale(), -0.1f*ActorFactory.getScale(), -5f*ActorFactory.getScale());
        // the collision mesh needs to be dynamic, otherwise, collision with dynamic objects
        // do not work well. They tend to go through others.
        //CollisionShape sceneShape = CollisionShapeFactory.createDynamicMeshShape(base);
        RigidBodyControl landscape = new RigidBodyControl(0f);
        base.addControl(landscape);
        bulletAppState.getPhysicsSpace().add(landscape); // tells the physicis engine it is a rigid-solid box-shaped object
        rootNode.attachChild(base);
    }

    private void createPhysicsEngineAndAttachItToScene() {
        bulletAppState = new BulletAppState(); // physics engine based in jbullet
        bulletAppState.setThreadingType(BulletAppState.ThreadingType.PARALLEL);
        bulletAppState.setEnabled(true);
        stateManager.attach(bulletAppState);
        //bulletAppState.getPhysicsSpace().setAccuracy(1 / 120f);
        //bulletAppState.getPhysicsSpace().enableDebug(assetManager); // to show the collision wireframes

        bulletAppState.getPhysicsSpace().setGravity(new Vector3f(0f, -9.81f*ActorFactory.getScale(), 0f));
        bulletAppState.getPhysicsSpace().addCollisionListener(this);
    }

    private Geometry createOtherObjectInScene(Node rootNode,
            AssetManager assetManager) {
        // code modified from http://jmonkeyengine.org/wiki/doku.php/jme3:beginner:hello_asset
        Box box = new Box(1f, 0.1f, 1f);
        Geometry wall = new Geometry("Obstacle", box);
        Material mat_brick = new Material(
                assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat_brick.setTexture("ColorMap",
                assetManager.loadTexture("Textures/Terrain/BrickWall/BrickWall.jpg"));
        wall.setMaterial(mat_brick);

        //wall.setLocalTranslation(new Vector3f(7f,1f, 7f));
        //wall.move(new Vector3f(10f, 10f, 5f));
        //wall.setLocalScale(0.15f); // make wall smaller

        // this part makes the object solid for others that define as well a "collision shape"
        // warning, you have to use a dynamic shape. If you choose other kind, collisions will not be detected        
        CollisionShape wallShape = CollisionShapeFactory.createDynamicMeshShape(wall);
        // Associate a rigid body to the wall so that it is processed by the physical engine 
        // the object mass has to be greater than 0 so that gravity acts on it.
        RigidBodyControl wallBody = new RigidBodyControl(wallShape, 10f);
        wall.addControl(wallBody);
        wallBody.setEnabled(true);
        wallBody.setFriction(0.2f);
        bulletAppState.getPhysicsSpace().add(wallBody);

        rootNode.attachChild(wall);

        wallBody.setPhysicsLocation(new Vector3f(20f, 0.2f, 20f));
        return wall;

    }

    private void createLight() {
        // We add light so we see the scene
        AmbientLight al = new AmbientLight();
        //al.setColor(ColorRGBA.White.mult(1.3f));
        al.setColor(ColorRGBA.White.mult(0.6f));
        rootNode.addLight(al);

        DirectionalLight dl = new DirectionalLight();
        dl.setColor(ColorRGBA.Gray);
        dl.setDirection(new Vector3f(2.8f, -2.8f, -2.8f).normalizeLocal());
        rootNode.addLight(dl);
    }
    private ActionListener actionListener = new ActionListener() {

        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("Push") && !keyPressed) {
                basicActor.slip();
            } else if(name.equals("StandUp") && !keyPressed) {
                basicActor.standUp();
            }
        }
    };

    private void pause(boolean enabled) {
        if (enabled) {
        }
    }

    /**
     * Custom Keybinding: Map named actions to inputs.
     */
    private void initKeys() {
        // You can map one or several inputs to one named action
        inputManager.addMapping("Push", new KeyTrigger(KeyInput.KEY_P));
        inputManager.addMapping("StandUp", new KeyTrigger(KeyInput.KEY_SPACE));
        // Add the names to the action listener.
        inputManager.addListener(actionListener, new String[]{"Push", "StandUp"});

    }
}