package phat.world;

import com.jme3.app.SimpleApplication;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import phat.structures.houses.*;
import com.jme3.system.AppSettings;
import java.util.logging.Logger;

/**
 * App for testing mesh navegation in a small house. Starting point is set by
 * left-clicking on the mouse. Ending point is set by right-clicking on the
 * mouse. Pressing space key an animated character follows the path generated.
 *
 * @author Pablo
 */
public class TestWorld extends SimpleApplication {

    private static final Logger logger = Logger.getLogger(TestHouse.class.getName());
    
    public static void main(String[] args) {
        TestWorld app = new TestWorld();
        
        AppSettings settings = new AppSettings(true);
        settings.setWidth(1280);
        settings.setHeight(720);
        app.setSettings(settings);
        app.setDisplayFps(true);
        app.setShowSettings(true);
        app.setPauseOnLostFocus(false);
        app.start();
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        super.simpleUpdate(tpf);
    }
    
    @Override
    public void simpleInitApp() {        
        //Debug.enableDebugGrid(30, assetManager, rootNode);
        
        flyCam.setMoveSpeed(10f);
        flyCam.setDragToRotate(true);
        
        WorldAppState worldAppState = new WorldAppState();                
        getStateManager().attach(worldAppState);
        
        worldAppState.setCalendar(2013, 1, 1, 5, 0, 0);
        worldAppState.setVisibleCalendar(true);
        worldAppState.setEnableShadows(true);
        worldAppState.setSpeedFactor(1000);
        worldAppState.setLandType(WorldAppState.LandType.TwoHouses);
        
        cam.setLocation(new Vector3f(-0.40728146f, 7.5530906f, 20.056808f));
        cam.setRotation(new Quaternion(0.0010348918f, 0.9921636f, -0.12466925f, 0.008235897f));
    }
}
