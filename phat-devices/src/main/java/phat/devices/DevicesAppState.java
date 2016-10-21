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
package phat.devices;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import phat.body.control.physics.PHATCharacterControl;
import phat.devices.commands.PHATDeviceCommand;
import phat.devices.smartphone.SmartPhoneFactory;
import phat.structures.houses.House;
import phat.structures.houses.HouseAppState;
import phat.util.SpatialUtils;
import phat.world.WorldAppState;

/**
 *
 * @author pablo
 */
public class DevicesAppState extends AbstractAppState {

    SimpleApplication app;
    AssetManager assetManager;
    BulletAppState bulletAppState;
    HouseAppState houseAppState;
    WorldAppState worldAppState;
    Map<String, Node> availableDevices = new HashMap<>();
    ConcurrentLinkedQueue<PHATDeviceCommand> runningCommands = new ConcurrentLinkedQueue<>();
    ConcurrentLinkedQueue<PHATDeviceCommand> pendingCommands = new ConcurrentLinkedQueue<>();

    @Override
    public void initialize(AppStateManager stateManager, Application application) {
        System.out.println("Inititalize " + getClass().getSimpleName());
        super.initialize(stateManager, application);
        this.app = (SimpleApplication) application;
        this.assetManager = application.getAssetManager();

        worldAppState = app.getStateManager().getState(WorldAppState.class);
        houseAppState = app.getStateManager().getState(HouseAppState.class);
        bulletAppState = app.getStateManager().getState(BulletAppState.class);

        SmartPhoneFactory.init(bulletAppState, assetManager, app.getRenderManager(), app.getCamera(), app.getAudioRenderer());
    }

    public void runCommand(PHATDeviceCommand command) {
        pendingCommands.add(command);
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);

        runningCommands.addAll(pendingCommands);
        pendingCommands.clear();
        for (PHATDeviceCommand bc : runningCommands) {
            bc.run(app);
        }
        runningCommands.clear();
    }

    public boolean isBodyInAHouse(String bodyId) {
        Node body = availableDevices.get(bodyId);
        for (House house : houseAppState.getHouses()) {
            if (house.isSpatialInHouse(body)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isBodyInTheWorld(String bodyId) {
        Node body = availableDevices.get(bodyId);
        if (body != null) {
            return body.getParent() != null;
        }
        return false;
    }

    public void registerAllAndroidDevicesInScenario() {
        List<Spatial> devices = SpatialUtils.getSpatialsByRole(app.getRootNode(), "AndroidDevice");
        for (Spatial device : devices) {
            String id = device.getUserData("ID");
            if (id != null) {
                addDevice(id, (Node) device);
            }
        }
    }

    public void addDevice(String smartphoneId, Node smartphone) {
        availableDevices.put(smartphoneId, smartphone);
    }

    public Node getDevice(String deviceId) {
        return availableDevices.get(deviceId);
    }
    
    public Set<String> getDeviceIds() {
        return availableDevices.keySet();
    }
    
    public Vector3f getLocation(String bodyId) {
        Node body = availableDevices.get(bodyId);
        if (body != null && body.getParent() != null) {
            if(body.getControl(RigidBodyControl.class) != null) {
                return body.getControl(RigidBodyControl.class).getPhysicsLocation();
            } else {
                return body.getWorldTranslation();
            }
        }
        return null;
    }

    @Override
    public void cleanup() {
        super.cleanup();
    }
}
