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
package phat.body.commands.tests;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import java.util.logging.Logger;

import phat.app.PHATApplication;
import phat.app.PHATInitAppListener;
import phat.body.BodiesAppState;
import phat.body.commands.RandomWalkingCommand;
import phat.body.commands.SetBodyInCoordenatesCommand;
import phat.structures.houses.TestHouse;
import phat.util.SpatialFactory;
import phat.world.WorldAppState;

/**
 *
 * @author pablo
 */
public class CreateBodyCommandTest implements PHATInitAppListener {

    private static final Logger logger = Logger.getLogger(TestHouse.class.getName());    
    BodiesAppState bodiesAppState;
    WorldAppState worldAppState;

    public static void main(String[] args) {
        CreateBodyCommandTest test = new CreateBodyCommandTest();
        PHATApplication phat = new PHATApplication(test);
        phat.setDisplayFps(true);
        phat.setDisplayStatView(false);
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

        BulletAppState bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        //bulletAppState.setDebugEnabled(true);

        worldAppState = new WorldAppState();
        app.getStateManager().attach(worldAppState);
        worldAppState.setCalendar(2013, 1, 1, 12, 0, 0);
        worldAppState.setLandType(WorldAppState.LandType.Basic);

        bodiesAppState = new BodiesAppState();
        stateManager.attach(bodiesAppState);
        
        int bodyNum = 50;
        float step = 0.7f;
        float offset = -step*bodyNum/2f;
        
        for(int i = 0; i < bodyNum; i++) {
            String bodyId = "Body-"+i;
            bodiesAppState.createBody(BodiesAppState.BodyType.ElderLP, bodyId);
            bodiesAppState.runCommand(new SetBodyInCoordenatesCommand(bodyId, Vector3f.ZERO.add(offset+=0.7f, 0f, 0f)));
            bodiesAppState.runCommand(new RandomWalkingCommand(bodyId, true));
            //bodiesAppState.runCommand(new DebugSkeletonCommand(bodyId, Boolean.TRUE));
        }
        
        
        
        //bodiesAppState.runCommand(new SetPCListenerToBodyCommand("Patient"));

        /*devicesAppState = new DevicesAppState();
        stateManager.attach(devicesAppState);
        
        devicesAppState.runCommand(new CreateSmartphoneCommand("Smartphone1", "emulator-5554"));
        devicesAppState.runCommand(new SetDeviceOnPartOfBodyCommand("Patient", "Smartphone1", 
                SetDeviceOnPartOfBodyCommand.PartOfBody.LeftHand));
        
        devicesAppState.runCommand(new CreateSmartphoneCommand("Smartphone2", "emulator-5554"));
        devicesAppState.runCommand(new SetDeviceOnPartOfBodyCommand("Patient", "Smartphone2", 
                SetDeviceOnPartOfBodyCommand.PartOfBody.RightHand));
        
        devicesAppState.runCommand(new CreateSmartphoneCommand("Smartphone3", "emulator-5554"));
        devicesAppState.runCommand(new SetDeviceOnPartOfBodyCommand("Patient", "Smartphone3", 
                SetDeviceOnPartOfBodyCommand.PartOfBody.RightWrist));
        
        devicesAppState.runCommand(new CreateSmartphoneCommand("Smartphone4", "emulator-5554"));
        devicesAppState.runCommand(new SetDeviceOnPartOfBodyCommand("Patient", "Smartphone4", 
                SetDeviceOnPartOfBodyCommand.PartOfBody.LeftWrist));
        
        devicesAppState.runCommand(new CreateSmartphoneCommand("Smartphone5", "emulator-5554"));
        devicesAppState.runCommand(new SetDeviceOnPartOfBodyCommand("Patient", "Smartphone5", 
                SetDeviceOnPartOfBodyCommand.PartOfBody.RightWrist));
        
        devicesAppState.runCommand(new CreateSmartphoneCommand("Smartphone6", "emulator-5554"));
        devicesAppState.runCommand(new SetDeviceOnPartOfBodyCommand("Patient", "Smartphone6", 
                SetDeviceOnPartOfBodyCommand.PartOfBody.RightUnkle));
        
        devicesAppState.runCommand(new CreateSmartphoneCommand("Smartphone7", "emulator-5554"));
        devicesAppState.runCommand(new SetDeviceOnPartOfBodyCommand("Patient", "Smartphone7", 
                SetDeviceOnPartOfBodyCommand.PartOfBody.LeftUnkle));*/
    }
}