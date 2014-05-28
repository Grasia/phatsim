package phat.structures.houses;

import com.jme3.app.SimpleApplication;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.system.AppSettings;
import java.util.logging.Logger;
import phat.structures.houses.commands.CreateHouseCommand;
import phat.structures.houses.commands.DebugShowHouseNavMeshCommand;
import phat.world.WorldAppState;

/**
 * App for testing mesh navegation in a small house. Starting point is set by
 * left-clicking on the mouse. Ending point is set by right-clicking on the
 * mouse. Pressing space key an animated character follows the path generated.
 *
 * @author Pablo
 */
public class TestHouse extends SimpleApplication {

    private static final Logger logger = Logger.getLogger(TestHouse.class.getName());
    private boolean initialized = false;
    
    WorldAppState worldAppState;
    HouseAppState houseAppState;
            
    public static void main(String[] args) {
        TestHouse app = new TestHouse();
        AppSettings settings = new AppSettings(true);
        settings.setWidth(1280);
        settings.setHeight(720);
        app.setSettings(settings);
        app.setDisplayFps(true);
        app.setShowSettings(false);
        app.start();
    }


    @Override
    public void simpleUpdate(float tpf) {
        super.simpleUpdate(tpf);        
    }  

    @Override
    public void simpleInitApp() {
        flyCam.setMoveSpeed(10f);
        
        cam.setLocation(new Vector3f(6.4812074f, 14.044729f, 11.929495f));
        cam.setRotation(new Quaternion(0.f, 0.8630833f, -0.50487673f, 0.011799832f));
        
        flyCam.setDragToRotate(true);
        
        worldAppState = new WorldAppState();
        getStateManager().attach(worldAppState);
        
        worldAppState.setHour(5);
        worldAppState.setVisibleCalendar(true);
        worldAppState.setEnableShadows(false);
        worldAppState.setSpeedFactor(1000);
        worldAppState.setLandType(WorldAppState.LandType.TwoHouses);

        houseAppState = new HouseAppState();
        houseAppState.runCommand(new CreateHouseCommand("House1", HouseFactory.HouseType.House3room2bath));
        houseAppState.runCommand(new CreateHouseCommand("House2", HouseFactory.HouseType.House3room2bath));
        houseAppState.runCommand(new DebugShowHouseNavMeshCommand(true));
        getStateManager().attach(houseAppState);
        
        /*getStateManager().attach(new AbstractAppState() {
            
            float counter = 0f;
            boolean on = false;
            @Override
            public void update(float tpf) {
                super.update(tpf);
                
                counter += tpf;
                
                if(counter > 10f) {
                    houseAppState.getHouse().switchLights("LivingRoom", on);
                    houseAppState.getHouse().switchLights("Kitchen", on);
                    houseAppState.getHouse().switchLights("Hall", on);
                    
                    counter = 0f;
                    on = !on;
                }
            }
        });*/
        initialized = true;        
    }
    
    public boolean isInitialized() {
        return initialized;
    }
}
