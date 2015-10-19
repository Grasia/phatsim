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
package phat.devices.commands.tests;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import phat.environment.*;

import java.util.logging.Logger;

import phat.app.PHATApplication;
import phat.app.PHATInitAppListener;
import phat.devices.DevicesAppState;
import phat.devices.commands.CreateSmartphoneCommand;
import phat.devices.commands.SetDeviceOnFurnitureCommand;
import phat.structures.houses.HouseFactory;
import phat.structures.houses.TestHouse;
import phat.structures.houses.commands.CreateHouseCommand;
import phat.world.WorldAppState;

/**
 *
 * @author pablo
 */
public class CreateSmartphoneTest implements PHATInitAppListener {

    private static final Logger logger = Logger.getLogger(TestHouse.class.getName());
    DevicesAppState devicesAppState;
    SpatialEnvironmentAPI seAPI;

    public static void main(String[] args) {
        CreateSmartphoneTest test = new CreateSmartphoneTest();
        PHATApplication phat = new PHATApplication(test);
        phat.setDisplayFps(true);
        phat.setDisplayStatView(false);
        phat.start();
    }

    @Override
    public void init(SimpleApplication app) {
        AppStateManager stateManager = app.getStateManager();

        app.getFlyByCamera().setMoveSpeed(10f);

        app.getCamera().setLocation(new Vector3f(4.497525f, 6.3693237f, 4.173162f));
        app.getCamera().setRotation(new Quaternion(0.5199084f, 0.42191547f, -0.32954147f, 0.6656463f));

        BulletAppState bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        //bulletAppState.setDebugEnabled(true);

        seAPI = SpatialEnvironmentAPI.createSpatialEnvironmentAPI(app);

        seAPI.getWorldAppState().setCalendar(2013, 1, 1, 12, 0, 0);
        seAPI.getWorldAppState().setLandType(WorldAppState.LandType.Basic);
        seAPI.getHouseAppState().runCommand(new CreateHouseCommand("House1", HouseFactory.HouseType.BrickHouse60m));
        

        devicesAppState = new DevicesAppState();
        stateManager.attach(devicesAppState);

        devicesAppState.runCommand(new CreateSmartphoneCommand("Smartphone11"));
        devicesAppState.runCommand(new SetDeviceOnFurnitureCommand("Smartphone11", "House1", "Table1"));
        devicesAppState.runCommand(new CreateSmartphoneCommand("Smartphone12"));
        devicesAppState.runCommand(new SetDeviceOnFurnitureCommand("Smartphone12", "House1", "Table1"));
        devicesAppState.runCommand(new CreateSmartphoneCommand("Smartphone13"));
        devicesAppState.runCommand(new SetDeviceOnFurnitureCommand("Smartphone13", "House1", "Table1"));
        devicesAppState.runCommand(new CreateSmartphoneCommand("Smartphone14"));
        devicesAppState.runCommand(new SetDeviceOnFurnitureCommand("Smartphone14", "House1", "Table1"));
        
        devicesAppState.runCommand(new CreateSmartphoneCommand("Smartphone31"));
        devicesAppState.runCommand(new SetDeviceOnFurnitureCommand("Smartphone31", "House1", "Table2"));
        devicesAppState.runCommand(new CreateSmartphoneCommand("Smartphone32"));
        devicesAppState.runCommand(new SetDeviceOnFurnitureCommand("Smartphone32", "House1", "Table2"));
        devicesAppState.runCommand(new CreateSmartphoneCommand("Smartphone33"));
        devicesAppState.runCommand(new SetDeviceOnFurnitureCommand("Smartphone33", "House1", "Table2"));
        devicesAppState.runCommand(new CreateSmartphoneCommand("Smartphone34"));
        devicesAppState.runCommand(new SetDeviceOnFurnitureCommand("Smartphone34", "House1", "Table2"));
        
        devicesAppState.runCommand(new CreateSmartphoneCommand("Smartphone2"));
        devicesAppState.runCommand(new SetDeviceOnFurnitureCommand("Smartphone2", "House1", "Microwave"));
        devicesAppState.runCommand(new CreateSmartphoneCommand("Smartphone21"));
        devicesAppState.runCommand(new SetDeviceOnFurnitureCommand("Smartphone21", "House1", "Microwave"));
        
        devicesAppState.runCommand(new CreateSmartphoneCommand("Smartphone3"));
        devicesAppState.runCommand(new SetDeviceOnFurnitureCommand("Smartphone3", "House1", "Sofa3Seats"));
        
        devicesAppState.runCommand(new CreateSmartphoneCommand("Smartphone4"));
        devicesAppState.runCommand(new SetDeviceOnFurnitureCommand("Smartphone4", "House1", "Sofa3Seats"));  
        
        devicesAppState.runCommand(new CreateSmartphoneCommand("Smartphone5"));
        devicesAppState.runCommand(new SetDeviceOnFurnitureCommand("Smartphone5", "House1", "Sofa3Seats"));
        
        devicesAppState.runCommand(new CreateSmartphoneCommand("Smartphone6"));
        devicesAppState.runCommand(new SetDeviceOnFurnitureCommand("Smartphone6", "House1", "Sofa3Seats"));
        
        devicesAppState.runCommand(new CreateSmartphoneCommand("Smartphone7"));
        devicesAppState.runCommand(new SetDeviceOnFurnitureCommand("Smartphone7", "House1", "Sofa3Seats"));
        
        devicesAppState.runCommand(new CreateSmartphoneCommand("Smartphone8"));
        devicesAppState.runCommand(new SetDeviceOnFurnitureCommand("Smartphone8", "House1", "Sofa3Seats"));
        
        devicesAppState.runCommand(new CreateSmartphoneCommand("Smartphone81"));
        devicesAppState.runCommand(new SetDeviceOnFurnitureCommand("Smartphone81", "House1", "Basin1"));
        devicesAppState.runCommand(new CreateSmartphoneCommand("Smartphone82"));
        devicesAppState.runCommand(new SetDeviceOnFurnitureCommand("Smartphone82", "House1", "Basin1"));
        
        devicesAppState.runCommand(new CreateSmartphoneCommand("Smartphone83"));
        devicesAppState.runCommand(new SetDeviceOnFurnitureCommand("Smartphone83", "House1", "WC1"));
        devicesAppState.runCommand(new CreateSmartphoneCommand("Smartphone84"));
        devicesAppState.runCommand(new SetDeviceOnFurnitureCommand("Smartphone84", "House1", "WC1"));
    }
}