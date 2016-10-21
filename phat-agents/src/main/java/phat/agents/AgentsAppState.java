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
import com.jme3.scene.Node;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import phat.PHATInterface;
import phat.agents.automaton.Automaton;
import phat.agents.commands.PHATAgentCommand;
import phat.agents.events.BodyEventSource;
import phat.agents.events.PHATAudioEvent;
import phat.agents.events.PHATEvent;
import phat.agents.events.PHATEventListener;
import phat.agents.events.actuators.CallStateEventLauncher;
import phat.agents.events.actuators.DeviceSource;
import phat.body.BodiesAppState;
import phat.body.sensing.hearing.HearingSense;
import phat.body.sensing.hearing.WordsHeardListener;
import phat.devices.DevicesAppState;
import phat.mobile.adm.AndroidVirtualDevice;
import phat.sensors.microphone.MicrophoneControl;
import phat.server.ServerAppState;
import phat.structures.houses.HouseAppState;
import phat.world.PHATCalendar;
import phat.world.WorldAppState;

/**
 *
 * @author pablo
 */
public class AgentsAppState extends AbstractAppState implements PHATEventListener, WordsHeardListener {

    SimpleApplication app;
    AssetManager assetManager;
    BulletAppState bulletAppState;
    Node rootNode;
    HouseAppState houseAppState;
    WorldAppState worldAppState;
    BodiesAppState bodiesAppState;
    DevicesAppState devicesAppState;
    ServerAppState serverAppState;
    PHATInterface phatInterface;
    final Map<String, Agent> availableAgents = new HashMap<>();
    ConcurrentLinkedQueue<PHATAgentCommand> runningCommands = new ConcurrentLinkedQueue<>();
    ConcurrentLinkedQueue<PHATAgentCommand> pendingCommands = new ConcurrentLinkedQueue<>();
    ConcurrentLinkedQueue<PHATEvent> events = new ConcurrentLinkedQueue<>();
    List<CallStateEventLauncher> callStateEventLaunchers = new ArrayList<>();

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
        worldAppState = app.getStateManager().getState(WorldAppState.class);
        houseAppState = app.getStateManager().getState(HouseAppState.class);
        devicesAppState = app.getStateManager().getState(DevicesAppState.class);
        serverAppState = app.getStateManager().getState(ServerAppState.class);

        for (Agent a : availableAgents.values()) {
            a.setAgentsAppState(this);
        }
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);

        if (!callStateEventLaunchers.isEmpty()) {
            for (CallStateEventLauncher callStateEventLauncher : callStateEventLaunchers) {
                callStateEventLauncher.update(tpf);
            }
        }

        if (!pendingCommands.isEmpty()) {
            runningCommands.addAll(pendingCommands);
            pendingCommands.clear();
            for (PHATAgentCommand bc : runningCommands) {
                bc.run(app);
            }
            runningCommands.clear();
        }

        for (PHATAgentTick agent : availableAgents.values()) {
            for (PHATEvent e : events) {
                ((Agent) agent).getEventManager().add(e);
            }
        }
        events.clear();

        for (PHATAgentTick agent : availableAgents.values()) {
            agent.update(phatInterface);
        }
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

    public DevicesAppState getDevicesAppState() {
        return devicesAppState;
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

    public void runCommand(PHATAgentCommand command) {
        pendingCommands.add(command);
    }

    public void activateAllCallStateEventLaunchers() {
        for (String id : devicesAppState.getDeviceIds()) {
            Node device = devicesAppState.getDevice(id);
            AndroidVirtualDevice avd = serverAppState.getAVD(id);
            if (device != null && avd != null) {
                callStateEventLaunchers.add(new CallStateEventLauncher(avd, new DeviceSource(device), this));
            }
        }
    }

    @Override
    public void newEvent(PHATEvent event) {
        add(event);
    }

    public void activateWoredsHeard(String id) {
        Node body = bodiesAppState.getBody(id);
        MicrophoneControl micro = body.getControl(MicrophoneControl.class);
        HearingSense hs = micro.getListener(HearingSense.class);
        hs.add(this);
    }

    @Override
    public void notifyNewWordsHeard(String bodyId, String[] words) {
        String id = bodyId + ":";
        for (int i = 0; i < words.length - 1; i++) {
            id += words[i] + "-";
        }
        id += words[words.length - 1];
        add(new PHATAudioEvent(id, new BodyEventSource(bodiesAppState.getBody(bodyId))));
    }
    
    public boolean exists(String agentId) {
        return availableAgents.get(agentId) != null;
    }
    
    public Automaton getCurrentAction(String agentId) {
        Agent agent = availableAgents.get(agentId);
        if(agent != null) {
            return agent.getCurrentAction();
        }
        return null;
    }
    
    public PHATCalendar getTime() {
        return worldAppState.getCalendar();
    }
}
