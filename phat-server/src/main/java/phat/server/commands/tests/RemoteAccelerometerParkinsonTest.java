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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.omg.CORBA.portable.InputStream;

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
import phat.devices.commands.CreateAccelerometerSensorCommand;
import phat.devices.commands.CreateSmartphoneCommand;
import phat.devices.commands.SetDeviceOnPartOfBodyCommand;
import phat.mobile.servicemanager.server.ServiceManagerServer;
import phat.mobile.servicemanager.services.Service;
import phat.sensors.accelerometer.AccelerationData;
import phat.sensors.accelerometer.AccelerometerControl;
import phat.sensors.accelerometer.XYAccelerationsChart;
import phat.server.PHATServerManager;
import phat.server.ServerAppState;
import phat.server.commands.ActivateAccelerometerServerCommand;
import phat.server.commands.DisplayAVDScreenCommand;
import phat.server.commands.PHATServerCommand;
import phat.server.commands.SetAndroidEmulatorCommand;
import phat.server.commands.StartActivityCommand;
import phat.structures.houses.TestHouse;
import phat.util.Debug;
import phat.util.SpatialFactory;
import phat.world.WorldAppState;
import sim.android.hardware.service.SimSensorEvent;

/**
 *
 * @author pablo
 */
public class RemoteAccelerometerParkinsonTest implements PHATInitAppListener {

	private static final Logger logger = Logger.getLogger(TestHouse.class.getName());
	BodiesAppState bodiesAppState;
	ServerAppState serverAppState;
	DevicesAppState devicesAppState;
	WorldAppState worldAppState;

	public static void main(String[] args) {
		RemoteAccelerometerParkinsonTest test = new RemoteAccelerometerParkinsonTest();
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



		devicesAppState.runCommand(new CreateAccelerometerSensorCommand("sensor1"));
		devicesAppState.runCommand(new SetDeviceOnPartOfBodyCommand("Patient", "sensor1",
				SetDeviceOnPartOfBodyCommand.PartOfBody.Chest));

		devicesAppState.runCommand(new CreateAccelerometerSensorCommand("sensor2"));
		devicesAppState.runCommand(new SetDeviceOnPartOfBodyCommand("Patient", "sensor2",
				SetDeviceOnPartOfBodyCommand.PartOfBody.LeftHand));

		serverAppState = new ServerAppState();
		stateManager.attach(serverAppState);

		serverAppState.runCommand(new ActivateAccelerometerServerCommand("PatientBodyAccel","sensor1"));
		serverAppState.runCommand(new ActivateAccelerometerServerCommand("PatientBodyAccel","sensor2"));

		launchRemoteXYChart("Chest Remote",PHATServerManager.getAddress(),"sensor1");
		launchRemoteXYChart("RightHand Remote",PHATServerManager.getAddress(),"sensor2");

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
					AccelerometerControl ac = devicesAppState.getDevice("sensor1").getControl(AccelerometerControl.class);
					ac.setMode(AccelerometerControl.AMode.ACCELEROMETER_MODE);
					XYAccelerationsChart chart = new XYAccelerationsChart("Chart - Acc.", "Local accelerations", "m/s2", "x,y,z");
					ac.add(chart);
					chart.showWindow();
					init = true;

				}
				/*cont += f;
				if (cont > timeToFall && cont < timeToFall + 1 && !fall) {
					bodiesAppState.runCommand(new FallDownCommand("Patient"));
					fall = true;
				} else if (fall && cont > timeToFall + 6) {
					PHATCommand standUp = new StandUpCommand("Patient");
					bodiesAppState.runCommand(standUp);
					fall = false;
					cont = 0;
				}*/
			}
		});
	}

	public static void launchRemoteXYChart(final String title, final InetAddress host, final String sensor) {

		new Thread(){
			public void run(){
				Socket s;
				try {
					final XYAccelerationsChart chart = new XYAccelerationsChart(title, "Remote "+sensor+":"+title+" accelerations", "m/s2", "x,y,z");
					chart.showWindow();

					Service sensorService=null;

					for (int k=0;k<5 && sensorService==null;k++) 
					{
						sensorService=phat.mobile.servicemanager.client.RemoteSocketClient.getService(host,PHATServerManager.getPort(), sensor);
						try {
							Thread.currentThread().sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

					s = phat.mobile.servicemanager.client.RemoteSocketClient.createSocket(PHATServerManager.getAddress(), 
							phat.mobile.servicemanager.client.RemoteSocketClient.getService(PHATServerManager.getAddress(),
									PHATServerManager.getPort(), sensor).getPort(), 
							10,1000);
					if (s==null) throw new RuntimeException("Could not connect to host "+PHATServerManager.getAddress()+" at port "+ PHATServerManager.getPort());

					BufferedReader is=new BufferedReader(new InputStreamReader(s.getInputStream()));

					String objRead=null;
					Long lastRead=new Date().getTime();
					do {

						objRead=is.readLine();
						final long interval=new Date().getTime()-lastRead;
						lastRead=new Date().getTime();								
						if (objRead!=null && !objRead.isEmpty()){

							SimSensorEvent sse=SimSensorEvent.fromString(objRead);
							if (sse!=null){
								final float x=sse.getValues()[0];
								final float y=sse.getValues()[1];
								final float z=sse.getValues()[2];
								SwingUtilities.invokeLater(new Runnable(){
									public void run(){											
										AccelerationData ad = new AccelerationData(
												interval,x,y,z);
										chart.update(null,ad);
										chart.repaint();
									}
								});
							}
						};
					} while (objRead!=null);
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();

	}

}

