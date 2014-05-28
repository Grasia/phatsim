package phat.examples.android;

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
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Image;
import com.jme3.texture.Image.Format;
import com.jme3.texture.Texture2D;
import java.util.logging.Level;
import java.util.logging.Logger;
import phat.mobile.adm.AndroidVirtualDevice;
import phat.sensors.camera.CameraSensor;
import phat.server.PHATServerManager;
import phat.server.camera.TCPCameraSensorServer;
import phat.util.Debug;
import phat.util.PHATImageUtils;
import phat.util.SimpleScenario;
import phat.util.SpatialFactory;

/**
 * Test CameraControl and AccelerometerControl attached to a cube. 
 * We can see at up-rigth the perspective view of the cube.
 * 
 * @author pablo
 */
public class CameraSensorAndroid extends SimpleScenario {

    private PHATServerManager serverManager;
    
    public static void main(String [] args) {
        Logger.getGlobal().setLevel(Level.ALL);
        
        PHATImageUtils.printImageFormatNames();
        
        CameraSensorAndroid app = new CameraSensorAndroid();
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
        serverManager = new PHATServerManager();
        createSmartphone("Smartphone1", "emulator-5554", new Vector3f(0f, 5f, 0f), ColorRGBA.Pink);
        createSmartphone("Smartphone2", "emulator-5556", new Vector3f(0f, 5f, 2f), ColorRGBA.Cyan);
    }
    
    private void createSmartphone(String smartphoneId, String emulatorId, Vector3f loc, ColorRGBA color) {
        Node smartphone = new Node(smartphoneId);
        smartphone.setLocalTranslation(loc);
        Geometry geo = SpatialFactory.createCube(Vector3f.UNIT_XYZ.mult(0.1f), color);
        smartphone.attachChild(geo);

        RigidBodyControl rbc = new RigidBodyControl(1);
        smartphone.addControl(rbc);
        
        smartPhoneCamera = /*new Camera(640, 480);*/cam.clone();
        /*smartPhoneCamera.setLocation(loc);
        smartPhoneCamera.setRotation(cam.getRotation());
        smartPhoneCamera.setAxes(cam.getLeft(), cam.getUp(), cam.getDirection());*/
        
        cameraNode = createCameraNode(smartPhoneCamera);        
        smartphone.attachChild(cameraNode);
                
        ViewPort vp = createViewPort(smartPhoneCamera); 
        
        int width = vp.getCamera().getWidth();
        int height = vp.getCamera().getHeight();
        FrameBuffer fb = new FrameBuffer(width, height, 0);
        Texture2D texture = new Texture2D(width, height, Format.RGBA8);        
        fb.setColorTexture(texture);
        fb.setDepthBuffer(Image.Format.Depth);
        //fb.setDepthTexture(texture);
        //fb.setDepthBuffer(Image.Format.Depth24);
        vp.setOutputFrameBuffer(fb);
        
        CameraSensor cameraSensor = new CameraSensor("CameraSensor-"+smartphone.getName());
        vp.addProcessor(cameraSensor);
        
        /*CameraSensor cameraSensor = new CameraSensor("CameraSensor-"+smartphone.getName());        
        //cameraSensor.initialize(renderManager, vp);
        cameraSensor.setViewPort(vp);
        smartphone.addControl(cameraSensor);*/
        
        TCPCameraSensorServer cameraServer = serverManager.createAndStartCameraServer(smartphone.getName(), cameraSensor);
        cameraServer.setRate(0.2f);
        
        AndroidVirtualDevice avd = new AndroidVirtualDevice(smartphone.getName(), 
        		emulatorId, smartphone.getName());
        System.err.println("Despues.................");
        avd.sendConfigFileForService(serverManager.getIP(), serverManager.getPort());
        System.out.println("avd = " + avd);
        System.out.println("unlock()");
        //avd.unlock();
        System.out.println("startActivity");
        avd.startActivity("phat.android.apps", "CameraCaptureActivity");
        //avd.startActivity("phat.android.app.mic", "MainActivity");
        // empieza a procesar audio
        //System.out.println("press start button!");
        //avd.tap(45, 193);
        
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
        
        if(serverManager != null) {
            serverManager.stop();
        }
    }
}
