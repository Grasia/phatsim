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
package phat.agents.filters;

import com.jme3.math.Vector3f;
import phat.*;
import phat.agents.Agent;
import phat.agents.AgentImpl;
import phat.agents.automaton.AutomatonIcon;
import phat.agents.automaton.DoNothing;
import phat.agents.automaton.FSM;
import phat.agents.automaton.Transition;
import phat.agents.automaton.conditions.EventCondition;
import phat.agents.automaton.conditions.TimerFinishedCondition;
import phat.agents.events.EventSource;
import phat.agents.events.PHATEventForAll;
import phat.body.BodiesAppState;
import phat.body.commands.SetBodyHeightCommand;
import phat.body.commands.SetPCListenerToBodyCommand;
import phat.body.commands.SetStoopedBodyCommand;
import phat.body.commands.TremblingHandCommand;
import phat.body.commands.TremblingHeadCommand;
import phat.config.AgentConfigurator;
import phat.config.BodyConfigurator;
import phat.config.DeviceConfigurator;
import phat.config.HouseConfigurator;
import phat.config.ServerConfigurator;
import phat.config.WorldConfigurator;
import phat.structures.houses.HouseFactory;
import phat.world.WorldAppState;

/**
 *
 * @author pablo
 */
public class SymptomEvolutionTest implements PHATInitializer {

    public static void main(String[] args) {
        SymptomEvolutionTest sim = new SymptomEvolutionTest();
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
        //houseConfig.setDebugNavMesh(true);
    }

    @Override
    public void initBodies(BodyConfigurator bodyConfig) {
        bodyConfig.createBody(BodiesAppState.BodyType.ElderLP, "Patient");
        bodyConfig.setInSpace("Patient", "House1", "Hall");
        bodyConfig.runCommand(new TremblingHeadCommand("Patient", true));
        bodyConfig.runCommand(new SetStoopedBodyCommand("Patient", true));
        bodyConfig.runCommand(new TremblingHandCommand("Patient", true, true));
        //bodyConfig.runCommand(new BodyLabelCommand("Relative", true));
        /*SetCameraToBodyCommand setCameraToBodyCommand = new SetCameraToBodyCommand("Patient");
         setCameraToBodyCommand.setFront(true);
         setCameraToBodyCommand.setDistance(3f);
         setCameraToBodyCommand.setHeight(15f);
         bodyConfig.runCommand(setCameraToBodyCommand);*/
        bodyConfig.runCommand(new SetPCListenerToBodyCommand("Patient"));
        bodyConfig.runCommand(new SetBodyHeightCommand("Patient", 1.7f));
    }

    @Override
    public void initDevices(DeviceConfigurator deviceConfig) {
    }

    @Override
    public void initServer(ServerConfigurator deviceConfig) {
    }
    
    @Override
    public void initAgents(AgentConfigurator agentsConfig) {
        Agent patient = new AgentImpl("Patient") {
            @Override
            protected void initAutomaton() {
                DoNothing doNothing = new DoNothing(this, "DoNothing");
                doNothing.setFinishCondition(new TimerFinishedCondition(1, 0, 0));

                FSM fsm = new FSM(this);
                fsm.registerStartState(doNothing);
                fsm.registerFinalState(doNothing);
                this.setAutomaton(fsm);

                fsm.addListener(new AutomatonIcon());

                DiseaseManager dm = new DiseaseManager(this);
                this.setDiseaseManager(dm);

                Symptom memoryLoss = new Symptom("MemoryLoss");
                FSMSymptomEvolution evo = new FSMSymptomEvolution(this, memoryLoss) {
                    @Override
                    public void initSymptomEvolutionBehavior(PHATInterface phatInterface) {
                        SymptomState none = new SymptomState(agent, symptom, Symptom.Level.NONE);
                        SymptomState low = new SymptomState(agent, symptom, Symptom.Level.LOW);
                        SymptomState medium = new SymptomState(agent, symptom, Symptom.Level.MEDIUM);
                        SymptomState high = new SymptomState(agent, symptom, Symptom.Level.HIGH);
                        
                        registerTransition(none, new Transition(new TimerFinishedCondition(0, 0, 5), low));
                        registerTransition(low, new Transition(new TimerFinishedCondition(0, 0, 5), medium));
                        registerTransition(medium, new Transition(new TimerFinishedCondition(0, 0, 5), high));
                        registerTransition(high, new Transition(new EventCondition("RemindEvent"), none));
                        
                        symptom.setSymptomEvolution(this);
                    }
                };
                
                memoryLoss.setCurrentLevel(Symptom.Level.NONE);
                dm.add(memoryLoss);

                setAutomaton(fsm);
            }
        };

        agentsConfig.add(patient);

        Agent reminder = new AgentImpl("Reminder") {
            @Override
            protected void initAutomaton() {
                DoNothing doNothing = new DoNothing(this, "Wait");
                doNothing.setFinishCondition(new TimerFinishedCondition(0, 0, 20));

                DoNothing remindAutomaton = new DoNothing(this, "Remaind");
                remindAutomaton.setFinishCondition(new TimerFinishedCondition(0, 0, 1));


                FSM fsm = new FSM(this);
                fsm.registerStartState(doNothing);

                PHATEventForAll remindEvent =
                        new PHATEventForAll(this, "RemindEvent", new EventSource() {
                    @Override
                    public Vector3f getLocation() {
                        return Vector3f.ZERO;
                    }
                });
                remindAutomaton.addListener(remindEvent);

                fsm.registerTransition(doNothing, remindAutomaton);
                fsm.registerTransition(remindAutomaton, doNothing);

                setAutomaton(fsm);
            }
        };

        agentsConfig.add(reminder);
    }

    @Override
    public String getTittle() {
        return "PHAT-" + getClass().getSimpleName();
    }
}