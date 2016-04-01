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

import com.jme3.asset.TextureKey;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;
import com.jme3.util.TempVars;
import java.util.logging.Level;
import java.util.logging.Logger;
import phat.util.Debug;
import phat.util.SimpleScenario;
import phat.util.SpatialFactory;

/**
 * Test CameraControl and AccelerometerControl attached to a cube. We can see at
 * up-rigth the perspective view of the cube.
 *
 * @author pablo
 */
public class CreateScreenCommandTest extends SimpleScenario {

    Geometry screen;
    Vector3f screenLoc;
    Vector3f qrInitialPos = new Vector3f(0f,1f,1f);
    float qrSize = 0.01f;
    float initialDistance = 0.5f;
    float MIN_ANGLE = -45f;
    float MAX_ANGLE = 45f;
    float incAngle = 22.5f;
    float currentAngle = 0f;
    
    public enum Action {

        Forward, Backward, Next, Previous
    }
    Action nextAction;

    public static void main(String[] args) {
        Logger.getGlobal().setLevel(Level.ALL);

        CreateScreenCommandTest app = new CreateScreenCommandTest();
        app.setPauseOnLostFocus(false);
        app.setShowSettings(false);

        AppSettings settings = new AppSettings(true);
        settings.setWidth(480);
        settings.setHeight(800);
        app.setSettings(settings);

        app.start();
    }

    @Override
    public void simpleInitApp() {
        super.simpleInitApp();

        initKeys();
    }

    private void initKeys() {
        // You can map one or several inputs to one named action
        inputManager.addMapping(Action.Forward.name(), new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping(Action.Backward.name(), new KeyTrigger(KeyInput.KEY_DOWN));
        inputManager.addMapping(Action.Next.name(), new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping(Action.Previous.name(), new KeyTrigger(KeyInput.KEY_LEFT));

        // Add the names to the action listener.
        inputManager.addListener(actionListener, Action.Forward.name());
        inputManager.addListener(actionListener, Action.Backward.name());
        inputManager.addListener(actionListener, Action.Next.name());
        inputManager.addListener(actionListener, Action.Previous.name());
    }
    private ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (keyPressed) {
                nextAction = Action.valueOf(name);
            }
        }
    };

    @Override
    public void createTerrain() {
        Debug.enableDebugGrid(10, assetManager, rootNode);
        //bulletAppState.setDebugEnabled(true);

        Vector3f dimensions = new Vector3f(10f, 0.1f, 10f);
        Geometry floor = SpatialFactory.createCube(dimensions, ColorRGBA.Gray);

        RigidBodyControl rbc = new RigidBodyControl(0);
        floor.setLocalTranslation(new Vector3f(0f, -0.1f, 0f));

        floor.addControl(rbc);


        bulletAppState.getPhysicsSpace().add(rbc);

        rootNode.attachChild(floor);
    }

    @Override
    public void simpleUpdate(float tpf) {
        super.simpleUpdate(tpf);

        if (nextAction == null) {
            return;
        }

        switch (nextAction) {
            case Forward:
                moveForward();
                System.out.println("Forwared!");
                break;
            case Backward:
                moveBackward();
                System.out.println("Backward!");
                break;
            case Next:
                if(currentAngle+incAngle <= MAX_ANGLE) {
                    currentAngle += incAngle;
                }
                initialPos();
                System.out.println("Next!");
                break;
            case Previous:
                if(currentAngle-incAngle >= MIN_ANGLE) {
                    currentAngle -= incAngle;
                }
                initialPos();
                System.out.println("Previous!");
                break;
            default:

        }
        nextAction = null;
    }

    float distanceToScreen() {
        return cam.getLocation().distance(screenLoc);
    }
    
    void moveForward() {
        Vector3f inc = cam.getDirection().normalize().mult(0.01f);
        //cam.getDirection().set(cam.getLocation().subtract(screenLoc));
        cam.setLocation(cam.getLocation().add(inc));
        System.out.println("d = "+distanceToScreen());
    }
    
    void moveBackward() {
        Vector3f inc = cam.getDirection().normalize().mult(0.01f);
        inc.negateLocal();
        //cam.getDirection().set(cam.getLocation().subtract(screenLoc));
        cam.setLocation(cam.getLocation().add(inc));
        System.out.println("d = "+distanceToScreen());
    }
    
    void initialPos() {
        TempVars vars = TempVars.get();
        Quaternion q = vars.quat1;        
        q.fromAngles(0f, FastMath.DEG_TO_RAD*currentAngle, 0f);
        Vector3f dir = new Vector3f(0f, 0f, 1f);
        q.mult(dir, dir);
        vars.release();
        
        cam.setLocation(screenLoc.add(dir.mult(initialDistance)));
        cam.lookAt(screenLoc, Vector3f.UNIT_Y);
        cam.getDirection().set(cam.getLocation().subtract(screenLoc));
        System.out.println("cam.loc = "+cam.getLocation());
    }

    @Override
    public void createOtherObjects() {
        Node screenNode = new Node();
        Quad quadMesh = new Quad(1, 1);
        quadMesh.updateGeometry(1, 1, true);

        screen = new Geometry("Textured Quad", quadMesh);
        screenNode.attachChild(screen);

        //assetManager.registerLocator("https://jmonkeyengine.googlecode.com/svn/BookSamples/assets/Textures/", UrlLocator.class);
        TextureKey key = new TextureKey("Textures/PHAT-QR.png", false);
        key.setGenerateMips(true);
        Texture tex = assetManager.loadTexture(key);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", tex);
        screen.setMaterial(mat);

        screen.setLocalScale(new Vector3f(qrSize, qrSize, 1));
        screen.center();
        screen.move(qrInitialPos);

        //screenNode.setLocalTranslation(0f,1f,1f);
        screenNode.lookAt(new Vector3f(0f, 0f, 1f), Vector3f.UNIT_Y);

        rootNode.attachChild(screenNode);
    }

    @Override
    protected void createCameras() {
        flyCam.setMoveSpeed(1f);
        flyCam.setDragToRotate(true);// to prevent mouse capture
        //flyCam.setEnabled(false);

        cam.setFrustumPerspective(45f, (float) cam.getWidth() / cam.getHeight(), 0.01f, 1000f);
        
        Vector3f center = screen.getModelBound().getCenter();
        center.multLocal(qrSize);
        screenLoc = screen.getWorldTranslation().add(center);
        
        initialPos();
    }

    @Override
    public void destroy() {
        super.destroy();

    }
}
