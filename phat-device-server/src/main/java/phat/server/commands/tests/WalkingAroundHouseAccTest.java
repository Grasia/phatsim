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
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import java.util.List;
import java.util.Random;

import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;

import phat.app.PHATApplication;
import phat.app.PHATInitAppListener;
import phat.body.BodiesAppState;
import phat.body.commands.GoToSpaceCommand;
import phat.body.commands.SetBodyInHouseSpaceCommand;
import phat.body.commands.SetCameraToBodyCommand;
import phat.body.commands.SetSpeedDisplacemenetCommand;
import phat.body.commands.TremblingHandCommand;
import phat.commands.PHATCommand;
import phat.devices.DevicesAppState;
import phat.commands.PHATCommandListener;
import phat.devices.commands.CreateAccelerometerSensorCommand;
import phat.devices.commands.CreatePresenceSensorCommand;
import phat.devices.commands.SetDeviceOnPartOfBodyCommand;
import phat.sensors.accelerometer.AccelerometerControl;
import phat.sensors.accelerometer.XYAccelerationsChart;
import phat.sensors.presence.PHATPresenceSensor;
import phat.sensors.presence.PresenceStatePanel;
import phat.server.ServerAppState;
import phat.server.commands.CreateAllPresenceSensorServersCommand;
import phat.structures.houses.HouseAppState;
import phat.structures.houses.HouseFactory;
import phat.structures.houses.TestHouse;
import phat.structures.houses.commands.CreateHouseCommand;
import phat.util.SpatialFactory;
import phat.world.WorldAppState;

/**
 *
 * @author pablo
 */
public class WalkingAroundHouseAccTest implements PHATInitAppListener, PHATCommandListener {

    private static final Logger logger = Logger.getLogger(TestHouse.class.getName());
    BodiesAppState bodiesAppState;
    DevicesAppState devicesAppState;
    WorldAppState worldAppState;
    HouseAppState houseAppState;
    String bodyId = "Patient";
    String houseId = "House1";
    JFrame sensorMonitor;

    public static void main(String[] args) {
        WalkingAroundHouseAccTest test = new WalkingAroundHouseAccTest();
        PHATApplication phat = new PHATApplication(test);
        phat.setDisplayFps(true);
        phat.setDisplayStatView(false);
        AppSettings settings = new AppSettings(true);
        settings.setTitle("PHAT");
        settings.setWidth(1280);
        settings.setHeight(720);
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
        bulletAppState.setThreadingType(BulletAppState.ThreadingType.PARALLEL);
        stateManager.attach(bulletAppState);
        bulletAppState.getPhysicsSpace().setAccuracy(1 / 60f);
        //bulletAppState.setDebugEnabled(true);

        worldAppState = new WorldAppState();
        worldAppState.setLandType(WorldAppState.LandType.Grass);
        app.getStateManager().attach(worldAppState);
        worldAppState.setCalendar(2013, 1, 1, 12, 0, 0);

        houseAppState = new HouseAppState();
        houseAppState.runCommand(new CreateHouseCommand(houseId, HouseFactory.HouseType.House3room2bath));
        app.getStateManager().attach(houseAppState);

        //Debug.enableDebugGrid(10, app.getAssetManager(), app.getRootNode());
        bodiesAppState = new BodiesAppState();
        stateManager.attach(bodiesAppState);

        bodiesAppState.createBody(BodiesAppState.BodyType.Elder, bodyId);
        //bodiesAppState.runCommand(new SetBodyInCoordenatesCommand(bodyId, Vector3f.ZERO));
        bodiesAppState.runCommand(new SetBodyInHouseSpaceCommand(bodyId, houseId, "Kitchen"));
        //bodiesAppState.runCommand(new RandomWalkingCommand(bodyId, true));
        //bodiesAppState.runCommand(new TremblingHandCommand(bodyId, true, true));
        bodiesAppState.runCommand(new SetSpeedDisplacemenetCommand(bodyId, 0.5f));

        SetCameraToBodyCommand camCommand = new SetCameraToBodyCommand("Patient");
        camCommand.setDistance(3);
        camCommand.setHeight(4);
        camCommand.setFront(true);
        bodiesAppState.runCommand(camCommand);

        devicesAppState = new DevicesAppState();
        stateManager.attach(devicesAppState);

        devicesAppState.runCommand(new CreateAccelerometerSensorCommand("sensor1"));
        devicesAppState.runCommand(new SetDeviceOnPartOfBodyCommand("Patient", "sensor1",
                SetDeviceOnPartOfBodyCommand.PartOfBody.Chest));

        devicesAppState.runCommand(new CreateAccelerometerSensorCommand("sensor2"));
        devicesAppState.runCommand(new SetDeviceOnPartOfBodyCommand("Patient", "sensor2",
                SetDeviceOnPartOfBodyCommand.PartOfBody.LeftWrist));

        stateManager.attach(new AbstractAppState() {
            PHATApplication app;

            @Override
            public void initialize(AppStateManager asm, Application aplctn) {
                app = (PHATApplication) aplctn;

            }
            boolean standUp = false;
            boolean washingHands = false;
            boolean havingShower = false;
            float cont = 0f;
            float timeToStandUp = 5f;
            boolean init = false;

            @Override
            public void update(float f) {
                if (!init) {
                    AccelerometerControl ac = devicesAppState.getDevice("sensor1").getControl(AccelerometerControl.class);
                    //ac.setMode(AccelerometerControl.AMode.ACCELEROMETER_MODE);
                    XYAccelerationsChart chart = new XYAccelerationsChart("Chart - Acc.", "Chest accelerations", "m/s2", "x,y,z");
                    ac.add(chart);
                    chart.showWindow();
                    
                    ac = devicesAppState.getDevice("sensor2").getControl(AccelerometerControl.class);
                    //ac.setMode(AccelerometerControl.AMode.ACCELEROMETER_MODE);
                    chart = new XYAccelerationsChart("Chart - Acc.", "LeftHand accelerations", "m/s2", "x,y,z");
                    ac.add(chart);
                    chart.showWindow();
                    
                    init = true;
                    bodiesAppState.runCommand(goToRandomRoom());
                }
            }
        });
    }

    @Override
    public void commandStateChanged(PHATCommand command) {
        if (command instanceof CreatePresenceSensorCommand) {
            CreatePresenceSensorCommand cpsc = (CreatePresenceSensorCommand) command;
            Node psNode = devicesAppState.getDevice(cpsc.getPresenceSensorId());
            if (psNode != null) {
                /*PHATPresenceSensor psControl = psNode.getControl(PHATPresenceSensor.class);
                 if (psControl != null) {
                 PresenceStatePanel psp1 = new PresenceStatePanel();
                 psControl.add(psp1);
                 sensorMonitor.getContentPane().add(psp1);
                 sensorMonitor.pack();
                 }*/
            }
        }
    }
    Random random = new Random();

    private GoToSpaceCommand goToRandomRoom() {
        List<String> roomNames = houseAppState.getHouse(houseId).getRoomNames();
        int size = roomNames.size();
        int index = random.nextInt(size);
        return new GoToSpaceCommand(bodyId, roomNames.get(index), new PHATCommandListener() {
            @Override
            public void commandStateChanged(PHATCommand command) {
                if (command.getState().equals(PHATCommand.State.Success)) {
                    bodiesAppState.runCommand(goToRandomRoom());
                    //bodiesAppState.runCommand(goToRandomRoom());
                } else if (command.getState().equals(PHATCommand.State.Fail)) {
                    bodiesAppState.runCommand(goToRandomRoom());
                }
            }
        });
    }
}