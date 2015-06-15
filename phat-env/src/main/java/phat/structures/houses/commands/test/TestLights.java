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
package phat.structures.houses.commands.test;

import phat.environment.*;
import com.jme3.app.SimpleApplication;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.system.AppSettings;
import java.util.logging.Logger;
import phat.structures.houses.HouseFactory;
import phat.structures.houses.TestHouse;
import phat.structures.houses.commands.CreateHouseCommand;
import phat.structures.houses.commands.SwitchLight;
import phat.world.WorldAppState;

/**
 *
 * @author pablo
 */
public class TestLights  extends SimpleApplication {

    private static final Logger logger = Logger.getLogger(TestHouse.class.getName());
    private boolean initialized = false;
    
    public static void main(String[] args) {
        TestLights app = new TestLights();
        AppSettings settings = new AppSettings(true);
        settings.setWidth(640);
        settings.setHeight(360);
        settings.setTitle("PHAT");
        app.setSettings(settings);
        app.setShowSettings(false);
        app.setPauseOnLostFocus(false);
        app.setDisplayFps(true);
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
        
        seAPI.getWorldAppState().setCalendar(2013, 1, 1, 1, 0, 0);
        seAPI.getWorldAppState().setEnableShadows(true);
        seAPI.getWorldAppState().setLandType(WorldAppState.LandType.Basic);
        
        seAPI.getHouseAppState().runCommand(new CreateHouseCommand("House1", HouseFactory.HouseType.House3room2bath));
        seAPI.getHouseAppState().runCommand(new SwitchLight("House1", "BedRoom1", true));
        seAPI.getHouseAppState().runCommand(new SwitchLight("House1", "BathRoom1", true));
        initialized = true;
    }
    
    public boolean isInitialized() {
        return initialized;
    }
}