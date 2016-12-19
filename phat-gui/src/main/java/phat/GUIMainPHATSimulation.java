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
package phat;

import java.util.logging.Level;
import java.util.logging.Logger;
import phat.agents.Agent;
import phat.agents.HumanAgent;
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
import phat.agents.commands.ActivateActuatorEventsLauncherCommand;
import phat.agents.commands.ActivateCallStateEventsLauncherCommand;
import phat.agents.commands.ActivateWordsHeardEventsLauncherCommand;
import phat.body.BodiesAppState;
import phat.body.commands.SetBodyHeightCommand;
import phat.body.commands.SetBodyInHouseSpaceCommand;
import phat.body.commands.SetStoopedBodyCommand;
import phat.body.commands.TremblingHandCommand;
import phat.body.commands.TremblingHeadCommand;
import phat.body.sensing.hearing.GrammarFacilitator;
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
public class GUIMainPHATSimulation implements PHATInitializer {

    public static void main(String[] args) {
        String[] a = {/*"-ml",*/ "-record"};
        GUIMainPHATSimulation sim = new GUIMainPHATSimulation();
        GUIPHATInterface phat = new GUIPHATInterface(sim, new GUIArgumentProcessor(a));
        phat.setSeed(0);
        phat.setDisplayHeight(800);
        phat.setDisplayWidth(480);
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
        bodyConfig.runCommand(new SetBodyInHouseSpaceCommand("Relative", "House1", "BedRoom1"));
        //bodyConfig.runCommand(new TremblingHeadCommand("Relative", true));
        //bodyConfig.runCommand(new SetStoopedBodyCommand("Relative", true));
        //bodyConfig.runCommand(new TremblingHandCommand("Relative", true, true));
        //bodyConfig.runCommand(new BodyLabelCommand("Relative", true));
        /*SetCameraToBodyCommand setCameraToBodyCommand = new SetCameraToBodyCommand("Relative");
        setCameraToBodyCommand.setFront(true);
        setCameraToBodyCommand.setDistance(3f);
        setCameraToBodyCommand.setHeight(15f);
        bodyConfig.runCommand(setCameraToBodyCommand);
        bodyConfig.runCommand(new SetPCListenerToBodyCommand("Relative"));*/
        bodyConfig.runCommand(new SetBodyHeightCommand("Relative", 1.7f));
    }
    
    @Override
    public void initDevices(DeviceConfigurator deviceConfig) {
        //deviceConfig.runCommand(new CreateSmartphoneCommand("Smartphone1"));
        //deviceConfig.runCommand(new SetDeviceOnFurnitureCommand("Smartphone1", "House1", "Table1"));
    }

    @Override
    public void initServer(ServerConfigurator deviceConfig) {
        /*deviceConfig.runCommand(new SetAndroidEmulatorCommand("Smartphone1", "Smartphone1", "emulator-5554"));
        //deviceConfig.runCommand(new StartActivityCommand("Smartphone1", "phat.android.apps", "CameraCaptureActivity"));
        
        deviceConfig.runCommand(new StartActivityCommand("Smartphone1", "phat.android.apps.vibrator", "VibratorTestActivity"));
        
        DisplayAVDScreenCommand displayCommand = new DisplayAVDScreenCommand("Smartphone1", "Smartphone1");
        displayCommand.setFrecuency(0.5f);
        deviceConfig.runCommand(displayCommand);*/
    }
    
    @Override
    public void initAgents(AgentConfigurator agentsConfig) {
        /*Agent relative = new HumanAgent("Relative");
        
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
        sleep.setFinishCondition(new TimerFinishedCondition(0, 0, 5));
        
        MoveToSpace moveToGettingDressedArea1 = new MoveToSpace(relative, "GoToGettingDressedArea1", "GettingDressedArea1");
        
        MoveToSpace moveToHaveBreakfast = new MoveToSpace(relative, "GoToHaveBreakfast", "Kitchen");
        
        SitDownAutomaton sitDownInKitchen = new SitDownAutomaton(relative, "Chair1");
        
        Automaton haveBreakfast = new DrinkAutomaton(relative).setFinishCondition(new TimerFinishedCondition(0, 0, 20));
        
        UseObjectAutomaton useSink = new UseObjectAutomaton(relative, "Sink");
        useSink.setFinishCondition(new TimerFinishedCondition(0, 0, 30));
        
        Automaton wait1 = new DoNothing(relative, "Wait1").setFinishCondition(new TimerFinishedCondition(0, 0, 5));
        Automaton wait2 = new DoNothing(relative, "Wait2").setFinishCondition(new TimerFinishedCondition(0, 0, 5));
        Automaton wait3 = new DoNothing(relative, "Wait3").setFinishCondition(new TimerFinishedCondition(0, 0, 5));
        Automaton wait4 = new DoNothing(relative, "Wait3").setFinishCondition(new TimerFinishedCondition(0, 0, 5));
        
        UseDoorbellAutomaton useDoorbell = new UseDoorbellAutomaton(relative, "Doorbell1");
        
        FallAutomaton fall = new FallAutomaton(relative, "TripOver");
        fall.setFinishCondition(new TimerFinishedCondition(0, 0, 5));
        
        SayAutomaton say1 = new SayAutomaton(relative, "SayGoodMorning", "i need help", 0.5f);
        SayAutomaton say2 = new SayAutomaton(relative, "SayGoodMorning", "where are you", 0.5f);
        SayAutomaton say3 = new SayAutomaton(relative, "SayGoodMorning", "look at me", 0.5f);
        
        StandUpAutomaton standUp3 = new StandUpAutomaton(relative, "StandUp");
        

        FSM fsm = new FSM(relative);
        fsm.registerStartState(wait1);
        fsm.registerTransition(wait1, say1);
        fsm.registerTransition(say1, wait2);
        fsm.registerTransition(wait2, say2);
        fsm.registerTransition(say2, wait3);
        fsm.registerTransition(wait3, say3);
        fsm.registerTransition(say3, wait4);
        fsm.registerTransition(wait4, moveToBathroom1);
        
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
        fsm.registerFinalState(useSink);
        
        fsm.addListener(new AutomatonIcon());
        
        relative.setAutomaton(fsm);
        agentsConfig.add(relative);
        
        System.setProperty("java.util.logging.config.class", "");
        Logger.getLogger("").setLevel(Level.OFF);
        
        agentsConfig.runCommand(new ActivateActuatorEventsLauncherCommand(null));
        agentsConfig.runCommand(new ActivateCallStateEventsLauncherCommand(null));
        ActivateWordsHeardEventsLauncherCommand awhelc = new ActivateWordsHeardEventsLauncherCommand("Relative", null);
        awhelc.addWord("i");
        awhelc.addWord("need");
        awhelc.addWord("help");
        awhelc.addWord("where");
        awhelc.addWord("are");
        awhelc.addWord("you");
        awhelc.addWord("look");
        awhelc.addWord("at");
        awhelc.addWord("me");
        
        GrammarFacilitator grammarFacilitator = new GrammarFacilitator(System.getProperty("user.dir"), "basic");
        grammarFacilitator.add("i need help");
        grammarFacilitator.add("where are you");
        grammarFacilitator.add("look at me");
        grammarFacilitator.createFile();
        awhelc.setGrammarFacilitator(grammarFacilitator);
        
        agentsConfig.runCommand(awhelc);*/
    }

    @Override
    public String getTittle() {
        return "PHAT-"+getClass().getSimpleName();
    }
}