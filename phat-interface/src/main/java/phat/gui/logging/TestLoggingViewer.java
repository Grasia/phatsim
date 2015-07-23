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
package phat.gui.logging;

import phat.*;
import phat.agents.Agent;
import phat.agents.AgentImpl;
import phat.agents.automaton.Automaton;
import phat.agents.automaton.AutomatonIcon;
import phat.agents.automaton.DoNothing;
import phat.agents.automaton.DrinkAutomaton;
import phat.agents.automaton.FSM;
import phat.agents.automaton.FallAutomaton;
import phat.agents.automaton.GoIntoBedAutomaton;
import phat.agents.automaton.MoveToSpace;
import phat.agents.automaton.SayAutomaton;
import phat.agents.automaton.SitDownAutomaton;
import phat.agents.automaton.StandUpAutomaton;
import phat.agents.automaton.UseObjectAutomaton;
import phat.agents.automaton.conditions.TimerFinishedCondition;
import phat.agents.automaton.uses.UseDoorbellAutomaton;
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
public class TestLoggingViewer implements PHATInitializer {

    public static void main(String[] args) {
        TestLoggingViewer sim = new TestLoggingViewer();
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
        bodyConfig.createBody(BodiesAppState.BodyType.ElderLP, "Relative");        
        bodyConfig.setInSpace("Relative", "House1", "BedRoom1");
        bodyConfig.runCommand(new TremblingHeadCommand("Relative", true));
        bodyConfig.runCommand(new SetStoopedBodyCommand("Relative", true));
        bodyConfig.runCommand(new TremblingHandCommand("Relative", true, true));
        //bodyConfig.runCommand(new BodyLabelCommand("Relative", true));
        /*SetCameraToBodyCommand setCameraToBodyCommand = new SetCameraToBodyCommand("Relative");
        setCameraToBodyCommand.setFront(true);
        setCameraToBodyCommand.setDistance(3f);
        setCameraToBodyCommand.setHeight(15f);
        bodyConfig.runCommand(setCameraToBodyCommand);*/
        bodyConfig.runCommand(new SetPCListenerToBodyCommand("Relative"));
        bodyConfig.runCommand(new SetBodyHeightCommand("Relative", 1.7f));
    }
    
    @Override
    public void initDevices(DeviceConfigurator deviceConfig) {
    }

    @Override
    public void initServer(ServerConfigurator deviceConfig) {
    }
    
    @Override
    public void initAgents(AgentConfigurator agentsConfig) {
        Agent relative = new AgentImpl("Relative");
        
        MoveToSpace moveToBathroom1 = new MoveToSpace(relative, "GoToBathRoom1", "BathRoom1");
        
        UseObjectAutomaton useShower = new UseObjectAutomaton(relative, "Shower1");
        useShower.setFinishCondition(new TimerFinishedCondition(0, 0, 20));
        
        UseObjectAutomaton useWC1 = new UseObjectAutomaton(relative, "WC1");
        useWC1.setFinishCondition(new TimerFinishedCondition(0, 0, 10));
        
        UseObjectAutomaton useBasin1 = new UseObjectAutomaton(relative, "Basin1");
        useBasin1.setFinishCondition(new TimerFinishedCondition(0, 0, 10));
        
        MoveToSpace moveToBedroom1 = new MoveToSpace(relative, "GoToBedRoom1", "BedRoom1");
        
        GoIntoBedAutomaton goIntoBed = new GoIntoBedAutomaton(relative, "Bed1");
        
        StandUpAutomaton standUp1 = new StandUpAutomaton(relative, "StandUpFromBed");
        StandUpAutomaton standUp2 = new StandUpAutomaton(relative, "StandUpFromBed");
        
        DoNothing sleep = new DoNothing(relative, "Sleep");
        sleep.setFinishCondition(new TimerFinishedCondition(0, 0, 3));
        
        MoveToSpace moveToGettingDressedArea1 = new MoveToSpace(relative, "GoToGettingDressedArea1", "GettingDressedArea1");
        
        MoveToSpace moveToHaveBreakfast = new MoveToSpace(relative, "GoToHaveBreakfast", "Kitchen");
        
        SitDownAutomaton sitDownInKitchen = new SitDownAutomaton(relative, "Chair1");
        
        Automaton haveBreakfast = new DrinkAutomaton(relative).setFinishCondition(new TimerFinishedCondition(0, 0, 20));
        
        UseObjectAutomaton useSink = new UseObjectAutomaton(relative, "Sink");
        useSink.setFinishCondition(new TimerFinishedCondition(0, 0, 30));
        
        DoNothing fin = new DoNothing(relative, "Fin");
        fin.setFinishCondition(new TimerFinishedCondition(0, 0, 30));
        
        UseDoorbellAutomaton useDoorbell = new UseDoorbellAutomaton(relative, "Doorbell1");
        
        FallAutomaton fall = new FallAutomaton(relative, "TripOver");
        fall.setFinishCondition(new TimerFinishedCondition(0, 0, 5));
        
        SayAutomaton goodMorning = new SayAutomaton(relative, "SayGoodMorning", "Good Morning, Jorge!", 0.1f);
        
        StandUpAutomaton standUp3 = new StandUpAutomaton(relative, "StandUp");
                
        FSM fsm = new FSM(relative);
        fsm.registerStartState(goIntoBed);
        fsm.registerTransition(goIntoBed, sleep);
        fsm.registerTransition(sleep, standUp1);
        fsm.registerTransition(standUp1, goodMorning);
        fsm.registerTransition(goodMorning, moveToBathroom1);
        fsm.registerTransition(moveToBathroom1, fall);
        fsm.registerTransition(fall, standUp3);
        fsm.registerTransition(standUp3, useShower);
        fsm.registerTransition(useShower, useWC1);
        fsm.registerTransition(useWC1, useBasin1);
        fsm.registerTransition(useBasin1, moveToHaveBreakfast);
        fsm.registerTransition(moveToHaveBreakfast, sitDownInKitchen);
        fsm.registerTransition(sitDownInKitchen, haveBreakfast);
        fsm.registerTransition(haveBreakfast, standUp2);
        fsm.registerTransition(standUp2, useSink);
        fsm.registerTransition(useSink, fin);
        fsm.registerFinalState(fin);
        
        fsm.addListener(new AutomatonIcon());
        
        relative.setAutomaton(fsm);
        agentsConfig.add(relative);
    }

    @Override
    public String getTittle() {
        return "PHAT-"+getClass().getSimpleName();
    }
}