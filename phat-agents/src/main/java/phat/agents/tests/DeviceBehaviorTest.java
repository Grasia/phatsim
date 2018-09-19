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
package phat.agents.tests;

import phat.*;
import phat.agents.DeviceAgent;
import phat.agents.automaton.Automaton;
import phat.agents.automaton.ParallelAutomaton;
import phat.agents.automaton.Transition;
import phat.agents.automaton.conditions.TimerFinishedCondition;
import phat.agents.automaton.devices.FSMProgramBehavior;
import phat.agents.automaton.devices.ProgState;
import phat.agents.automaton.devices.TextToSpeachAutomaton;
import phat.agents.automaton.devices.VibrateDeviceAutomaton;
import phat.config.AgentConfigurator;
import phat.config.BodyConfigurator;
import phat.config.DeviceConfigurator;
import phat.config.HouseConfigurator;
import phat.config.ServerConfigurator;
import phat.config.WorldConfigurator;
import phat.devices.commands.CreateSmartphoneCommand;
import phat.devices.commands.SetDeviceOnFurnitureCommand;
import phat.structures.houses.HouseFactory;
import phat.world.WorldAppState;

/**
 *
 * @author pablo
 */
public class DeviceBehaviorTest implements PHATInitializer {

    public static void main(String[] args) {
        DeviceBehaviorTest sim = new DeviceBehaviorTest();
        PHATInterface phat = new PHATInterface(sim);
        phat.start();
    }

    @Override
    public void initWorld(WorldConfigurator worldConfig) {
        worldConfig.setTime(2014, 2, 3, 14, 0, 0);
        worldConfig.setTimeVisible(true);
        worldConfig.setLandType(WorldAppState.LandType.Grass);
    }

    @Override
    public void initHouse(HouseConfigurator houseConfig) {
        houseConfig.addHouseType("House1", HouseFactory.HouseType.House3room2bath);
    }

    @Override
    public void initBodies(BodyConfigurator bodyConfig) {
    }

    @Override
    public void initDevices(DeviceConfigurator deviceConfig) {
        deviceConfig.runCommand(new CreateSmartphoneCommand("Smartphone1"));    
        deviceConfig.runCommand(new SetDeviceOnFurnitureCommand("Smartphone1", "House1", "Table1"));

        deviceConfig.runCommand(new CreateSmartphoneCommand("Smartphone2"));
        deviceConfig.runCommand(new SetDeviceOnFurnitureCommand("Smartphone2", "House1", "Table1"));
    }

    @Override
    public void initServer(ServerConfigurator deviceConfig) {

    }

    @Override
    public void initAgents(AgentConfigurator agentsConfig) {

        String deviceId = "Smartphone1";
        
        DeviceAgent deviceAgent = new DeviceAgent(deviceId);

        ParallelAutomaton pa = new ParallelAutomaton(deviceAgent, "ParallelAutomaton");

        FSMProgramBehavior vibrate1 = new FSMProgramBehavior(deviceAgent, "FSMVibrate1") {
            @Override
            protected void initProgramStates(PHATInterface phatInterface) {
                ProgState psWaiting = new ProgState(agent, "PSWaiting");
                
                ProgState psVibrating = new ProgState(agent, "PSVibrating");
                Automaton vibrate = new VibrateDeviceAutomaton(agent, "VibrateDeviceAutomaton").setDeviceId("Smartphone1").setMillis(2000);
                psVibrating.addTransition(vibrate, false);
                psVibrating.addTransition(new TextToSpeachAutomaton(agent, "TTS1").setDeviceId("Smartphone1").setMessage("Hello darling"), false);
                
                Transition waitingToVibrating = new Transition(new TimerFinishedCondition(0, 0, 5), psVibrating);
                Transition vibratingToWaiting = new Transition(new TimerFinishedCondition(0, 0, 5), psWaiting);
                
                registerStartState(psWaiting);
                
                registerTransition(psWaiting, waitingToVibrating);
                registerTransition(psVibrating, vibratingToWaiting);
            }
        };
        
        FSMProgramBehavior vibrate2 = new FSMProgramBehavior(deviceAgent, "FSMVibrate2") {
            @Override
            protected void initProgramStates(PHATInterface phatInterface) {
                ProgState psWaiting = new ProgState(agent, "PSWaiting");
                
                ProgState psVibrating = new ProgState(agent, "PSVibrating");
                Automaton vibrate = new VibrateDeviceAutomaton(agent, "VibrateDeviceAutomaton").setDeviceId("Smartphone2").setMillis(2000);
                psVibrating.addTransition(vibrate, false);
                
                Transition waitingToVibrating = new Transition(new TimerFinishedCondition(0, 0, 5), psVibrating);
                Transition vibratingToWaiting = new Transition(new TimerFinishedCondition(0, 0, 5), psWaiting);
                
                registerStartState(psWaiting);
                
                registerTransition(psWaiting, waitingToVibrating);
                registerTransition(psVibrating, vibratingToWaiting);
            }
        };

        pa.addTransition(vibrate1, true);
        pa.addTransition(vibrate2, true);

        deviceAgent.setAutomaton(pa);
        agentsConfig.add(deviceAgent);        
    }
    
    @Override
    public String getTittle() {
        return "PHAT-" + getClass().getSimpleName();
    }

    @Override
    public String getDescription() {
        return "DeviceBehaviorTest";
    }
}