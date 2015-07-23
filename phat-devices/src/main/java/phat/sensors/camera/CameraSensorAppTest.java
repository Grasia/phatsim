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
package phat.sensors.camera;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.control.CameraControl;
import com.jme3.system.AppSettings;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import phat.util.Debug;
import phat.util.SimpleScenario;
import phat.util.SpatialFactory;

/**
 * Test CameraControl and AccelerometerControl attached to a cube. 
 * We can see at up-rigth the perspective view of the cube.
 * 
 * @author pablo
 */
public class CameraSensorAppTest extends SimpleScenario {

    public static void main(String [] args) {
        Logger.getGlobal().setLevel(Level.ALL);
        
        CameraSensorAppTest app = new CameraSensorAppTest();
        app.setPauseOnLostFocus(false);
        app.setShowSettings(false);
        
        AppSettings settings = new AppSettings(true);
        settings.setWidth(480);
        settings.setHeight(800);
        app.setSettings(settings);
        
        app.start();
    }
    
    List<JFrame> frames = new ArrayList<JFrame>();
    
    @Override
    public void simpleInitApp() {
        super.simpleInitApp();
        
        //flyCam.setEnabled(false);
        /*ScreenshotAppState screenshotAppState = new ScreenshotAppState();
        stateManager.attach(screenshotAppState);*/
    }
    
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
    }
    
    Camera smartPhoneCamera;
    CameraNode cameraNode;
    
    @Override
    public void createOtherObjects() {
        createSmartphone("Smartphone1", new Vector3f(0f, 5f, 0f), ColorRGBA.Pink);
        createSmartphone("Smartphone2", new Vector3f(0f, 5f, 2f), ColorRGBA.Cyan);
    }
    
    private void createSmartphone(String smartphoneId, Vector3f loc, ColorRGBA color) {
        Node smartphone = new Node(smartphoneId);
        smartphone.setLocalTranslation(loc);
        Geometry geo = SpatialFactory.createCube(Vector3f.UNIT_XYZ.mult(0.1f), color);
        smartphone.attachChild(geo);

        RigidBodyControl rbc = new RigidBodyControl(1);
        smartphone.addControl(rbc);
        
        smartPhoneCamera = /*new Camera(640, 480);*/cam.clone();
        smartPhoneCamera.setFrustumPerspective(45f, (float) cam.getWidth() / cam.getHeight(), 0.1f, 1000f);
        /*smartPhoneCamera.setLocation(loc);
        smartPhoneCamera.setRotation(cam.getRotation());
        smartPhoneCamera.setAxes(cam.getLeft(), cam.getUp(), cam.getDirection());*/
        
        cameraNode = createCameraNode(smartPhoneCamera);        
        smartphone.attachChild(cameraNode);
                
        ViewPort vp = createViewPort(smartPhoneCamera); 
        
        int width = vp.getCamera().getWidth();
        int height = vp.getCamera().getHeight();
        /*FrameBuffer fb = new FrameBuffer(width, height, 0);
        Texture2D texture = new Texture2D(width, height, Format.RGBA8);        
        fb.setColorTexture(texture);
        fb.setDepthBuffer(Image.Format.Depth);
        //fb.setDepthTexture(texture);
        //fb.setDepthBuffer(Image.Format.Depth24);
        vp.setOutputFrameBuffer(fb);*/
        
        CameraSensor cp = new CameraSensor("CameraSensor-"+smartphone.getName());
        vp.addProcessor(cp);
        
        /*CameraSensor cameraSensor = new CameraSensor("CameraSensor-"+smartphone.getName());        
        //cameraSensor.initialize(renderManager, vp);
        cameraSensor.setViewPort(vp);
        smartphone.addControl(cameraSensor);*/
        
        CameraSensorListenerFrame cameraFrame = new CameraSensorListenerFrame();
        cp.add(cameraFrame);
        
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(480, 800);
        frame.setVisible(true);
        frame.setContentPane(cameraFrame);
        
        frames.add(frame);
        
        bulletAppState.getPhysicsSpace().addAll(smartphone);

        rootNode.attachChild(smartphone);
    }
    
    private ViewPort createViewPort(Camera smartPhoneCamera) {
        float xFactor = (float)cam.getWidth()/(float)smartPhoneCamera.getWidth();
        float yFactor = (float)cam.getHeight()/(float)smartPhoneCamera.getHeight();
        smartPhoneCamera.setViewPort(0f*xFactor, 1f*xFactor, 0f*yFactor, 1f*yFactor);
        ViewPort vp = renderManager.createPreView("asdf", smartPhoneCamera);        
        vp.setClearFlags(true, true, true);        
        vp.setBackgroundColor(ColorRGBA.White);
        vp.attachScene(SpatialFactory.getRootNode());
        return vp;
    }
    
    private CameraNode createCameraNode(Camera smartPhoneCamera) {
        CameraNode camNode = new CameraNode("Camera Node", smartPhoneCamera);
        camNode.setControlDir(CameraControl.ControlDirection.SpatialToCamera);
        camNode.setLocalTranslation(new Vector3f(0, 0, 0));
        return camNode;
    }
    
    @Override
    public void destroy() {        
        super.destroy();
        
        for(JFrame f: frames) {
            f.dispose();
        }
    }
}
