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
package phat.devices.smartphone;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioRenderer;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.control.BillboardControl;
import com.jme3.scene.control.CameraControl;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.texture.plugins.AWTLoader;
import java.awt.image.BufferedImage;
import phat.devices.actuators.Actuator;
import phat.devices.actuators.ActuatorListener;
import phat.devices.actuators.VibratorActuator;
import phat.sensors.accelerometer.AccelerometerControl;
import phat.sensors.camera.CameraSensor;
import phat.sensors.microphone.MicrophoneControl;
import phat.util.SpatialFactory;

/**
 *
 * @author PyP
 */
public class SmartPhoneFactory {

    public static AssetManager assetManager;
    public static RenderManager renderManager;
    public static AudioRenderer audioRenderer;
    public static Camera camera;
    public static BulletAppState bulletAppState;

    public static Material blackMat;
    public static Material screenMat;
    
    public static void init(BulletAppState bulletAppState, AssetManager assetManager,
            RenderManager renderManager, Camera camera,
            AudioRenderer audioRenderer) {
        SmartPhoneFactory.bulletAppState = bulletAppState;
        SmartPhoneFactory.assetManager = assetManager;
        SmartPhoneFactory.renderManager = renderManager;
        SmartPhoneFactory.camera = camera;
        SmartPhoneFactory.audioRenderer = audioRenderer;
        
        screenMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        screenMat.setTexture("ColorMap", assetManager.loadTexture("Textures/FrontSmartPhone.jpg"));
        
        blackMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        blackMat.setColor("Color", ColorRGBA.Black);
    }
    
    public static Node createAccelGeometry(String sensorId, Vector3f dimensions) {
        Node sensor = new Node(sensorId);
        
        sensor.setUserData("ID", sensorId);
        sensor.setUserData("ROLE", "Accelerometer");
        sensor.setUserData("TYPE", "Sensor");
        
        Box box = new Box(dimensions.x, dimensions.y, dimensions.z);
        Geometry deviceBody = new Geometry(sensorId, box);
        deviceBody.setMaterial(blackMat);
        sensor.attachChild(deviceBody);
        
        
        RigidBodyControl rbc = new RigidBodyControl(new BoxCollisionShape(), 5f);
        sensor.addControl(rbc);
        rbc.setFriction(1f);
        
        return sensor;
    }

    public static Node createSmartphoneGeometry(String smartphoneId, Vector3f dimensions) {
        Node smartphone = new Node(smartphoneId);
        
        smartphone.setUserData("ID", smartphoneId);
        smartphone.setUserData("ROLE", "AndroidDevice");
        smartphone.setUserData("TYPE", "Smartphone");
        
        Box box = new Box(dimensions.x, dimensions.y, dimensions.z);
        Geometry deviceBody = new Geometry(smartphoneId, box);
//deviceBody.rotate(0f, 0f, FastMath.DEG_TO_RAD*90f);
        deviceBody.setMaterial(blackMat);
        smartphone.attachChild(deviceBody);
        
        Geometry screen = createDisplayGeometry("Screen1", dimensions.x*2f*0.9f, dimensions.y*2f*0.9f);
//screen.rotate(0f, FastMath.DEG_TO_RAD*180f, FastMath.DEG_TO_RAD*90f);
        screen.setMaterial(screenMat);
        smartphone.attachChild(screen);
//screen.move(0f, 0f, dimensions.z+0.001f);
        screen.move(-dimensions.x, -dimensions.y, dimensions.z+0.001f);
        
        RigidBodyControl rbc = new RigidBodyControl(new BoxCollisionShape(), 5f);
        smartphone.addControl(rbc);
        rbc.setFriction(1f);
        
        return smartphone;
    }
    
    public static Geometry createDisplayGeometry(String id, float width, float height) {
        Geometry screen = new Geometry("Screen", new Quad(width, height));
        screen.setUserData("ID", id);
        screen.setUserData("ROLE", "Screen");
        
        return screen;
    }

    public static void setImageOnTexture(Geometry geo, BufferedImage bufImg) {
        Texture texture = new Texture2D(bufImg.getWidth(), bufImg.getHeight(), Image.Format.Depth24);
        texture.setImage(new AWTLoader().load(bufImg, false));
        geo.getMaterial().setTexture("ColorMap", texture);
    }

