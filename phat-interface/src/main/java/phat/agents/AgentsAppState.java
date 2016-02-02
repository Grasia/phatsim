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
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import phat.PHATInterface;
import phat.agents.commands.PHATAgentCommand;
import phat.agents.events.PHATEvent;
import phat.agents.events.PHATEventManager;
import phat.body.BodiesAppState;
import phat.commands.PHATCommand;
import phat.gui.logging.LoggingViewerAppState;
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
        houseAppState = app.getStateManager().getState(HouseAppState.class);
        
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
    
    public Set<String> getAgentIds() {
        return availableAgents.keySet();
    }
    
    public Agent getAgent(String id) {
        return availableAgents.get(id);
    }
}
