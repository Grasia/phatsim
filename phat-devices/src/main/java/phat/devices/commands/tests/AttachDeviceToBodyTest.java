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
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.system.AppSettings;

import java.util.logging.Logger;

import phat.app.PHATApplication;
import phat.app.PHATInitAppListener;
import phat.body.BodiesAppState;
import phat.body.commands.RandomWalkingCommand;
import phat.body.commands.SetBodyInCoordenatesCommand;
import phat.body.commands.SetSpeedDisplacemenetCommand;
import phat.devices.DevicesAppState;
import phat.devices.commands.CreateSmartphoneCommand;
import phat.devices.commands.SetDeviceOnPartOfBodyCommand;
import phat.structures.houses.TestHouse;
import phat.util.SpatialFactory;
import phat.world.WorldAppState;

/**
 *
 * @author pablo
 */
public class AttachDeviceToBodyTest implements PHATInitAppListener {

    private static final Logger logger = Logger.getLogger(TestHouse.class.getName());
    BodiesAppState bodiesAppState;
    WorldAppState worldAppState;

    public static void main(String[] args) {
        AttachDeviceToBodyTest test = new AttachDeviceToBodyTest();
        PHATApplication phat = new PHATApplication(test);
        phat.setDisplayFps(true);
        phat.setDisplayStatView(true);
        AppSettings settings = new AppSettings(true);
        settings.setWidth(1024);
        settings.setHeight(720);
        phat.setSettings(settings);
        phat.start();
    }

    @Override
    public void init(SimpleApplication app) {
        SpatialFactory.init(app.getAssetManager(), app.getRootNode());

        AppStateManager stateManager = app.getStateManager();

        app.getFlyByCamera().setMoveSpeed(10f);

        app.getCamera().setLocation(new Vector3f(4.034334f, 3.8802402f, 6.621415f));
        app.getCamera().setRotation(new Quaternion(-7.4161455E-4f, 0.97616464f, -0.21700443f, -0.0033340578f));

        app.getFlyByCamera().setDragToRotate(true);

        worldAppState = new WorldAppState();
        app.getStateManager().attach(worldAppState);
        worldAppState.setCalendar(2013, 1, 1, 12, 0, 0);
        worldAppState.setLandType(WorldAppState.LandType.Basic);
        worldAppState.setTerrainColor(ColorRGBA.White);

        bodiesAppState = new BodiesAppState();
        stateManager.attach(bodiesAppState);

        String bodyId = "body";
        bodiesAppState.createBody(BodiesAppState.BodyType.Elder, bodyId);
        bodiesAppState.runCommand(new SetBodyInCoordenatesCommand(bodyId,
                    Vector3f.ZERO));
        
        DevicesAppState devicesAppState = new DevicesAppState();
        app.getStateManager().attach(devicesAppState);
        devicesAppState.runCommand(new CreateSmartphoneCommand("Smartphone1").setAttachCoordinateAxes(true));
        devicesAppState.runCommand(new SetDeviceOnPartOfBodyCommand(
                bodyId, "Smartphone1", SetDeviceOnPartOfBodyCommand.PartOfBody.RightWrist));
        
        devicesAppState.runCommand(new CreateSmartphoneCommand("Smartphone2").setAttachCoordinateAxes(true));
        devicesAppState.runCommand(new SetDeviceOnPartOfBodyCommand(
                bodyId, "Smartphone2", SetDeviceOnPartOfBodyCommand.PartOfBody.LeftWrist));
    }
}