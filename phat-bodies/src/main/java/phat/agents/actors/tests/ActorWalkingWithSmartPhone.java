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
package phat.agents.actors.tests;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.control.CameraControl;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
import phat.agents.actors.ActorFactory;
import phat.agents.actors.BasicActor;
import phat.sensors.accelerometer.AccelerometerControl;
import phat.sensors.accelerometer.XYAccelerationsChart;
import phat.util.Debug;
import phat.util.SimpleScenario;
import phat.util.SpatialFactory;

/**
 *
 * @author Pablo
 */
public class ActorWalkingWithSmartPhone extends SimpleScenario {

    Node sm1;
    
    public static void main(String [] args) {
        ActorWalkingWithSmartPhone app = new ActorWalkingWithSmartPhone();
        app.setPauseOnLostFocus(false);
        app.setShowSettings(false);
        
        AppSettings settings = new AppSettings(true);
        settings.setWidth(800);
        settings.setHeight(480);
        app.setSettings(settings);
        
        app.setPhysicsDebugging(false);
        
        app.start();
    }
    
    @Override
    public void simpleInitApp() {
        super.simpleInitApp();
    }
    @Override
    public void simpleUpdate(float fps) {
        super.simpleUpdate(fps);        
    }
    
    @Override
    public void createTerrain() {
        Debug.enableDebugGrid(10, assetManager, rootNode);
        
        Vector3f dimensions = new Vector3f(10f, 0.1f, 10f);
        Geometry floor = SpatialFactory.createCube(dimensions, ColorRGBA.Gray);        
        
        RigidBodyControl rbc = new RigidBodyControl(0);
        floor.setLocalTranslation(new Vector3f(0f, -0.1f, 0f));
        
        floor.addControl(rbc);
        
        
        bulletAppState.getPhysicsSpace().add(rbc);
        
        rootNode.attachChild(floor);
    }

    @Override
    public void createOtherObjects() {
        ActorFactory.init(rootNode, assetManager, bulletAppState);
        
        sm1 = new Node();
        // Smartphone creation
        Geometry geo = createSmartphoneGeometry("Smartphone1");
        sm1.attachChild(geo);
        sm1.setUserData("ID", "Cube");
        sm1.setLocalTranslation(0f, 10f, 0f);
                
        RigidBodyControl rbc = new RigidBodyControl(0.3f);        
        sm1.addControl(rbc);
        
        //PHATVirtualCameraControl cc = new PHATVirtualCameraControl();
        //sm1.addControl(cc);        
        
        AccelerometerControl ac = new AccelerometerControl("Smartphone1");
        sm1.addControl(ac);
        
        XYAccelerationsChart chart = new XYAccelerationsChart("Chart - Acc.", "Box accelerations", "m/s2", "x,y,z");
        ac.add(chart);
        chart.showWindow();
        
        createViewPort(sm1);
        
        bulletAppState.getPhysicsSpace().add(sm1);
        
        rootNode.attachChild(sm1);
        
        // Actor creation
        BasicActor ba = ActorFactory.createBasicActor(
                "Patient", 
                "Models/People/Elder/Elder.j3o", 
                Vector3f.ZERO.add(2f, 0f, 2f), 
                0.2f, 0.5f, 0.1f);
        ba.setAnimation("Yawn");
        
        ba.pickUp(sm1, false);
    }
    
    public Geometry createSmartphoneGeometry(String name) {
        Box box = new Box(Vector3f.ZERO, 0.03f, 0.06f, 0.002f);        
        Geometry geo = new Geometry(name, box);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        //mat.setTexture("ColorMap", assetManager.loadTexture("Textures/FrontSmartPhone.jpg"));
        geo.setMaterial(mat);
        return geo;
    }
    
    private void createViewPort(Node smartphone) {
        Camera smartPhoneCamera = cam.clone();
        float xFactor = (float)cam.getWidth()/(float)smartPhoneCamera.getWidth();
        float yFactor = (float)cam.getHeight()/(float)smartPhoneCamera.getHeight();
        smartPhoneCamera.setViewPort(0f*xFactor, 0.2f*xFactor, 0.8f*yFactor, 1f*yFactor);
        ViewPort viewPort = renderManager.createMainView("asdf", smartPhoneCamera);        
        viewPort.setClearFlags(true, true, true);
        viewPort.setBackgroundColor(ColorRGBA.White);
        viewPort.attachScene(SpatialFactory.getRootNode());
        
        //create the camera Node
        CameraNode camNode = new CameraNode("Camera Node", smartPhoneCamera);
        //This mode means that camera copies the movements of the target:
        camNode.setControlDir(CameraControl.ControlDirection.SpatialToCamera);
        //Attach the camNode to the target:
        smartphone.attachChild(camNode);
        //Move camNode, e.g. behind and above the target:
        camNode.setLocalTranslation(new Vector3f(0, 0, 0));
        //Rotate the camNode to look at the target:
        //camNode.lookAt(target.getLocalTranslation(), Vector3f.UNIT_Y);
    }
}
