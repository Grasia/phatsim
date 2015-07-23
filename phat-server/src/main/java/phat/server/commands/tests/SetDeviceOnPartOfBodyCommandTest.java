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
package phat.server.commands.tests;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.system.AppSettings;

import java.util.logging.Logger;

import phat.app.PHATApplication;
import phat.app.PHATInitAppListener;
import phat.body.BodiesAppState;
import phat.body.commands.RandomWalkingCommand;
import phat.body.commands.SetBodyInCoordenatesCommand;
import phat.devices.DevicesAppState;
import phat.devices.commands.CreateSmartphoneCommand;
import phat.devices.commands.SetDeviceOnFurnitureCommand;
import phat.devices.commands.SetDeviceOnPartOfBodyCommand;
import phat.structures.houses.TestHouse;
import phat.util.SpatialFactory;
import phat.world.WorldAppState;

/**
 *
 * @author pablo
 */
public class SetDeviceOnPartOfBodyCommandTest implements PHATInitAppListener {

    private static final Logger logger = Logger.getLogger(TestHouse.class.getName());    
    BodiesAppState bodiesAppState;
    DevicesAppState devicesAppState;
    WorldAppState worldAppState;

    public static void main(String[] args) {
        SetDeviceOnPartOfBodyCommandTest test = new SetDeviceOnPartOfBodyCommandTest();
        PHATApplication phat = new PHATApplication(test);
        phat.setDisplayFps(true);
        phat.setDisplayStatView(false);
        AppSettings settings = new AppSettings(true);
        settings.setWidth(480);
        settings.setHeight(800);
        phat.setSettings(settings);
        phat.start();
    }

    @Override
    public void init(SimpleApplication app) {
        SpatialFactory.init(app.getAssetManager(), app.getRootNode());
        
        AppStateManager stateManager = app.getStateManager();

        app.getFlyByCamera().setMoveSpeed(10f);

        app.getCamera().setLocation(new Vector3f(0.2599395f, 2.7232018f, 3.373138f));
        app.getCamera().setRotation(new Quaternion(-0.0035931943f, 0.9672268f, -0.25351822f, -0.013704466f));

        BulletAppState bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        //bulletAppState.setDebugEnabled(true);

        worldAppState = new WorldAppState();
        worldAppState.setLandType(WorldAppState.LandType.Grass);
        app.getStateManager().attach(worldAppState);
        worldAppState.setCalendar(2013, 1, 1, 12, 0, 0);

        bodiesAppState = new BodiesAppState();
        stateManager.attach(bodiesAppState);

        bodiesAppState.createBody(BodiesAppState.BodyType.ElderLP, "Patient");
        bodiesAppState.runCommand(new SetBodyInCoordenatesCommand("Patient", Vector3f.ZERO));
        bodiesAppState.runCommand(new RandomWalkingCommand("Patient", true));
        //bodiesAppState.runCommand(new SetPCListenerToBodyCommand("Patient"));

        devicesAppState = new DevicesAppState();
        stateManager.attach(devicesAppState);
        
        devicesAppState.runCommand(new CreateSmartphoneCommand("Smartphone1"));
        devicesAppState.runCommand(new SetDeviceOnPartOfBodyCommand("Patient", "Smartphone1", 
                SetDeviceOnPartOfBodyCommand.PartOfBody.Chest));
        
        devicesAppState.runCommand(new CreateSmartphoneCommand("SmartWatch1").setDimensions(0.03f, 0.03f, 0.01f));
        devicesAppState.runCommand(new SetDeviceOnPartOfBodyCommand("Patient", "SmartWatch1", 
                SetDeviceOnPartOfBodyCommand.PartOfBody.LeftWrist));
        devicesAppState.runCommand(new SetDeviceOnFurnitureCommand("deviceId", "House1", "furnitureId"));
        
        //devicesAppState.runCommand(new SetAndroidEmulatorCommand("Smartphone1", "Smartphone1", "emulator-5554"));
        //devicesAppState.runCommand(new StartActivityCommand("Smartphone1", "phat.android.apps", "CameraCaptureActivity"));
        
        /*devicesAppState.runCommand(new CreateSmartphoneCommand("Smartphone2"));
        devicesAppState.runCommand(new SetDeviceOnPartOfBodyCommand("Patient", "Smartphone2", 
                SetDeviceOnPartOfBodyCommand.PartOfBody.RightHand));
        
        devicesAppState.runCommand(new CreateSmartphoneCommand("Smartphone3"));
        devicesAppState.runCommand(new SetDeviceOnPartOfBodyCommand("Patient", "Smartphone3", 
                SetDeviceOnPartOfBodyCommand.PartOfBody.RightWrist));
        
        devicesAppState.runCommand(new CreateSmartphoneCommand("Smartphone4"));
        devicesAppState.runCommand(new SetDeviceOnPartOfBodyCommand("Patient", "Smartphone4", 
                SetDeviceOnPartOfBodyCommand.PartOfBody.LeftWrist));
        
        devicesAppState.runCommand(new CreateSmartphoneCommand("Smartphone5"));
        devicesAppState.runCommand(new SetDeviceOnPartOfBodyCommand("Patient", "Smartphone5", 
                SetDeviceOnPartOfBodyCommand.PartOfBody.RightWrist));
        
        devicesAppState.runCommand(new CreateSmartphoneCommand("Smartphone6"));
        devicesAppState.runCommand(new SetDeviceOnPartOfBodyCommand("Patient", "Smartphone6", 
                SetDeviceOnPartOfBodyCommand.PartOfBody.RightUnkle));
        
        devicesAppState.runCommand(new CreateSmartphoneCommand("Smartphone7"));
        devicesAppState.runCommand(new SetDeviceOnPartOfBodyCommand("Patient", "Smartphone7", 
                SetDeviceOnPartOfBodyCommand.PartOfBody.LeftUnkle));*/
    }
}