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
package phat;

import com.aurellem.capture.AurellemSystemDelegate;
import com.aurellem.capture.IsoTimer;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;

import javax.swing.JFrame;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppState;
import com.jme3.bullet.BulletAppState;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.control.CameraControl;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;
import com.jme3.system.JmeSystem;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.File;

import phat.agents.AgentsAppState;
import phat.app.PHATApplication;
import phat.app.PHATFinalizeAppListener;
import phat.app.PHATInitAppListener;
import phat.audio.MultiAudioAppState;
import phat.audio.PHATSystemDelegate;
import phat.audio.SingleAudioAppState;
import phat.body.BodiesAppState;
import phat.config.DeviceConfigurator;
import phat.config.ServerConfigurator;
import phat.config.impl.AgentConfiguratorImpl;
import phat.config.impl.AudioConfiguratorImpl;
import phat.config.impl.BodyConfiguratorImpl;
import phat.config.impl.DeviceConfiguratorImpl;
import phat.config.impl.HouseConfiguratorImpl;
import phat.config.impl.ServerConfiguratorImpl;
import phat.config.impl.WorldConfiguratorImpl;
import phat.devices.DevicesAppState;
import phat.server.ServerAppState;
import phat.structures.houses.HouseAppState;
import phat.util.PHATUtils;
import phat.util.video.RecordVideoCommand;
import phat.world.PHATCalendar;
import phat.world.WorldAppState;

/**
 *
 * @author sala26
 */
public class PHATInterface implements PHATInitAppListener, PHATFinalizeAppListener {

	public static final String SERVER_NAME = "PHATInterface";
	PHATApplication app;
	PHATInitializer initializer;
	BulletAppState bulletAppState;
	AudioConfiguratorImpl audioConfig;
	WorldConfiguratorImpl worldConfig;
	HouseConfiguratorImpl houseConfig;
	BodyConfiguratorImpl bodyConfig;
	DeviceConfiguratorImpl deviceConfig;
	ServerConfiguratorImpl serverConfig;
	AgentConfiguratorImpl agentConfig;
	boolean paused;
	long seed;
	String tittle = "PHAT";
	Random random;
	private Registry registry;
	PHATCalendar initSimTime = null;
	boolean multiListener = false;
	private int displayWidth = 480;
	private int displayHeight = 800;
	private boolean recordVideo = false;
	private JFrame rootFrame;

	public PHATInterface(PHATInitializer initializer) {
		this.initializer = initializer;
	}
	
	public JFrame getRootJFrame() {
		return rootFrame;
	}

	public PHATInterface(PHATInitializer initializer, ArgumentProcessor ap) {
		this(initializer);

		ap.initialize(this);
	}

