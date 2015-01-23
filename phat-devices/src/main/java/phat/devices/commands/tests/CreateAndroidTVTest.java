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
import phat.devices.commands.DisplayAVDScreenCommand;
import phat.devices.commands.SetAndroidEmulatorCommand;
import phat.devices.commands.SetDeviceInCoordenatesCommand;
import phat.devices.commands.SetDeviceOnFurnitureCommand;
import phat.devices.commands.SetImageOnScreenCommand;
import phat.devices.commands.StartActivityCommand;
import phat.devices.smartphone.SmartPhoneFactory;
import static phat.devices.smartphone.SmartPhoneFactory.camera;
import phat.structures.houses.HouseFactory;
import phat.structures.houses.TestHouse;
import phat.structures.houses.commands.CreateHouseCommand;
import phat.world.WorldAppState;

/**
 *
 * @author pablo
 */
public class CreateAndroidTVTest implements PHATInitAppListener {

    private static final Logger logger = Logger.getLogger(TestHouse.class.getName());
    DevicesAppState devicesAppState;
    SpatialEnvironmentAPI seAPI;

    public static void main(String[] args) {
        CreateAndroidTVTest test = new CreateAndroidTVTest();
        PHATApplication phat = new PHATApplication(test);
        phat.setDisplayFps(true);
        phat.setDisplayStatView(false);
        phat.start();
    }

    @Override
    public void init(SimpleApplication app) {
        AppStateManager stateManager = app.getStateManager();
        
        app.getFlyByCamera().setMoveSpeed(10f);
        camera = app.getCamera();
        camera.setFrustumPerspective(45f, (float) camera.getWidth() / camera.getHeight(), 0.1f, 1000f);
        
        app.getCamera().setLocation(new Vector3f(4.497525f, 6.3693237f, 4.173162f));
        app.getCamera().setRotation(new Quaternion(0.5199084f, 0.42191547f, -0.32954147f, 0.6656463f));

        seAPI = SpatialEnvironmentAPI.createSpatialEnvironmentAPI(app);

        seAPI.getWorldAppState().setCalendar(2013, 1, 1, 12, 0, 0);
        seAPI.getWorldAppState().setLandType(WorldAppState.LandType.Basic);
        seAPI.getHouseAppState().runCommand(new CreateHouseCommand("House1", HouseFactory.HouseType.House3room2bath));
        
        BulletAppState bulletAppState = app.getStateManager().getState(BulletAppState.class);
        SmartPhoneFactory.init(bulletAppState, app.getAssetManager(), app.getRenderManager(), 
                app.getCamera(), app.getAudioRenderer());

        devicesAppState = new DevicesAppState();
        stateManager.attach(devicesAppState);

        //String deviceId = "Smartphone1";
        String deviceId = "AndroidTV1";
        //String avdId = "Nexes_S_API_21";
        String avdId = "AndroidTV1";
        devicesAppState.runCommand(new CreateSmartphoneCommand(deviceId));
        //Vector3f loc = new Vector3f(9.0942f, 0.75f, 4.0266f);
        //devicesAppState.runCommand(new SetDeviceInCoordenatesCommand(deviceId, loc));
        devicesAppState.runCommand(new SetAndroidEmulatorCommand(deviceId, avdId, "emulator-5554"));
        devicesAppState.runCommand(new StartActivityCommand(deviceId, "es.ucm.fdi.grasia.smarttvapp.gui", "TVAppActivity"));
        
        //devicesAppState.runCommand(new SetImageOnScreenCommand(deviceId, "Textures/FrontSmartPhone.jpg"));
        DisplayAVDScreenCommand displayCommand = new DisplayAVDScreenCommand(deviceId, avdId);
        displayCommand.setFrecuency(0.5f);
        devicesAppState.runCommand(displayCommand);
        
    }
}