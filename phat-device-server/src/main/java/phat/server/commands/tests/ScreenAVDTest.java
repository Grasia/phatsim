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

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;

import java.util.logging.Logger;

import phat.app.PHATApplication;
import phat.app.PHATInitAppListener;
import phat.body.BodiesAppState;
import phat.body.commands.CreateBodyTypeCommand;
import phat.body.commands.FallDownCommand;
import phat.body.commands.RandomWalkingCommand;
import phat.body.commands.SetBodyHeightCommand;
import phat.body.commands.SetBodyInCoordenatesCommand;
import phat.body.commands.StandUpCommand;
import phat.body.commands.TremblingHandCommand;
import phat.commands.PHATCommand;
import phat.devices.DevicesAppState;
import phat.devices.commands.CreateSmartphoneCommand;
import phat.devices.commands.SetDeviceInCoordenatesCommand;
import phat.server.ServerAppState;
import phat.server.commands.DisplayAVDScreenCommand;
import phat.server.commands.SetAndroidEmulatorCommand;
import phat.structures.houses.HouseAppState;
import phat.structures.houses.TestHouse;
import phat.util.Debug;
import phat.util.SpatialFactory;
import phat.world.WorldAppState;

/**
 *
 * @author pablo
 */
public class ScreenAVDTest implements PHATInitAppListener {

    private static final Logger logger = Logger.getLogger(TestHouse.class.getName());
    ServerAppState serverAppState;
    DevicesAppState devicesAppState;
    WorldAppState worldAppState;
    HouseAppState houseAppState;
    BodiesAppState bodiesAppState;

    public static void main(String[] args) {
        ScreenAVDTest test = new ScreenAVDTest();
        PHATApplication phat = new PHATApplication(test);
        phat.setDisplayFps(true);
        phat.setDisplayStatView(false);
        AppSettings settings = new AppSettings(true);
        settings.setTitle("PHAT");
        settings.setWidth(640);
        settings.setHeight(480);
        phat.setSettings(settings);
        phat.start();
    }

    @Override
    public void init(SimpleApplication app) {
        SpatialFactory.init(app.getAssetManager(), app.getRootNode());

        AppStateManager stateManager = app.getStateManager();

        app.getFlyByCamera().setMoveSpeed(10f);

        //app.getCamera().setLocation(new Vector3f(0.2599395f, 2.7232018f, 3.373138f));
        //app.getCamera().setRotation(new Quaternion(-0.0035931943f, 0.9672268f, -0.25351822f, -0.013704466f));
        app.getCamera().setLocation(new Vector3f(3.619537f, 2.4257905f, 6.62355f));
        app.getCamera().setRotation(new Quaternion(0.26168963f, -0.47725555f, 0.15136504f, 0.8251268f));

        BulletAppState bulletAppState = new BulletAppState();
        bulletAppState.setThreadingType(BulletAppState.ThreadingType.PARALLEL);
        stateManager.attach(bulletAppState);
        bulletAppState.getPhysicsSpace().setAccuracy(1 / 60f);
        //bulletAppState.setDebugEnabled(true);

        worldAppState = new WorldAppState();
        worldAppState.setLandType(WorldAppState.LandType.Grass);
        app.getStateManager().attach(worldAppState);
        worldAppState.setCalendar(2013, 1, 1, 12, 0, 0);

        /*
        houseAppState = new HouseAppState();
        houseAppState.runCommand(new CreateHouseCommand("House1", HouseFactory.HouseType.House3room2bath));
        app.getStateManager().attach(houseAppState);
        //Debug.enableDebugGrid(10, app.getAssetManager(), app.getRootNode());

        bodiesAppState = new BodiesAppState();
        app.getStateManager().attach(bodiesAppState);
        bodiesAppState.createBody(BodiesAppState.BodyType.Elder, "Patient");
        bodiesAppState.runCommand(
                new SetBodyInCoordenatesCommand("Patient", new Vector3f(1.5f, 0.0f, 7.45f)));
        bodiesAppState.runCommand(new SetBodyHeightCommand("Patient", 1.7f));*/
                
        devicesAppState = new DevicesAppState();
        stateManager.attach(devicesAppState);

        devicesAppState.runCommand(new CreateSmartphoneCommand("Smartphone1"));
        SetDeviceInCoordenatesCommand command = 
                new SetDeviceInCoordenatesCommand("Smartphone1", new Vector3f(2.05f, 0.9f, 7.95722f));
        command.setScale(1f);
        command.setRotation(new Quaternion().fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_X));
        devicesAppState.runCommand(command);

        serverAppState = new ServerAppState();
        stateManager.attach(serverAppState);
        
        serverAppState.runCommand(new SetAndroidEmulatorCommand("Smartphone1", "Smartphone1", "emulator-5554"));
        DisplayAVDScreenCommand displayCommand = new DisplayAVDScreenCommand("Smartphone1", "Smartphone1");
        displayCommand.setFrecuency(0.5f);
        serverAppState.runCommand(displayCommand);
    }
}