	public void startServer(String name) throws RemoteException, AlreadyBoundException, NotBoundException {
		final java.util.concurrent.ConcurrentLinkedQueue<String> commands = new java.util.concurrent.ConcurrentLinkedQueue<String>();
		RemotePHATInterface rmi = new RemotePHATInterface() {
			@Override
			public void resumePHAT() throws RemoteException {
				commands.add("resume");
				// guimainMenu.resume(); // resume cannot be
				// executed in an appstate because appstate execution is frozen
				// if a pause has been issued before
			}

			@Override
			public void pausePHAT() throws RemoteException {
				commands.add("pause");

			}

			@Override
			public PHATCalendar getSimTime() throws RemoteException {
				// TODO Auto-generated method stub
				return PHATInterface.this.getSimTime();
			}

			@Override
			public long getElapsedSimTimeSeconds() throws RemoteException {
				// TODO Auto-generated method stub
				return PHATInterface.this.getElapsedSimTimeSeconds();
			}
		};

		AbstractAppState absApp = new AbstractAppState() {
			@Override
			public void update(float tpf) {
				super.update(tpf);
				if (!commands.isEmpty()) {
					String command = commands.poll();

					/*
					 * if (command.equalsIgnoreCase("pause")) { guimainMenu.pause(); }
					 */
				}

			}
		};

		app.getStateManager().attach(absApp);

		int port = 60200;
		System.setProperty("java.rmi.server.useCodebaseOnly", "false");
		if (System.getProperty("phat.monitorport") != null) {
			port = Integer.parseInt(System.getProperty("phat.monitorport"));
		}
		if (registry == null) {
			try {

				registry = LocateRegistry.getRegistry(port);
				registry.list();// to force the connection and ensure there is something at the other side
				// getRegistry is not failing when resolving the registry

			} catch (Exception e) {
				registry = java.rmi.registry.LocateRegistry.createRegistry(port); // Creates and exports a Registry
																					// instance

			}
		}
		try {
			Thread.currentThread().sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		RemotePHATInterface stub = (RemotePHATInterface) UnicastRemoteObject.exportObject(rmi, 0); // Exports remote
																									// object
		registry.bind(name, stub); // Binds a remote reference
		registry.lookup(name);

	}

	public void start() {
		PHATUtils.removeNativeFiles();
		app = new PHATApplication(this);

		AppSettings s = new AppSettings(true);
		s.setTitle(initializer.getTittle());
		s.setWidth(displayWidth);
		s.setHeight(displayHeight);
		if (multiListener) {
			s.setAudioRenderer(AurellemSystemDelegate.SEND);
			JmeSystem.setSystemDelegate(new PHATSystemDelegate());
			app.setTimer(new IsoTimer(60f));
			org.lwjgl.input.Mouse.setGrabbed(false);
		} else {
			s.setAudioRenderer("LWJGL");
			JmeSystem.setSystemDelegate(new PHATSystemDelegate());
		}
		app.setSettings(s);

		/*
		 * AppSettings s = new AppSettings(true);
		 * s.setAudioRenderer(AurellemSystemDelegate.SEND);
		 * JmeSystem.setSystemDelegate(new AurellemSystemDelegate());
		 * app.setSettings(s);
		 */

		/*
		 * AppSettings s = new AppSettings(true); s.setAudioRenderer("LWJGL");
		 * JmeSystem.setSystemDelegate(new AurellemSystemDelegate());
		 * app.setSettings(s);
		 */

		app.createCanvas();

		JmeCanvasContext ctx = (JmeCanvasContext) app.getContext();

		ctx.setSystemListener(app);

		rootFrame = new JFrame("PHATSIM");
		Dimension dim = new Dimension(displayWidth, displayHeight);
		ctx.getCanvas().setPreferredSize(dim);
		rootFrame.getContentPane().setLayout(new FlowLayout());
		rootFrame.getContentPane().add(ctx.getCanvas());
		rootFrame.pack();
		rootFrame.setVisible(true);

		app.startCanvas();

		// app.start();
		/*
		 * try { startServer(PHATInterface.SERVER_NAME); } catch (RemoteException |
		 * AlreadyBoundException | NotBoundException e) { // TODO Auto-generated catch
		 * block e.printStackTrace(); System.exit(-1); }
		 */
	}

	@Override
	public void init(SimpleApplication app) {

		app.getFlyByCamera().setMoveSpeed(10f);
		app.getFlyByCamera().setDragToRotate(true);

		app.getCamera().setFrustumPerspective(45f, (float) app.getCamera().getWidth() / app.getCamera().getHeight(),
				0.1f, 1000f);

		app.getCamera().setLocation(new Vector3f(6.2354145f, 18.598438f, 4.6557f));
		app.getCamera().setRotation(new Quaternion(0.5041053f, -0.49580166f, 0.5068195f, 0.4931456f));

		// Debug.enableDebugGrid(20, app.getAssetManager(), app.getRootNode());
		// bulletAppState = new BulletAppState();
		// app.getStateManager().attach(bulletAppState);
		// bulletAppState.getPhysicsSpace().setAccuracy(1f/200f);
		// bulletAppState.setDebugEnabled(true);

		if (multiListener) {
			audioConfig = new AudioConfiguratorImpl(new MultiAudioAppState());
		} else {
			audioConfig = new AudioConfiguratorImpl(new SingleAudioAppState());
		}

		// audioConfig.setMultiAudioRenderer(false, app);
		Node camFollower = new Node("CamNode");
		// means that the Camera's transform is "copied" to the Transform of the
		// Spatial.
		CameraControl cc = new CameraControl(app.getCamera(), CameraControl.ControlDirection.CameraToSpatial);
		camFollower.addControl(cc);
		app.getRootNode().attachChild(camFollower);
		audioConfig.getAudioAppState().setPCSpeakerTo(camFollower);
		app.getStateManager().attach(audioConfig.getAudioAppState());

		worldConfig = new WorldConfiguratorImpl(new WorldAppState());
		app.getStateManager().attach(worldConfig.getWorldAppState());

		houseConfig = new HouseConfiguratorImpl(new HouseAppState());
		app.getStateManager().attach(houseConfig.getHousedAppState());

		bodyConfig = new BodyConfiguratorImpl(new BodiesAppState());
		app.getStateManager().attach(bodyConfig.getBodiesAppState());

		deviceConfig = new DeviceConfiguratorImpl(new DevicesAppState());
		app.getStateManager().attach(deviceConfig.getDevicesAppState());

		serverConfig = new ServerConfiguratorImpl(new ServerAppState());
		app.getStateManager().attach(serverConfig.getServerAppState());

		agentConfig = new AgentConfiguratorImpl(new AgentsAppState(this));
		agentConfig.getAgentsAppState().setBodiesAppState(bodyConfig.getBodiesAppState());
		app.getStateManager().attach(agentConfig.getAgentsAppState());

		// app.getStateManager().attach(initAppState);
		initializer.initWorld(worldConfig);
		initializer.initHouse(houseConfig);
		initializer.initBodies(bodyConfig);
		initializer.initDevices(deviceConfig);
		initializer.initServer(serverConfig);
		initializer.initAgents(agentConfig);

		random = new Random(seed);

		this.initSimTime = new PHATCalendar(getSimTime());

		if (recordVideo) {
			HouseAppState has = app.getStateManager().getState(HouseAppState.class);
			
			has.runCommand(new RecordVideoCommand(new File(initializer.getTittle() + ".mp4")));
			
		}
	}

	public void setSimSpeed(float speed) {
		if (speed > 0) {
			app.setSimSpeed(speed);
			if (paused) {
				resumePHAT();
			}
		} else if (!paused) {
			pausePHAT();
		}
	}

	public void pausePHAT() {
		paused = true;

		pauseAppState(BulletAppState.class);
		pauseAppState(AgentsAppState.class);
		pauseAppState(DevicesAppState.class);
		pauseAppState(ServerAppState.class);
		pauseAppState(BodiesAppState.class);
		pauseAppState(HouseAppState.class);
		pauseAppState(WorldAppState.class);
	}

	private <T extends AppState> void pauseAppState(Class<T> appStateClass) {
		T appState = app.getStateManager().getState(appStateClass);
		if (appState != null) {
			app.getStateManager().detach(appState);
		}
	}

	public void resumePHAT() {
		paused = false;

		app.getStateManager().attach(bulletAppState);
		app.getStateManager().attach(agentConfig.getAgentsAppState());
		app.getStateManager().attach(serverConfig.getServerAppState());
		app.getStateManager().attach(deviceConfig.getDevicesAppState());
		app.getStateManager().attach(bodyConfig.getBodiesAppState());
		app.getStateManager().attach(houseConfig.getHousedAppState());
		app.getStateManager().attach(worldConfig.getWorldAppState());
	}

	public float getSimSpeed() {
		return app.getSimSpeed();
	}

	@Override
	public void finalize(SimpleApplication app) {}

	public Random getRandom() {
		return random;
	}

	public synchronized PHATCalendar getSimTime() {
		return worldConfig.getWorldAppState().getCalendar();
	}

	public synchronized long getElapsedSimTimeSeconds() {
		if (initSimTime == null) {
			return 0;
		}
		return initSimTime.spentTimeTo(getSimTime());
	}

	public long getSeed() {
		return seed;
	}

	public void setSeed(long seed) {
		this.seed = seed;
	}

	public String getTittle() {
		return tittle;
	}

	public void setTittle(String tittle) {
		this.tittle = tittle;
	}

	public DeviceConfigurator getDevicesConfig() {
		return deviceConfig;
	}

	public ServerConfigurator getServerConfig() {
		return serverConfig;
	}

	public boolean isMultiListener() {
		return multiListener;
	}

	public void setMultiListener(boolean multiListener) {
		this.multiListener = multiListener;
	}

	public int getDisplayWidth() {
		return displayWidth;
	}

	public void setDisplayWidth(int displayWidth) {
		this.displayWidth = displayWidth;
	}

	public int getDisplayHeight() {
		return displayHeight;
	}

	public void setDisplayHeight(int displayHeight) {
		this.displayHeight = displayHeight;
	}

	public void setRecordVideo(boolean recordVideo) {
		this.recordVideo = recordVideo;
	}

	public String getSimTitle() {
		return this.initializer.getTittle();
	}

	public String getSimDescription() {
		return this.initializer.getDescription();
	}
}
