/*
 * Copyright (C) 2014 Pablo Campillo-Sanchez <pabcampi@ucm.es> and Jorge Gomez Sanz
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
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.system.AppSettings;

import java.util.logging.Logger;
import phat.app.PHATApplication;
import phat.app.PHATInitAppListener;
import phat.body.BodiesAppState;
import phat.body.commands.RandomWalkingCommand;
import phat.body.commands.SetBodyInCoordenatesCommand;
import phat.body.commands.SetCameraToBodyCommand;
import phat.body.commands.SetPCListenerToBodyCommand;
import phat.body.commands.SetSpeedDisplacemenetCommand;
import phat.body.commands.SetStoopedBodyCommand;
import phat.devices.DevicesAppState;
import phat.devices.commands.CreateSmartphoneCommand;
import phat.devices.commands.SetDeviceOnPartOfBodyCommand;
import phat.server.ServerAppState;
import phat.server.commands.ActivateSpeakerServerCommand;
import phat.structures.houses.TestHouse;
import phat.util.Debug;
import phat.util.SpatialFactory;
import phat.world.WorldAppState;

/**
 *
 * @author pablo
 */
public class SpeakerTest implements PHATInitAppListener {

	private static final Logger logger = Logger.getLogger(TestHouse.class.getName());
	BodiesAppState bodiesAppState;
	ServerAppState serverAppState;
	DevicesAppState devicesAppState;
	WorldAppState worldAppState;

	public static void main(String[] args) {
		SpeakerTest test = new SpeakerTest();
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

		/*BulletAppState bulletAppState = new BulletAppState();
		bulletAppState.setThreadingType(BulletAppState.ThreadingType.PARALLEL);
		stateManager.attach(bulletAppState);
		bulletAppState.getPhysicsSpace().setAccuracy(1 / 60f);*/
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
                bodiesAppState.runCommand(new SetPCListenerToBodyCommand("Patient"));
		//bodiesAppState.runCommand(new TremblingHandCommand("Patient", true, true));
		//TremblingHeadCommand thc = new TremblingHeadCommand("Patient", true);
		//thc.setAngular(FastMath.HALF_PI);
		//bodiesAppState.runCommand(thc);

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

                devicesAppState.runCommand(new CreateSmartphoneCommand("Smartphone1"));
		devicesAppState.runCommand(new SetDeviceOnPartOfBodyCommand("Patient", "Smartphone1",
				SetDeviceOnPartOfBodyCommand.PartOfBody.Chest));

		serverAppState = new ServerAppState();
		stateManager.attach(serverAppState);

                serverAppState.runCommand(new ActivateSpeakerServerCommand("Smartphone1", "Speaker"));
                
	}

}

