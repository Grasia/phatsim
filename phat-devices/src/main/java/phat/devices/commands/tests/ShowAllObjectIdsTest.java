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

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import java.util.HashMap;
import java.util.Map;

import phat.environment.*;

import java.util.logging.Logger;

import phat.app.PHATApplication;
import phat.app.PHATInitAppListener;
import phat.body.commands.ShowLabelsOfVisibleObjectsCommand;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.commands.ShowLabelOfObjectById;
import phat.devices.DevicesAppState;
import phat.devices.commands.CreateSmartphoneCommand;
import phat.devices.commands.SetDeviceOnFurnitureCommand;
import phat.structures.houses.HouseAppState;
import phat.structures.houses.HouseFactory;
import phat.structures.houses.TestHouse;
import phat.structures.houses.commands.CreateHouseCommand;
import phat.structures.houses.commands.test.ShowAllLabelsOfScenario;
import phat.util.SpatialFactory;
import phat.util.SpatialUtils;
import phat.world.WorldAppState;

/**
 *
 * @author pablo
 */
public class ShowAllObjectIdsTest implements PHATInitAppListener {

    private static final Logger logger = Logger.getLogger(TestHouse.class.getName());
    DevicesAppState devicesAppState;
    SpatialEnvironmentAPI seAPI;

    public static void main(String[] args) {
        ShowAllObjectIdsTest test = new ShowAllObjectIdsTest();
        PHATApplication phat = new PHATApplication(test);
        phat.setDisplayFps(true);
        AppSettings settings = new AppSettings(true);
        settings.setWidth(1280);
        settings.setHeight(720);
        phat.setSettings(settings);
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
        seAPI.getHouseAppState().runCommand(new CreateHouseCommand("House1", HouseFactory.HouseType.House3room2bath, new PHATCommandListener() {

            @Override
            public void commandStateChanged(PHATCommand command) {
                showLabels();
                seAPI.getHouseAppState().runCommand(new ShowAllLabelsOfScenario(true));
            }
        }));


        devicesAppState = new DevicesAppState();
        stateManager.attach(devicesAppState);
        
    }

    private void showLabels() {
        Map<String, Spatial> objects = new HashMap<String, Spatial>();

        SpatialUtils.getAllSpatialWithId(SpatialFactory.getRootNode(), objects);

        System.out.println("Objects with ids:");
        for (String id : objects.keySet()) {
            System.out.println("\t* " + id);
            seAPI.getHouseAppState().runCommand(new ShowLabelOfObjectById(id, true));
        }
    }
}