/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.controls.animation;

import com.jme3.animation.AnimControl;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
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
import java.util.Collection;
import java.util.Vector;

/**
 *
 * @author pablo
 */
public class AnimationTestApp extends SimpleApplication {

    private Node model;    
    private boolean initialized = false;
    
    public boolean isInitialized() {
        return initialized;
    }
    
    public static AnimationTestApp startAndWait(boolean visible, int MAX_INITIALIZATION_TIMEOUT) {
        AnimationTestApp app = new AnimationTestApp();
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

        //SpatialFactory.init(assetManager, rootNode);
        //createPhysicsEngineAndAttachItToScene();
        //createTerrain();
        //createCameras();
        //createLight();

        //Geometry wall = createOtherObjectInScene(rootNode,assetManager);

        model = (Node) assetManager.loadModel("Models/People/Elder/Elder.j3o"); // from artifact
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
     */
    @Override
    public void simpleUpdate(float tpf) {
        super.simpleUpdate(tpf);
        initialized = true;
    }

    private void createCameras() {
        flyCam.setMoveSpeed(3f);
        cam.setLocation(new Vector3f(12.4013605f, 10.488437f, 17.843031f));
        cam.setRotation(new Quaternion(-0.060740203f, 0.93925786f, -0.2398315f, -0.2378785f));
    }

    private void createTerrain() {
        /*Geometry base = SpatialFactory.createCube(new Vector3f(15f, 1f, 15f), ColorRGBA.LightGray);
        base.move(-5f, -0.1f, -5f);
        // the collision mesh needs to be dynamic, otherwise, collision with dynamic objects
        // do not work well. They tend to go through others.
        CollisionShape sceneShape = CollisionShapeFactory.createDynamicMeshShape(base);
        RigidBodyControl landscape = new RigidBodyControl(sceneShape, 0);
        base.addControl(landscape);
        //bulletAppState.getPhysicsSpace().add(landscape); // tells the physicis engine it is a rigid-solid box-shaped object
        rootNode.attachChild(base);*/
    }

    /*private void createPhysicsEngineAndAttachItToScene() {
        bulletAppState = new BulletAppState(); // physics engine based in jbullet
        bulletAppState.setThreadingType(BulletAppState.ThreadingType.PARALLEL);
        bulletAppState.setEnabled(true);
        stateManager.attach(bulletAppState);
        bulletAppState.getPhysicsSpace().enableDebug(assetManager); // to show the collision wireframes
    }

    private Geometry createOtherObjectInScene(Node rootNode,
            AssetManager assetManager) {
        // code modified from http://jmonkeyengine.org/wiki/doku.php/jme3:beginner:hello_asset
        Box box = new Box(Vector3f.ZERO, 2.5f, 2.5f, 1.0f);
        Geometry wall = new Geometry("Box", box);
        Material mat_brick = new Material(
                assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat_brick.setTexture("ColorMap",
                assetManager.loadTexture("Textures/Terrain/BrickWall/BrickWall.jpg"));
        wall.setMaterial(mat_brick);

        wall.setLocalTranslation(new Vector3f(7f, 5f, 7f));
        //wall.move(new Vector3f(10f, 10f, 5f));
        wall.setLocalScale(0.15f); // make wall smaller

        // this part makes the object solid for others that define as well a "collision shape"
        // warning, you have to use a dynamic shape. If you choose other kind, collisions will not be detected        
        CollisionShape wallShape = CollisionShapeFactory.createDynamicMeshShape(wall);
        // Associate a rigid body to the wall so that it is processed by the physical engine 
        // the object mass has to be greater than 0 so that gravity acts on it.
        RigidBodyControl wallBody = new RigidBodyControl(wallShape, 4f);
        wallBody.setEnabled(true);
        wallBody.setFriction(0.5f);
        wall.addControl(wallBody);
        bulletAppState.getPhysicsSpace().add(wallBody);

        rootNode.attachChild(wall);

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
    }*/
}