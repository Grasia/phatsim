/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.controls.animation;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.CameraControl;
import com.jme3.scene.shape.Box;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;
import phat.agents.actors.ActorFactory;
import phat.agents.actors.BasicActor;
import phat.agents.actors.parkinson.HandTremblingControl;
import phat.agents.actors.parkinson.HeadTremblingControl;
import phat.audio.listeners.PCSpeaker;
import phat.audio.listeners.XYRMSAudioChart;
import phat.audio.util.AudioSimpleScenario;
import phat.sensors.microphone.MicrophoneControl;
import phat.util.Debug;

/**
 *
 * @author pablo
 */
public class FootStepsControlTestApp extends AudioSimpleScenario implements PhysicsCollisionListener/*, RagdollCollisionListener */{

    private MicrophoneControl micControl;
    private BasicActor actor;

    public FootStepsControlTestApp() {
        super();
        
        setShowSettings(true);
    }
    public static void main(String[] args) {
        FootStepsControlTestApp app = new FootStepsControlTestApp();
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

    @Override
    public void createTerrain() {
        Debug.enableDebugGrid(10f, assetManager, rootNode);

        bulletAppState.getPhysicsSpace().addCollisionListener(this);

        Box b = new Box(20f, 0.1f, 20f); // create cube shape at the origin
        Geometry base = new Geometry("Terrain", b);  // create cube geometry from the shape
        Material terrainMat = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");  // create a simple material
        terrainMat.setColor("Color", ColorRGBA.Gray);   // set color of material to blue
        base.setMaterial(terrainMat);
        base.move(new Vector3f(-1f, -0.1f, -1f).mult(ActorFactory.getScale()));
        base.setLocalScale(ActorFactory.getScale());
        // the collision mesh needs to be dynamic, otherwise, collision with dynamic objects
        // do not work well. They tend to go through others.
        CollisionShape sceneShape = CollisionShapeFactory.createDynamicMeshShape(base);
        RigidBodyControl landscape = new RigidBodyControl(sceneShape, 0f);
        base.addControl(landscape);

        bulletAppState.getPhysicsSpace().add(base); // tells the physicis engine it is a rigid-solid box-shaped object
        rootNode.attachChild(base);
    }

    @Override
    public void createOtherObjects() {
        ActorFactory.init(rootNode, assetManager, bulletAppState);
        actor = ActorFactory.createBasicActor("Actor", "Models/People/Elder/Elder.j3o", new Vector3f(0f, 2f, 0f), 0.3f, 0.5f, 0.5f, PatientAnimControl.class);
        rootNode.attachChild(actor.getNode());

        /*
        KinematicRagdollControl krc = ActorFactory.findControl(actor.getNode(), KinematicRagdollControl.class);
        krc.addCollisionListener(this);*/

        initGestures(actor.getNode());

        createObstacle(rootNode, assetManager);
    }

    private void initGestures(Node model) {
        HandTremblingControl htc = new HandTremblingControl(HandTremblingControl.Hand.LEFT_HAND);
        model.addControl(htc);

        htc = new HandTremblingControl(HandTremblingControl.Hand.RIGHT_HAND);
        model.addControl(htc);

        HeadTremblingControl headtc = new HeadTremblingControl();
        model.addControl(headtc);
    }
    /*private void initGestures(Node model) {
     HandTremblingControl htc = new HandTremblingControl(HandTremblingControl.Hand.LEFT_HAND);
     model.addControl(htc);  
        
     HeadTremblingControl headtc = new HeadTremblingControl();
     model.addControl(headtc);
     }*/
    float acumulative = 0f;
    float seconds = 0f;
    int numSeconds = 0;
    boolean moving = false;

    @Override
    public void simpleUpdate(float tpf) {
        super.simpleUpdate(tpf);

        Vector<Runnable> currentActions = new Vector<Runnable>(collisionActions);
        collisionActions.removeAll(currentActions);
        for (Runnable run : currentActions) {
            run.run();
        };

        acumulative += tpf;
        seconds += tpf;

        if (seconds > 1f) {
            System.out.println(numSeconds++);
            seconds = 0f;
        }

        if (acumulative > 2f && !moving) {
            actor.moveTo(new Vector3f(10f, 10f, 10f));
            moving = true;
        }

    }
    /*
    AudioSource noiseSource1;
    AudioSource noiseSource2;
    AudioSource noiseSource3;
    
    String noise1 = "Sound/HumanEffects/FallingDown/fallingDown1.ogg";
    String noise2 = "Sound/HumanEffects/FallingDown/fallingDown2.ogg";
    String noise3 = "Sound/HumanEffects/FallingDown/fallingDown3.ogg";

    private AudioSource createAudio(String resource) {
        AudioSource as = AudioFactory.getInstance().makeAudioSource("FootSteps", resource, Vector3f.ZERO);
        as.setLooping(false);
        as.setPositional(true);
        as.setDirectional(false);
        as.setVolume(0.5f);
        as.setRefDistance(1f);
        as.setTimeOffset(0f);

        //as.setShowRange(true);

        return as;
    }*/

    
    @Override
    protected void createAudio() {
        Node camFollower = new Node();
        // means that the Camera's transform is "copied" to the Transform of the Spatial.
        CameraControl cc = new CameraControl(cam, CameraControl.ControlDirection.CameraToSpatial);
        camFollower.addControl(cc);
        rootNode.attachChild(camFollower);

        micControl = new MicrophoneControl("Micro1", 10000, audioRenderer);
        camFollower.addControl(micControl);

        PCSpeaker pcSpeaker = new PCSpeaker();
        micControl.add(pcSpeaker);
        
        XYRMSAudioChart chart = new XYRMSAudioChart("RMS");
        micControl.add(chart);
        chart.showWindow();

        /*
        noiseSource1 = createAudio(noise1);
        noiseSource2 = createAudio(noise2);
        noiseSource3 = createAudio(noise3);

        actor.getNode().attachChild(noiseSource1);
        actor.getNode().attachChild(noiseSource2);
        actor.getNode().attachChild(noiseSource3);
        */
    }
    
    private ConcurrentLinkedQueue<Runnable> collisionActions = new ConcurrentLinkedQueue<Runnable>();

    @Override
    public void collision(PhysicsCollisionEvent pce) {
        Spatial obj1 = pce.getNodeA();
        Spatial obj2 = pce.getNodeB();
        if (obj1 != null && obj2 != null) {
            final String obj1n = pce.getNodeA().getName();
            final String obj2n = pce.getNodeB().getName();
            Runnable collisionAction = new Runnable() {
                @Override
                public void run() {
                    //System.out.println("o1 = "+obj1n+", o2 = "+obj2n);
                    if ((obj1n.equals("Obstacle") && obj2n.equals("Actor"))
                            || (obj2n.equals("Obstacle") && obj1n.equals("Actor"))) {
                        actor.tripOver();
                        //noiseSource1.playInstance();
                    }
                }
            };
            collisionActions.add(collisionAction);
        }
    }

    private Geometry createObstacle(Node rootNode, AssetManager assetManager) {
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
        RigidBodyControl wallBody = new RigidBodyControl(wallShape, 0f);
        wall.addControl(wallBody);
        wallBody.setEnabled(true);
        wallBody.setFriction(0.2f);
        bulletAppState.getPhysicsSpace().add(wallBody);

        rootNode.attachChild(wall);

        wallBody.setPhysicsLocation(new Vector3f(5f, 0.2f, 5f));
        return wall;

    }

    boolean head = false;
    boolean spine = false;
    
    @Override
    protected void createCameras() {
        flyCam.setMoveSpeed(100f);
        flyCam.setDragToRotate(true);// to prevent mouse capture
        cam.setLocation(new Vector3f(10f, 2.0f, 5f));
        //cam.setRotation(new Quaternion(-0.3325067f, 0.6662985f, -0.44692048f, -0.49572945f));
        cam.lookAt(rootNode.getWorldBound().getCenter(), Vector3f.UNIT_Y);
    }
    /*
    @Override
    public void collide(Bone bone, PhysicsCollisionObject pco, PhysicsCollisionEvent pce) {
        Runnable collisionAction = null;
        System.out.println("--------> "+bone.getName());
        if(!head && bone.getName().equals("Head")) {
            collisionAction = new Runnable() {
                @Override
                public void run() {
                    noiseSource1.playInstance();
                }
            };
            head = true;
        } else if(!spine && bone.getName().equals("Spine1")) {
            collisionAction = new Runnable() {
                @Override
                public void run() {
                    noiseSource2.playInstance();
                }
            };
            spine = true;
        }
        if(collisionAction != null) {
            collisionActions.add(collisionAction);
        }
    }*/
}
