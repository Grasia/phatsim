/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.sensors.accelerometer;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
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
import javax.swing.JFrame;
import phat.sensors.camera.CameraSensor;
import phat.sensors.camera.CameraSensorListenerFrame;
import phat.util.Debug;
import phat.util.SimpleScenario;
import phat.util.SpatialFactory;

/**
 * Test CameraControl and AccelerometerControl attached to a cube. 
 * We can see at up-rigth the perspective view of the cube.
 * 
 * @author pablo
 */
public class CameraAndAccelerometerControlAppTest extends SimpleScenario {

    List<JFrame> frames = new ArrayList<JFrame>();
    
    public static void main(String [] args) {
        CameraAndAccelerometerControlAppTest app = new CameraAndAccelerometerControlAppTest();
        app.setPauseOnLostFocus(false);
        app.setShowSettings(false);
        
        AppSettings settings = new AppSettings(true);
        settings.setWidth(1280);
        settings.setHeight(720);
        app.setSettings(settings);
        
        app.start();
    }
    
    @Override
    public void createTerrain() {
        Debug.enableDebugGrid(10, assetManager, rootNode);
        bulletAppState.getPhysicsSpace().enableDebug(assetManager);
        
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
        Node smartphone = createSmartphone("Smartphone1", new Vector3f(0f, 1f, 0f), ColorRGBA.Pink);
        
        AccelerometerControl ac = new AccelerometerControl("Box");
        smartphone.addControl(ac);
        
        XYAccelerationsChart chart = new XYAccelerationsChart("Chart - Acc.", "Box accelerations", "m/s2", "x,y,z");
        ac.add(chart);
        chart.showWindow();
        
        bulletAppState.getPhysicsSpace().add(smartphone);
        
        rootNode.attachChild(smartphone);
        
        System.out.println(cam.getWidth()+"x"+cam.getHeight());
        //cc.start(cam, renderManager, rootNode, 0, 0.2f, 0.8f, 1f);
    }
    
    private Node createSmartphone(String smartphoneId, Vector3f loc, ColorRGBA color) {
        Node smartphone = new Node(smartphoneId);
        smartphone.setLocalTranslation(loc);
        Geometry geo = SpatialFactory.createCube(Vector3f.UNIT_XYZ.mult(0.1f), color);
        smartphone.attachChild(geo);
        RigidBodyControl rbc = new RigidBodyControl(1);
        smartphone.addControl(rbc);
        
        Camera smartPhoneCamera = /*new Camera(640, 480);*/cam.clone();
        /*smartPhoneCamera.setLocation(loc);
        smartPhoneCamera.setRotation(cam.getRotation());
        smartPhoneCamera.setAxes(cam.getLeft(), cam.getUp(), cam.getDirection());*/
        
        CameraNode cameraNode = createCameraNode(smartPhoneCamera);        
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
        return smartphone;
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
