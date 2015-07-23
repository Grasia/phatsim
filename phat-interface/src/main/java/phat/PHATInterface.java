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

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeSystem;

import phat.agents.AgentsAppState;
import phat.app.PHATApplication;
import phat.app.PHATFinalizeAppListener;
import phat.app.PHATInitAppListener;
import phat.audio.AudioAppState;
import phat.body.BodiesAppState;
import phat.config.DeviceConfigurator;
import phat.config.impl.AgentConfiguratorImpl;
import phat.config.impl.AudioConfiguratorImpl;
import phat.config.impl.BodyConfiguratorImpl;
import phat.config.impl.DeviceConfiguratorImpl;
import phat.config.impl.HouseConfiguratorImpl;
import phat.config.impl.WorldConfiguratorImpl;
import phat.devices.DevicesAppState;
import phat.gui.GUIMainMenuAppState;
import phat.gui.logging.LoggingViewerAppState;
import phat.structures.houses.HouseAppState;
import phat.util.Debug;
import phat.world.MonitorEventQueue;
import phat.world.PHATCalendar;
import phat.world.WorldAppState;
import tonegod.gui.core.Screen;

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
	AgentConfiguratorImpl agentConfig;
	boolean paused;
	long seed;
	String tittle = "PHAT";
	Random random;
	private Registry registry;
	private GUIMainMenuAppState guimainMenu;
	PHATCalendar initSimTime=null;


	public PHATInterface(PHATInitializer initializer) {
		this.initializer = initializer;
	}




	public void startServer(String name) throws RemoteException, AlreadyBoundException, NotBoundException{


		
		
		final java.util.concurrent.ConcurrentLinkedQueue<String> commands=new java.util.concurrent.ConcurrentLinkedQueue<String>();
		RemotePHATInterface rmi=new RemotePHATInterface(){

			@Override
			public void resumePHAT() throws RemoteException {
				commands.add("resume");		
				guimainMenu.resume(); // resume cannot be 
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
		
		AbstractAppState absApp=new AbstractAppState(){

			@Override
			public void update(float tpf) {
				super.update(tpf);
				if (!commands.isEmpty()){
					String command=commands.poll();
					
					if (command.equalsIgnoreCase("pause"))
						guimainMenu.pause();
				}
				
			}
			
		};
		
		app.getStateManager().attach(absApp);

	

		int port=60200; 
		System.setProperty("java.rmi.server.useCodebaseOnly","false");
		if (System.getProperty("phat.monitorport")!=null){
			port=Integer.parseInt(System.getProperty("phat.monitorport"));
		}
		if (registry==null){
			try {

				registry = LocateRegistry.getRegistry(port);
				registry.list();// to force the connection and ensure there is something at the other side
				// getRegistry is not failing when resolving the registry

			} catch (Exception  e) {
				registry = java.rmi.registry.LocateRegistry.createRegistry(port); // Creates and exports a Registry instance	

			}		
		}
		try {
			Thread.currentThread().sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		RemotePHATInterface stub = (RemotePHATInterface) UnicastRemoteObject.exportObject(rmi, 0); // Exports remote object		
		registry.bind(name, stub); // Binds a remote reference
		registry.lookup(name);

	}
	public void start() {
		app = new PHATApplication(this);

		AppSettings s = new AppSettings(true);
		s.setTitle(initializer.getTittle());
		s.setWidth(480);
		s.setHeight(800);
		app.setDisplayStatView(false);
		app.setSettings(s);

		/*AppSettings s = new AppSettings(true);
         s.setAudioRenderer(AurellemSystemDelegate.SEND);
         JmeSystem.setSystemDelegate(new AurellemSystemDelegate());
         app.setSettings(s);*/

		/*AppSettings s = new AppSettings(true);
        s.setAudioRenderer("LWJGL");
        JmeSystem.setSystemDelegate(new AurellemSystemDelegate());
        app.setSettings(s);*/

		app.start();
		try {
			startServer(PHATInterface.SERVER_NAME);
		} catch (RemoteException | AlreadyBoundException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		}
	}

	@Override
	public void init(SimpleApplication app) {

		app.getFlyByCamera().setMoveSpeed(10f);
		app.getFlyByCamera().setDragToRotate(true);

		app.getCamera().setFrustumPerspective(45f, 
				(float) app.getCamera().getWidth() / app.getCamera().getHeight(), 
				0.1f, 1000f);

		app.getCamera().setLocation(new Vector3f(6.2354145f, 18.598438f, 4.6557f));
		app.getCamera().setRotation(new Quaternion(0.5041053f, -0.49580166f, 0.5068195f, 0.4931456f));

		//Debug.enableDebugGrid(20, app.getAssetManager(), app.getRootNode());
		//bulletAppState = new BulletAppState();        
		//app.getStateManager().attach(bulletAppState);
		//bulletAppState.getPhysicsSpace().setAccuracy(1f/200f);
		//bulletAppState.setDebugEnabled(true);

		Screen screen = new Screen(app, "tonegod/gui/style/def/style_map.gui.xml");
		app.getGuiNode().addControl(screen);
		guimainMenu=new GUIMainMenuAppState(screen);
		app.getStateManager().attach(guimainMenu);

		audioConfig = new AudioConfiguratorImpl(new AudioAppState());
		audioConfig.setMultiAudioRenderer(false, app);
		app.getStateManager().attach(audioConfig.getAudioAppState());

		worldConfig = new WorldConfiguratorImpl(new WorldAppState());
		app.getStateManager().attach(worldConfig.getWorldAppState());

		houseConfig = new HouseConfiguratorImpl(new HouseAppState());
		app.getStateManager().attach(houseConfig.getHousedAppState());

		bodyConfig = new BodyConfiguratorImpl(new BodiesAppState());
		app.getStateManager().attach(bodyConfig.getBodiesAppState());

		deviceConfig = new DeviceConfiguratorImpl(new DevicesAppState());
		app.getStateManager().attach(deviceConfig.getDevicesAppState());

		agentConfig = new AgentConfiguratorImpl(new AgentsAppState(this));
		agentConfig.getAgentsAppState().setBodiesAppState(bodyConfig.getBodiesAppState());
		app.getStateManager().attach(agentConfig.getAgentsAppState());

		app.getStateManager().attach(new LoggingViewerAppState());

		//app.getStateManager().attach(initAppState);
		initializer.initWorld(worldConfig);
		initializer.initHouse(houseConfig);
		initializer.initBodies(bodyConfig);
		initializer.initDevices(deviceConfig);
		initializer.initAgents(agentConfig);

		random = new Random(seed);
		
		this.initSimTime=new PHATCalendar(getSimTime());
	}

	public void setSimSpeed(float speed) {
		if(speed > 0) {
			app.setSimSpeed(speed);
			if(paused) {
				resumePHAT();
			}
		} else if(!paused) {
			pausePHAT();
		}
	}

	public void pausePHAT(){
		paused = true;
		
		pauseAppState(BulletAppState.class);
		pauseAppState(AgentsAppState.class);
		pauseAppState(DevicesAppState.class);
		pauseAppState(BodiesAppState.class);
		pauseAppState(HouseAppState.class);
		pauseAppState(WorldAppState.class);
	}

	private <T extends AppState> void pauseAppState(Class<T> appStateClass) {
		T appState = app.getStateManager().getState(appStateClass);
		if(appState != null) {
			app.getStateManager().detach(appState);
		}
	}

	public void resumePHAT() {
		paused = false;
		
		app.getStateManager().attach(bulletAppState);
		app.getStateManager().attach(agentConfig.getAgentsAppState());
		app.getStateManager().attach(deviceConfig.getDevicesAppState());
		app.getStateManager().attach(bodyConfig.getBodiesAppState());
		app.getStateManager().attach(houseConfig.getHousedAppState());
		app.getStateManager().attach(worldConfig.getWorldAppState());
	}

	public float getSimSpeed() {
		return app.getSimSpeed();
	}

	@Override
	public void finalize(SimpleApplication app) {

	}

	public Random getRandom() {
		return random;
	}

	public synchronized PHATCalendar getSimTime() {
		return worldConfig.getWorldAppState().getCalendar();
	}
	
	public synchronized long getElapsedSimTimeSeconds() {
		if (initSimTime==null)
			return 0;
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
}