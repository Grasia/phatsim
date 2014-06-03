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
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import java.util.logging.Logger;
import phat.app.PHATApplication;
import phat.app.PHATInitAppListener;
import phat.body.BodiesAppState;
import phat.body.commands.FallDownCommand;
import phat.body.commands.RandomWalkingCommand;
import phat.body.commands.SetBodyInCoordenatesCommand;
import phat.body.commands.SetCameraToBodyCommand;
import phat.body.commands.SetRigidArmCommand;
import phat.body.commands.SetSpeedDisplacemenetCommand;
import phat.body.commands.SetStoopedBodyCommand;
import phat.body.commands.StandUpCommand;
import phat.body.commands.TremblingHandCommand;
import phat.body.commands.TremblingHeadCommand;
import phat.body.commands.TripOverCommand;
import phat.commands.PHATCommand;
import phat.devices.DevicesAppState;
import phat.devices.commands.CreateSmartphoneCommand;
import phat.devices.commands.DisplayAVDScreenCommand;
import phat.devices.commands.SetAndroidEmulatorCommand;
import phat.devices.commands.SetDeviceOnPartOfBodyCommand;
import phat.devices.commands.StartActivityCommand;
import phat.sensors.accelerometer.AccelerometerControl;
import phat.sensors.accelerometer.XYAccelerationsChart;
import phat.structures.houses.TestHouse;
import phat.util.Debug;
import phat.util.SpatialFactory;
import phat.world.WorldAppState;

/**
 *
 * @author pablo
 */
public class AccelerometerParkinsonTest implements PHATInitAppListener {

    private static final Logger logger = Logger.getLogger(TestHouse.class.getName());
    BodiesAppState bodiesAppState;
    DevicesAppState devicesAppState;
    WorldAppState worldAppState;

    public static void main(String[] args) {
        AccelerometerParkinsonTest test = new AccelerometerParkinsonTest();
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

        Debug.enableDebugGrid(10, app.getAssetManager(), app.getRootNode());
        bodiesAppState = new BodiesAppState();
        stateManager.attach(bodiesAppState);

        bodiesAppState.createBody(BodiesAppState.BodyType.ElderLP, "Patient");
        bodiesAppState.runCommand(new SetBodyInCoordenatesCommand("Patient", Vector3f.ZERO));
        bodiesAppState.runCommand(new RandomWalkingCommand("Patient", true));
        bodiesAppState.runCommand(new TremblingHandCommand("Patient", true, true));
        TremblingHeadCommand thc = new TremblingHeadCommand("Patient", true);
        thc.setAngular(FastMath.HALF_PI);
        bodiesAppState.runCommand(thc);
        
        bodiesAppState.runCommand(new SetSpeedDisplacemenetCommand("Patient", 0.5f));        
        //bodiesAppState.runCommand(new SetRigidArmCommand("Patient", true, true));
        //bodiesAppState.runCommand(new SetRigidArmCommand("Patient", true, false));
        bodiesAppState.runCommand(new SetStoopedBodyCommand("Patient", true));
        
        SetCameraToBodyCommand camCommand = new SetCameraToBodyCommand("Patient");
        camCommand.setDistance(3);
        camCommand.setFront(true);
        bodiesAppState.runCommand(camCommand);

        devicesAppState = new DevicesAppState();
        stateManager.attach(devicesAppState);

        /*Quaternion rot90 = new Quaternion();
         float[] angles = new float[]{98 * FastMath.DEG_TO_RAD, 149 * FastMath.DEG_TO_RAD, 5 * FastMath.DEG_TO_RAD};
         //float [] angles = new float[]{0f, 90* FastMath.DEG_TO_RAD, 0f};
         rot90.fromAngles(angles);
         Vector3f acc = new Vector3f(0f, 9.8f, 0f);
         rot90.mult(acc, acc);
         System.out.println("Acc = "+acc);*/

        devicesAppState.runCommand(new CreateSmartphoneCommand("Smartphone1"));
        //devicesAppState.runCommand(new SetDeviceInCoordenatesCommand("Smartphone1", Vector3f.UNIT_Y));
        devicesAppState.runCommand(new SetDeviceOnPartOfBodyCommand("Patient", "Smartphone1",
                SetDeviceOnPartOfBodyCommand.PartOfBody.Chest));
        
        devicesAppState.runCommand(new SetAndroidEmulatorCommand("Smartphone1", "Smartphone1", "emulator-5554"));
        devicesAppState.runCommand(new StartActivityCommand("Smartphone1", "phat.android.apps", "BodyPositionMonitoring"));
        
        DisplayAVDScreenCommand displayCommand = new DisplayAVDScreenCommand("Smartphone1", "Smartphone1");
        displayCommand.setFrecuency(0.5f);
        devicesAppState.runCommand(displayCommand);

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
            boolean fall = false;
            float timeToFall = 10f;
            boolean init = false;

            @Override
            public void update(float f) {
                if (!init) {
                    AccelerometerControl ac = devicesAppState.getDevice("Smartphone1").getControl(AccelerometerControl.class);
                    ac.setMode(AccelerometerControl.AMode.GRAVITY_MODE);
                    XYAccelerationsChart chart = new XYAccelerationsChart("Chart - Acc.", "Smartphone1 accelerations", "m/s2", "x,y,z");
                    ac.add(chart);
                    chart.showWindow();
                    init = true;
                    
                }
                cont += f;
                if (cont > timeToFall && cont < timeToFall + 1 && !fall) {
                    bodiesAppState.runCommand(new FallDownCommand("Patient"));
                    fall = true;
                } else if (fall && cont > timeToFall + 6) {
                    PHATCommand standUp = new StandUpCommand("Patient");
                    bodiesAppState.runCommand(standUp);
                    fall = false;
                    cont = 0;
                }
            }
        });
    }
}