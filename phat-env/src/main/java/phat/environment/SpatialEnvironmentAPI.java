package phat.environment;

import com.jme3.app.SimpleApplication;

import phat.structures.houses.HouseAppState;
import phat.structures.houses.HouseFactory.HouseType;
import phat.util.SpatialFactory;
import phat.world.WorldAppState;

/**
 *
 * @author pablo
 */
public class SpatialEnvironmentAPI {
    SimpleApplication app;
    
    WorldAppState worldAppState;
    HouseAppState houseAppState;
    
    private SpatialEnvironmentAPI(SimpleApplication app) {
        this.app = app;        
        
        SpatialFactory.init(app.getAssetManager(), app.getRootNode());
        
        worldAppState = new WorldAppState();
        app.getStateManager().attach(worldAppState);
        
        houseAppState = new HouseAppState();
        app.getStateManager().attach(houseAppState);
    }
    
    private static SpatialEnvironmentAPI spatialEnvironmentAPI;
    
    public static SpatialEnvironmentAPI createSpatialEnvironmentAPI(SimpleApplication app) {
        if(spatialEnvironmentAPI == null) {
            spatialEnvironmentAPI = new SpatialEnvironmentAPI(app);            
        }
        return spatialEnvironmentAPI;
    }
    
    public static SpatialEnvironmentAPI getInstance() {
        return spatialEnvironmentAPI;
    }
    
    public WorldAppState getWorldAppState() {
        return worldAppState;
    }
    
    public HouseAppState getHouseAppState() {
        return houseAppState;
    }
}
