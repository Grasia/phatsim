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
package phat.server;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.scene.Node;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import phat.devices.DevicesAppState;
import phat.server.commands.DisplayAVDScreenCommand;
import phat.server.commands.PHATServerCommand;
import phat.devices.smartphone.SmartPhoneFactory;
import phat.mobile.adm.AndroidVirtualDevice;
import phat.structures.houses.HouseAppState;
import phat.world.WorldAppState;

/**
 *
 * @author pablo
 */
public class ServerAppState extends AbstractAppState {

    SimpleApplication app;
    AssetManager assetManager;
    BulletAppState bulletAppState;
    PHATServerManager serverManager;
    DevicesAppState devicesAppState;
    HouseAppState houseAppState;
    WorldAppState worldAppState;
    Map<String, AndroidVirtualDevice> availableAVDs = new HashMap<String, AndroidVirtualDevice>();
    ConcurrentLinkedQueue<PHATServerCommand> runningCommands = new ConcurrentLinkedQueue<PHATServerCommand>();
    ConcurrentLinkedQueue<PHATServerCommand> pendingCommands = new ConcurrentLinkedQueue<PHATServerCommand>();

    @Override
    public void initialize(AppStateManager stateManager, Application application) {
        System.out.println("Inititalize " + getClass().getSimpleName());
        super.initialize(stateManager, application);
        this.app = (SimpleApplication) application;
        this.assetManager = application.getAssetManager();

        worldAppState = app.getStateManager().getState(WorldAppState.class);
        houseAppState = app.getStateManager().getState(HouseAppState.class);
        bulletAppState = app.getStateManager().getState(BulletAppState.class);
        devicesAppState = app.getStateManager().getState(DevicesAppState.class);
        
        serverManager = new PHATServerManager();

        SmartPhoneFactory.init(bulletAppState, assetManager, app.getRenderManager(), app.getCamera(), app.getAudioRenderer());
    }

    public void runCommand(PHATServerCommand command) {
        pendingCommands.add(command);
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);

        runningCommands.addAll(pendingCommands);
        pendingCommands.clear();
        for (PHATServerCommand bc : runningCommands) {
            bc.run(app);
        }
        runningCommands.clear();
    }

    public void addAVD(String smartphoneId, AndroidVirtualDevice avd) {
        availableAVDs.put(smartphoneId, avd);
    }

    public PHATServerManager getServerManager() {
        return serverManager;
    }

    public AndroidVirtualDevice getAVD(String deviceId) {
        return availableAVDs.get(deviceId);
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
        // close connections with emulators
        Set<String> ids = availableAVDs.keySet();
        if (ids != null) {
            for (String id : ids) {
                AndroidVirtualDevice avd = availableAVDs.get(id);
                String avdId = avd.getAvdName();
                new DisplayAVDScreenCommand(id, avdId, false)
                .run(app);
                // Try to exit from an application
                // TODO Improve it
                avd.pressBackPhysicalButton();
                avd.pressBackPhysicalButton();
            }
            
        }
        if (ids != null && !ids.isEmpty()) {
            AndroidVirtualDevice.shutdown();
        }
        
        if (serverManager != null) {
            System.out.println("ServerManager Stopped!!");
            serverManager.stop();
        }
    }
}
