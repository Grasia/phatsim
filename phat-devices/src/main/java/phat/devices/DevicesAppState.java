package phat.devices;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.scene.Node;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import phat.commands.PHATCommand;
import phat.devices.commands.PHATDeviceCommand;
import phat.devices.smartphone.SmartPhoneFactory;
import phat.mobile.adm.AndroidVirtualDevice;
import phat.server.PHATServerManager;
import phat.structures.houses.HouseAppState;
import phat.world.WorldAppState;

/**
 *
 * @author pablo
 */
public class DevicesAppState  extends AbstractAppState {
    SimpleApplication app;
    AssetManager assetManager;
    BulletAppState bulletAppState;
    
    PHATServerManager serverManager;
    
    HouseAppState houseAppState;
    WorldAppState worldAppState;
    
    Map<String, Node> availableDevices = new HashMap<>();
    Map<String, AndroidVirtualDevice> availableAVDs = new HashMap<>();
    
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
        
        serverManager = new PHATServerManager();
        
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
    
    public boolean isBodyInTheWorld(String bodyId) {
        Node body = availableDevices.get(bodyId);
        if (body != null) {
            return body.getParent() != null;
        }
        return false;
    }

    public void addDevice(String smartphoneId, Node smartphone) {
        availableDevices.put(smartphoneId, smartphone);
    }
    
    public void addAVD(String smartphoneId, AndroidVirtualDevice avd) {
        availableAVDs.put(smartphoneId, avd);
    }

    public PHATServerManager getServerManager() {
        return serverManager;
    }
    
    public Node getDevice(String deviceId) {
        return availableDevices.get(deviceId);
    }
    
    public AndroidVirtualDevice getAVD(String deviceId) {
        return availableAVDs.get(deviceId);
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
        if(serverManager != null) {
            System.out.println("ServerManager Stopped!!");
            serverManager.stop();
        }
    }
}
