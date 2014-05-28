/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.mason;

import com.jme3.light.AmbientLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.system.AppSettings;
import java.util.logging.Level;
import java.util.logging.Logger;
import phat.audio.SimpleAudioScenario;

/**
 *
 * @author pablo
 */
public class PHATApplication extends SimpleAudioScenario {
    private static final boolean PHYSICS_DEBUG = false;
    
    private MASONAppState masonAppState;
        
    public PHATApplication(MASONAppState masonAppState) {
        super();
        this.masonAppState = masonAppState;        
    }
    
    @Override
    public void setSettings(AppSettings s){
        super.setSettings(s);
        
        settings.setResolution(480, 800);
        settings.setTitle("PHAT Display");
    }
    
    @Override
    public void simpleUpdate(float fps) {
        super.simpleUpdate(fps);
        
    }

    public void setEnabled(boolean enabled) {
        bulletAppState.setEnabled(enabled);
        masonAppState.setEnabled(enabled);
    }
    
    @Override
    public void start() {        
        System.out.println(getClass().getSimpleName()+" start()...");
        super.start();   
        //waitFor();
        System.out.println(getClass().getSimpleName()+" ...start()");
    }
    
    private void waitFor() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            Logger.getLogger(PHATApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * App initialisation. It is invoked by JME3 to create the world
     */
    @Override
    public void simpleInitApp() {
        super.simpleInitApp();
        
        System.out.println(getClass().getSimpleName()+" simpleInitApp()...");
                
        stateManager.attach(masonAppState);
        
        System.out.println(getClass().getSimpleName()+" ...simpleInitApp()");
    }
    
    public AppSettings getAppSettings() {
        return settings;
    }
    
    @Override
    public void createCameras() {
        flyCam.setMoveSpeed(10f);
        flyCam.setDragToRotate(true);// to prevent mouse capture
        cam.setLocation(new Vector3f(6.9417787f, 10.855435f, 5.6912956f));
        cam.setRotation(new Quaternion(0.41481552f, -0.5747476f, 0.421548f, 0.56558853f));        
    }


    @Override
    public void createLight() {
        // We add light so we see the scene
        AmbientLight al = new AmbientLight();
        //al.setColor(ColorRGBA.White.mult(1.3f));
        al.setColor(ColorRGBA.White.mult(0.2f));
        rootNode.addLight(al);
    }

    @Override
    public void initAudio() {
        
    }

    @Override
    public void createTerrain() {
        
    }

    @Override
    public void createOtherObjects() {
        
    }
}