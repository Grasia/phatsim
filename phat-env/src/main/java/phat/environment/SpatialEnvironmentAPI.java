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