    public static BitmapText showName(Node node) {
        BitmapFont guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText ch = new BitmapText(guiFont, false);
        ch.setSize(guiFont.getCharSet().getRenderedSize() * 0.02f);
        ch.setText(node.getName()); // crosshairs
        ch.setColor(new ColorRGBA(1f, 0.8f, 0.3f, 0.8f));

        // controlador para que los objetos miren a la cámara.
        BillboardControl control = new BillboardControl();
        ch.addControl(control);
        node.attachChild(ch);
        return ch;
    }

    public static Node createSmartphone(String smartphoneId, Vector3f dimensions) {
        return createSmartphoneGeometry(smartphoneId, dimensions);
    }

    public static void enableCameraFacility(Node smartphone) {
        enableCameraFacility(smartphone, 43f);
    }
    
    public static void enableCameraFacility(Node smartphone, float fovY) {
        // TODO cambiar resolución
        Camera smartPhoneCamera = new Camera(480,800);//camera.clone(); 
        smartPhoneCamera.setFrustumPerspective(fovY, (float) camera.getWidth() / camera.getHeight(), 0.01f, 1000f);
        /*smartPhoneCamera.setLocation(loc);
         smartPhoneCamera.setRotation(cam.getRotation());
         smartPhoneCamera.setAxes(cam.getLeft(), cam.getUp(), cam.getDirection());*/

        CameraNode cameraNode = createCameraNode(smartPhoneCamera);
        smartphone.attachChild(cameraNode);

        ViewPort vp = createViewPort(smartPhoneCamera);

        int width = vp.getCamera().getWidth();
        int height = vp.getCamera().getHeight();
        FrameBuffer fb = new FrameBuffer(width, height, 0);
        Texture2D texture = new Texture2D(width, height, Image.Format.RGBA8);
        fb.setColorTexture(texture);
        fb.setDepthBuffer(Image.Format.Depth);
        //fb.setDepthTexture(texture);
        //fb.setDepthBuffer(Image.Format.Depth24);
        vp.setOutputFrameBuffer(fb);

        CameraSensor cameraSensor = new CameraSensor("CameraSensor-" + smartphone.getName());
        cameraSensor.setPeriod(0.5f);
        //cameraSensor.setEnabled(false);
        smartphone.addControl(cameraSensor);
        vp.addProcessor(cameraSensor);
        cameraSensor.setEnabled(false);

        /*CameraSensor cameraSensor = new CameraSensor("CameraSensor-"+smartphone.getName());        
         //cameraSensor.initialize(renderManager, vp);
         cameraSensor.setViewPort(vp);
         smartphone.addControl(cameraSensor);*/
    }

    public static void enableMicrophoneFacility(Node smartphone) {
        MicrophoneControl micControl = new MicrophoneControl("Micro-" + smartphone.getName(), 10000, audioRenderer);
        smartphone.addControl(micControl);
        micControl.setEnabled(false);
    }

    public static void enableAccelerometerFacility(Node smartphone) {
        AccelerometerControl accControl = new AccelerometerControl("Accelerometer-" + smartphone.getName());
        smartphone.addControl(accControl);
        accControl.setEnabled(false);
        //accControl.start(bulletAppState.getPhysicsSpace());
    }

    public static void enableVibratorFacility(Node smartphone) {
        VibratorActuator vibratorControl = new VibratorActuator(smartphone.getName()+"-Vibrator");
        vibratorControl.setDebug(true);
        smartphone.addControl(vibratorControl);
    }
    
    private static ViewPort createViewPort(Camera smartPhoneCamera) {
        float xFactor = (float) camera.getWidth() / (float) smartPhoneCamera.getWidth();
        float yFactor = (float) camera.getHeight() / (float) smartPhoneCamera.getHeight();
        smartPhoneCamera.setViewPort(0f * xFactor, 1f * xFactor, 0f * yFactor, 1f * yFactor);
        ViewPort vp = renderManager.createPreView("asdf", smartPhoneCamera);
        vp.setClearFlags(true, true, true);
        vp.setBackgroundColor(ColorRGBA.White);
        vp.attachScene(SpatialFactory.getRootNode());
        return vp;
    }

    private static CameraNode createCameraNode(Camera smartPhoneCamera) {
        CameraNode camNode = new CameraNode("Camera Node", smartPhoneCamera);
        camNode.setControlDir(CameraControl.ControlDirection.SpatialToCamera);
        camNode.setLocalTranslation(new Vector3f(0, 0, 0));
        return camNode;
    }
}
