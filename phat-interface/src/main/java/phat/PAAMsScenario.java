package phat;

import phat.agents.Agent;
import phat.agents.AgentImpl;
import phat.agents.automaton.Automaton;
import phat.agents.automaton.AutomatonIcon;
import phat.agents.automaton.DoNothing;
import phat.agents.automaton.DrinkAutomaton;
import phat.agents.automaton.FSM;
import phat.agents.automaton.FallAutomaton;
import phat.agents.automaton.GoIntoBedAutomaton;
import phat.agents.automaton.MainAutomaton;
import phat.agents.automaton.MoveToBodyLocAutomaton;
import phat.agents.automaton.MoveToSpace;
import phat.agents.automaton.SayAutomaton;
import phat.agents.automaton.SitDownAutomaton;
import phat.agents.automaton.StandUpAutomaton;
import phat.agents.automaton.UseObjectAutomaton;
import phat.agents.automaton.conditions.PastTimeCondition;
import phat.agents.automaton.conditions.ProbCondition;
import phat.agents.automaton.conditions.TimerFinishedCondition;
import phat.agents.automaton.uses.UseDoorbellAutomaton;
import phat.agents.filters.DiseaseManager;
import phat.agents.filters.PDManager;
import phat.agents.filters.Symptom;
import phat.agents.filters.types.PlaceToGoFilter;
import phat.agents.filters.types.ReplaceTaskFilter;
import phat.agents.filters.types.SelectorFilter;
import phat.body.BodiesAppState;
import phat.body.commands.BodyLabelCommand;
import phat.body.commands.SetBodyHeightCommand;
import phat.body.commands.SetCameraToBodyCommand;
import phat.body.commands.SetPCListenerToBodyCommand;
import phat.body.commands.SetStoopedBodyCommand;
import phat.body.commands.TremblingHandCommand;
import phat.body.commands.TremblingHeadCommand;
import phat.config.AgentConfigurator;
import phat.config.BodyConfigurator;
import phat.config.DeviceConfigurator;
import phat.config.HouseConfigurator;
import phat.config.WorldConfigurator;
import phat.devices.commands.CreateSmartphoneCommand;
import phat.devices.commands.DisplayAVDScreenCommand;
import phat.devices.commands.SetAndroidEmulatorCommand;
import phat.devices.commands.SetDeviceOnPartOfBodyCommand;
import phat.devices.commands.StartActivityCommand;
import phat.structures.houses.HouseFactory;
import phat.world.WorldAppState;

/**
 *
 * @author pablo
 */
public class PAAMsScenario implements PHATInitializer {

    public static void main(String[] args) {
        PAAMsScenario sim = new PAAMsScenario();
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

    String relative = "Relative";
    String patient = "Patient";
    
    @Override
    public void initBodies(BodyConfigurator bodyConfig) {
        bodyConfig.createBody(BodiesAppState.BodyType.ElderLP, patient);        
        bodyConfig.setInSpace(patient, "House1", "Hall");
        //bodyConfig.runCommand(new BodyLabelCommand("Relative", true));
        /*SetCameraToBodyCommand setCameraToBodyCommand = new SetCameraToBodyCommand("Relative");
        setCameraToBodyCommand.setFront(true);
        setCameraToBodyCommand.setDistance(3f);
        setCameraToBodyCommand.setHeight(15f);
        bodyConfig.runCommand(setCameraToBodyCommand);*/
        bodyConfig.runCommand(new SetPCListenerToBodyCommand(patient));
        bodyConfig.runCommand(new SetBodyHeightCommand(patient, 1.7f));
        
        bodyConfig.createBody(BodiesAppState.BodyType.Young, relative);        
        bodyConfig.setInSpace(relative, "House1", "Kitchen");
        
        bodyConfig.runCommand(new SetBodyHeightCommand(relative, 1.8f));
    }
    
    @Override
    public void initDevices(DeviceConfigurator deviceConfig) {
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
    public void initAgents(AgentConfigurator agentsConfig) {
        Agent patientAgent = new AgentImpl(patient);
        agentsConfig.add(patientAgent);
        Automaton mainAutomaton = new MainAutomaton(patientAgent);
        MoveToSpace livingRoom = new MoveToSpace(patientAgent, "MoveToLivingRoom","LivingRoom");
        livingRoom.setMetadata("SOCIAALML_ENTITY_TYPE", "MoveToSpace");
        mainAutomaton.addTransition(livingRoom, true);
        mainAutomaton.addListener(new AutomatonIcon());
        patientAgent.setAutomaton(mainAutomaton);
        
        DiseaseManager dm = new PDManager(patientAgent);
        
        Agent relativeAgent = new AgentImpl(relative);
        MoveToBodyLocAutomaton move = new MoveToBodyLocAutomaton(relativeAgent, "MoveToPatient", patient);
        move.addListener(new AutomatonIcon());
        relativeAgent.setAutomaton(move);
        agentsConfig.add(relativeAgent);
    }

    @Override
    public String getTittle() {
        return "PHAT-"+getClass().getSimpleName();
    }
}