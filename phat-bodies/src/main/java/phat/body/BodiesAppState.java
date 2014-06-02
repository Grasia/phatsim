/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.body;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import phat.audio.AudioFactory;
import phat.body.commands.CreateBodyTypeCommand;
import phat.body.commands.RemoveBodyFromSpaceCommand;
import phat.body.commands.SetBodyInHouseSpaceCommand;
import phat.body.control.physics.PHATCharacterControl;
import phat.commands.PHATCommand;
import phat.structures.houses.House;
import phat.structures.houses.HouseAppState;
import phat.world.PHATCalendar;
import phat.world.WorldAppState;

/**
 *
 * @author pablo
 */
public class BodiesAppState extends AbstractAppState {

    SimpleApplication app;
    AssetManager assetManager;
    BulletAppState bulletAppState;
    Node rootNode;
    HouseAppState houseAppState;
    WorldAppState worldAppState;
    Node humans;
    Map<String, Node> availableBodies = new HashMap<>();

    public enum BodyType {
        Elder, Young, Sinbad, ElderLP
    }
    
    ConcurrentLinkedQueue<PHATCommand> runningCommands = new ConcurrentLinkedQueue<>();
    ConcurrentLinkedQueue<PHATCommand> pendingCommands = new ConcurrentLinkedQueue<>();
    
    @Override
    public void initialize(AppStateManager stateManager, Application application) {
        System.out.println("Inititalize " + getClass().getSimpleName());
        super.initialize(stateManager, application);
        this.app = (SimpleApplication) application;
        this.assetManager = application.getAssetManager();
        this.rootNode = app.getRootNode();

        worldAppState = app.getStateManager().getState(WorldAppState.class);
        houseAppState = app.getStateManager().getState(HouseAppState.class);
        
        AudioFactory.init(application.getAudioRenderer(), assetManager, rootNode);
        createHumansNode();
    }

    private void createHumansNode() {
        Node world = (Node) rootNode.getChild("World");
        if (world.getChild("Humans") == null) {
            humans = new Node("Humans");
            world.attachChild(humans);
        } else {
            humans = (Node) world.getChild("Humans");
        }
    }

    public Node getBodiesNode() {
        return humans;
    }

    public String createBody(BodyType type, String id) {
        switch (type) {
            case Elder:
                createBodyType("Models/People/Elder/Elder.j3o", id);
                break;
            case ElderLP:
                createBodyType("Models/male/male.j3o", id);
                break;
            case Young:
                createBodyType("Models/People/Male/Male.j3o", id);
                break;
            case Sinbad:
                createBodyType("Models/Sinbad/Sinbad.j3o", id);
                break;
        }
        return null;
    }

    private void createBodyType(String resource, String id) {
        pendingCommands.add(new CreateBodyTypeCommand(id, resource));
    }

    public void runCommand(PHATCommand command) {
        pendingCommands.add(command);
    }
    
    public boolean isBodyInTheWorld(String bodyId) {
        Node body = availableBodies.get(bodyId);
        if (body != null) {
            return body.getParent() != null;
        }
        return false;
    }
    
    public boolean isBodyInAHouse(String bodyId) {
        Node body = availableBodies.get(bodyId);
        for(House house: houseAppState.getHouses()) {
            if(house.isSpatialInHouse(body)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isBodyInHouse(String bodyId, String houseId) {
        Node body = availableBodies.get(bodyId);
        House house = houseAppState.getHouse(houseId);
        if(house != null) {
            return house.isSpatialInHouse(body);
        }
        return false;
    }

    public Vector3f getLocation(String bodyId) {
        Node body = availableBodies.get(bodyId);
        if (body != null && body.getParent() != null) {
            PHATCharacterControl cc = body.getControl(PHATCharacterControl.class);
            return cc.getLocation();
        }
        return null;
    }
    
    public float getSpeed(String bodyId) {
        Node body = availableBodies.get(bodyId);
        if (body != null && body.getParent() != null) {
            return body.getUserData("Speed");
        }
        return -1;
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);

        /*for(Node n: availableBodies.values()) {
            System.out.println("\n"+n.getName());
            for(int i = 0; i < n.getNumControls(); i++) {
                System.out.println("\t"+n.getControl(i).getClass().getSimpleName());
            }
        }*/
        
        runningCommands.addAll(pendingCommands);
        pendingCommands.clear();
        for (PHATCommand bc : runningCommands) {
            System.out.println("Running Command: " + bc);
            bc.run(app);
        }
        runningCommands.clear();
    }

    public void setInSpace(String bodyId, String houseId, String spaceId) {
        pendingCommands.add(new SetBodyInHouseSpaceCommand(bodyId, houseId, spaceId));
    }

    public void removeFromScene(String bodyId) {
        pendingCommands.add(new RemoveBodyFromSpaceCommand(bodyId));
    }

    public Node getRootNode() {
        return rootNode;
    }

    public Map<String, Node> getAvailableBodies() {
        return availableBodies;
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }
    
    public PHATCalendar getTime() {
    	return worldAppState.getCalendar();
    }
}
