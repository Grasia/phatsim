/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.environment;

import com.jme3.app.SimpleApplication;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.system.AppSettings;
import java.util.logging.Logger;
import phat.structures.houses.HouseFactory;
import phat.structures.houses.TestHouse;
import phat.structures.houses.commands.CreateHouseCommand;
import phat.world.WorldAppState;

/**
 *
 * @author pablo
 */
public class TestSpatialEnvironmentAPI  extends SimpleApplication {

    private static final Logger logger = Logger.getLogger(TestHouse.class.getName());
    private boolean initialized = false;
    
    public static void main(String[] args) {
        TestSpatialEnvironmentAPI app = new TestSpatialEnvironmentAPI();
        AppSettings settings = new AppSettings(true);
        settings.setWidth(640);
        settings.setHeight(360);
        settings.setTitle("PHAT");
        app.setSettings(settings);
        app.setShowSettings(false);
        app.setPauseOnLostFocus(false);
        app.setDisplayFps(false);
        app.setDisplayStatView(false);        
        
        app.start();
    }


    @Override
    public void simpleUpdate(float tpf) {
        super.simpleUpdate(tpf);        
    }  

    @Override
    public void simpleInitApp() {
        flyCam.setMoveSpeed(10f);
        cam.setLocation(new Vector3f(-0.40728146f, 7.5530906f, 20.056808f));
        cam.setRotation(new Quaternion(0.0010348918f, 0.9921636f, -0.12466925f, 0.008235897f));
        
        SpatialEnvironmentAPI seAPI = SpatialEnvironmentAPI.createSpatialEnvironmentAPI(this);
        
        seAPI.getWorldAppState().setCalendar(2013, 1, 1, 12, 0, 0);
        seAPI.getWorldAppState().setEnableShadows(true);
        seAPI.getWorldAppState().setLandType(WorldAppState.LandType.TwoHouses);
        
        seAPI.getHouseAppState().runCommand(new CreateHouseCommand("House1", HouseFactory.HouseType.House3room2bath));
        initialized = true;
    }
    
    public boolean isInitialized() {
        return initialized;
    }
}