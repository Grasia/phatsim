/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.agents;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import phat.PHATInterface;
import phat.agents.commands.PHATAgentCommand;
import phat.agents.events.PHATEvent;
import phat.agents.events.PHATEventManager;
import phat.body.BodiesAppState;
import phat.commands.PHATCommand;
import phat.structures.houses.HouseAppState;
import phat.world.WorldAppState;

/**
 *
 * @author pablo
 */
public class AgentsAppState extends AbstractAppState {

    SimpleApplication app;
    AssetManager assetManager;
    BulletAppState bulletAppState;
    Node rootNode;
    HouseAppState houseAppState;
    WorldAppState worldAppState;
    BodiesAppState bodiesAppState;
    PHATInterface phatInterface;
    final Map<String, Agent> availableAgents = new HashMap<>();
    ConcurrentLinkedQueue<PHATAgentCommand> commands = new ConcurrentLinkedQueue<>();
    ConcurrentLinkedQueue<PHATEvent> events = new ConcurrentLinkedQueue<>();

    public AgentsAppState(PHATInterface phatInterface) {
        this.phatInterface = phatInterface;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application application) {
        System.out.println("Inititalize " + getClass().getSimpleName());
        super.initialize(stateManager, application);
        this.app = (SimpleApplication) application;
        this.assetManager = application.getAssetManager();
        this.rootNode = app.getRootNode();

        bodiesAppState = app.getStateManager().getState(BodiesAppState.class);

        for (Agent a : availableAgents.values()) {
            a.setAgentsAppState(this);
        }
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);

        for (PHATCommand bc : commands) {
            bc.run(app);
        }

        for (PHATAgentTick agent : availableAgents.values()) {
            for (PHATEvent e : events) {
                ((Agent) agent).getEventManager().add(e);
            }
            agent.update(phatInterface);
        }

        commands.clear();
    }

    public void add(Agent agent) {
        availableAgents.put(agent.getId(), agent);
        agent.setAgentsAppState(this);
    }

    public void add(PHATEvent event) {
        events.add(event);
    }

    public void stop(Agent agent) {
    }

    public BodiesAppState getBodiesAppState() {
        return bodiesAppState;
    }

    public void setBodiesAppState(BodiesAppState bodiesAppState) {
        this.bodiesAppState = bodiesAppState;
    }
    
    public HouseAppState getHouseAppState() {
        return houseAppState;
    }

    public PHATInterface getPHAInterface() {
        return phatInterface;
    }
}
