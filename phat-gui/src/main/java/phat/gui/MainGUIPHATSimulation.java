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
package phat.gui;

import phat.*;
import phat.agents.DeviceAgent;
import phat.agents.automaton.Automaton;
import phat.agents.automaton.AutomatonIcon;
import phat.agents.automaton.DoNothing;
import phat.agents.automaton.FSM;
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
public class MainGUIPHATSimulation implements PHATInitializer {

    public static void main(String[] args) {
        MainGUIPHATSimulation sim = new MainGUIPHATSimulation();
        PHATInterface phat = new GUIPHATInterface(sim);
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
        /*bodyConfig.createBody(BodiesAppState.BodyType.ElderLP, "Relative");        
        bodyConfig.setInSpace("Relative", "House1", "BedRoom1");
        bodyConfig.runCommand(new TremblingHeadCommand("Relative", true));
        bodyConfig.runCommand(new SetStoopedBodyCommand("Relative", true));
        bodyConfig.runCommand(new TremblingHandCommand("Relative", true, true));
        //bodyConfig.runCommand(new BodyLabelCommand("Relative", true));*/
 /*SetCameraToBodyCommand setCameraToBodyCommand = new SetCameraToBodyCommand("Relative");
        setCameraToBodyCommand.setFront(true);
        setCameraToBodyCommand.setDistance(3f);
        setCameraToBodyCommand.setHeight(15f);
        bodyConfig.runCommand(setCameraToBodyCommand);*/
 /*bodyConfig.runCommand(new SetPCListenerToBodyCommand("Relative"));
        bodyConfig.runCommand(new SetBodyHeightCommand("Relative", 1.7f));*/
    }

    @Override
    public void initDevices(DeviceConfigurator deviceConfig) {
        deviceConfig.runCommand(new CreateSmartphoneCommand("Smartphone1"));
        //deviceConfig.runCommand(new SetDeviceOnPartOfBodyCommand("Relative","Smartphone1", SetDeviceOnPartOfBodyCommand.PartOfBody.Chest));        
        deviceConfig.runCommand(new SetDeviceOnFurnitureCommand("Smartphone1", "House1", "Table1"));

        deviceConfig.runCommand(new CreateSmartphoneCommand("Smartphone2"));
        //deviceConfig.runCommand(new SetDeviceOnPartOfBodyCommand("Relative","Smartphone1", SetDeviceOnPartOfBodyCommand.PartOfBody.Chest));        
        deviceConfig.runCommand(new SetDeviceOnFurnitureCommand("Smartphone2", "House1", "Table1"));

        /*Reflections reflections = new Reflections("phat");
        Set<Class<? extends PHATCommand>> subTypes = reflections.getSubTypesOf(PHATCommand.class);
        System.out.println("\n\nCommand List:");
        for (Class<? extends PHATCommand> command : subTypes) {
            System.out.println(command.getSimpleName());
            for (Constructor c : command.getConstructors()) {
                System.out.print("\t(");
                Annotation[][] parameterAnnotations = c.getParameterAnnotations();
                Class[] parameterTypes = c.getParameterTypes();
                int i = 0;
                for (Annotation[] annotations : parameterAnnotations) {
                    Class parameterType = parameterTypes[i++];
                    for (Annotation annotation : annotations) {
                        if (annotation instanceof ParamCommandAnn) {
                            ParamCommandAnn myAnnotation = (ParamCommandAnn) annotation;
                            System.out.print(myAnnotation.name()+": " + parameterType.getName()+", ");
                            
                            if(parameterTypes.length == 2) {
                                Object [] args = {"Smartphone1","666666666"};
                                try {
                                    deviceConfig.runCommand((PHATDeviceCommand) c.newInstance(args));
                                } catch (InstantiationException ex) {
                                    Logger.getLogger(MainGUIPHATSimulation.class.getName()).log(Level.SEVERE, null, ex);
                                } catch (IllegalAccessException ex) {
                                    Logger.getLogger(MainGUIPHATSimulation.class.getName()).log(Level.SEVERE, null, ex);
                                } catch (IllegalArgumentException ex) {
                                    Logger.getLogger(MainGUIPHATSimulation.class.getName()).log(Level.SEVERE, null, ex);
                                } catch (InvocationTargetException ex) {
                                    Logger.getLogger(MainGUIPHATSimulation.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                    }
                }
                System.out.println(")");
            }
        }*/
        System.out.println("");
        /*deviceConfig.runCommand(new CreateSmartphoneCommand("Smartphone1"));
        deviceConfig.runCommand(new SetDeviceOnPartOfBodyCommand("Relative","Smartphone1", SetDeviceOnPartOfBodyCommand.PartOfBody.Chest));        
        deviceConfig.runCommand(new SetAndroidEmulatorCommand("Smartphone1", "Smartphone1", "emulator-5554"));
        //deviceConfig.runCommand(new StartActivityCommand("Smartphone1", "phat.android.apps", "CameraCaptureActivity"));
        deviceConfig.runCommand(new StartActivityCommand("Smartphone1", "phat.android.apps", "BodyPositionMonitoring"));
        
        DisplayAVDScreenCommand displayCommand = new DisplayAVDScreenCommand("Smartphone1", "Smartphone1");
        displayCommand.setFrecuency(0.5f);
        deviceConfig.runCommand(displayCommand);*/
    }

    @Override
    public void initServer(ServerConfigurator deviceConfig) {

    }

    @Override
    public void initAgents(AgentConfigurator agentsConfig) {
        /*
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
        agentsConfig.add(relative);*/

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

        /*DoNothing idle = (DoNothing) new DoNothing(deviceAgent, "DeviceIdle")
                .setFinishCondition(new TimerFinishedCondition(0, 0, 5));
        VibrateDeviceAutomaton vda = new VibrateDeviceAutomaton(deviceAgent, deviceId);
        TextToSpeachAutomaton ttsa = new TextToSpeachAutomaton(deviceAgent, deviceId, "Hello");
        DoNothing finish = (DoNothing) new DoNothing(deviceAgent, "DeviceIdle")
                .setFinishCondition(new TimerFinishedCondition(0, 0, 5));
        
        FSM fsm = new FSM(deviceAgent);
        
        fsm.registerStartState(idle);
        fsm.registerTransition(idle, vda);
        fsm.registerTransition(vda, ttsa);
        fsm.registerTransition(ttsa, finish);
        fsm.registerFinalState(finish);
        
                
        deviceAgent.setAutomaton(fsm);
        agentsConfig.add(deviceAgent);*/
    }

    private void createDeviceAgent(String deviceId, AgentConfigurator agentsConfig, FSMProgramBehavior... behaviors) {
        DeviceAgent deviceAgent = new DeviceAgent(deviceId);
        ParallelAutomaton pa = new ParallelAutomaton(deviceAgent, "ParallelAutomaton");
        
        pa.addTransition(new FSM(deviceAgent,0, "FSMVibrate1"), true);
        pa.addTransition(new FSM(deviceAgent,0, "FSMVibrate1"), true);

        deviceAgent.setAutomaton(pa);
        agentsConfig.add(deviceAgent);
    }
    @Override
    public String getTittle() {
        return "PHAT-" + getClass().getSimpleName();
    }

    @Override
    public String getDescription() {
        return "MainGUIPHATSimulation";
    }
}
