/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.examples.gestures;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import phat.agents.actors.ActorFactory;
import phat.agents.actors.BasicActor;
import phat.util.SimpleScenario;
import phat.util.SpatialFactory;

/**
 *
 * @author pablo
 */
public class GestureTester extends SimpleScenario {
    private BasicActor basicActor;
    
    public static void main(String[] args) {
        GestureTester app = new GestureTester();
        //app.setDisplayFps(false);
        app.setShowSettings(false);
        
        //app.setPauseOnLostFocus(false);
        app.start();
    }
    
    @Override
    public void simpleInitApp() {
        SpatialFactory.init(assetManager, rootNode);
        ActorFactory.setScale(1f);
        
        super.simpleInitApp();
    }
    
    @Override
    public void createTerrain() {
        
        Box b = new Box(50f, 0.1f, 50f); // create cube shape at the origin
        Geometry base = new Geometry("Terrain", b);  // create cube geometry from the shape
        Material mat = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");  // create a simple material
        mat.setColor("Color", ColorRGBA.Gray);   // set color of material to blue
        base.setMaterial(mat);
        base.move(-5f, -0.1f, -5f);
        // the collision mesh needs to be dynamic, otherwise, collision with dynamic objects
        // do not work well. They tend to go through others.
        //CollisionShape sceneShape = CollisionShapeFactory.createDynamicMeshShape(base);
        RigidBodyControl landscape = new RigidBodyControl(0f);
        base.addControl(landscape);
        bulletAppState.getPhysicsSpace().add(landscape); // tells the physicis engine it is a rigid-solid box-shaped object
        rootNode.attachChild(base);
    }

    @Override
    public void createOtherObjects() {
        ActorFactory.init(rootNode, assetManager, bulletAppState);
        basicActor = ActorFactory.createBasicActor("Patient", "Models/People/Elder/Elder.j3o", new Vector3f(0f, 2.0f, 0f), 0.2f, 5f, 0.5f);
        basicActor.showName(true);
    }
}
