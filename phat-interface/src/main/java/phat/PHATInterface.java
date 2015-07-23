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

import java.util.Random;

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
import phat.config.ServerConfigurator;
import phat.config.impl.AgentConfiguratorImpl;
import phat.config.impl.AudioConfiguratorImpl;
import phat.config.impl.BodyConfiguratorImpl;
import phat.config.impl.DeviceConfiguratorImpl;
import phat.config.impl.HouseConfiguratorImpl;
import phat.config.impl.ServerConfiguratorImpl;
import phat.config.impl.WorldConfiguratorImpl;
import phat.devices.DevicesAppState;
import phat.gui.GUIMainMenuAppState;
import phat.gui.logging.LoggingViewerAppState;
import phat.server.ServerAppState;
import phat.structures.houses.HouseAppState;
import phat.util.Debug;
import phat.world.PHATCalendar;
import phat.world.WorldAppState;
import tonegod.gui.core.Screen;

/**
 *
 * @author sala26
 */
public class PHATInterface implements PHATInitAppListener, PHATFinalizeAppListener {

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
    


    public PHATInterface(PHATInitializer initializer) {
        this.initializer = initializer;
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
        app.getStateManager().attach(new GUIMainMenuAppState(screen));

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
        
        serverConfig = new ServerConfiguratorImpl(new ServerAppState());
        app.getStateManager().attach(serverConfig.getServerAppState());
        
        agentConfig = new AgentConfiguratorImpl(new AgentsAppState(this));
        agentConfig.getAgentsAppState().setBodiesAppState(bodyConfig.getBodiesAppState());
        app.getStateManager().attach(agentConfig.getAgentsAppState());

        app.getStateManager().attach(new LoggingViewerAppState());
        
        //app.getStateManager().attach(initAppState);
        initializer.initWorld(worldConfig);
        initializer.initHouse(houseConfig);
        initializer.initBodies(bodyConfig);
        initializer.initDevices(deviceConfig);
        initializer.initServer(serverConfig);
        initializer.initAgents(agentConfig);

        random = new Random(seed);
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
    
    private void pausePHAT() {
        paused = true;
        
        bulletAppState = app.getStateManager().getState(BulletAppState.class);
                
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
        if(appState != null) {
            app.getStateManager().detach(appState);
        }
    }
    
    private void resumePHAT() {
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
    public void finalize(SimpleApplication app) {
        
    }
    
    public Random getRandom() {
        return random;
    }

    public PHATCalendar getSimTime() {
        return worldConfig.getWorldAppState().getCalendar();
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
}