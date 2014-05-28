package phat.agents;

import java.util.Hashtable;
import java.util.Vector;

import com.jme3.math.Vector3f;
import java.util.ArrayList;
import java.util.List;

import phat.PHATInterface;
import phat.agents.automaton.Automaton;
import phat.agents.events.PHATEvent;
import phat.agents.events.PHATEventManager;
import phat.body.BodiesAppState;
import phat.commands.PHATCommand;
import phat.world.PHATCalendar;

public abstract class Agent implements PHATAgentTick {

    protected Automaton automaton;
    boolean init = false;
    AgentsAppState agentsAppState;
    private Hashtable<String, Vector3f> listened = new Hashtable<String, Vector3f>();
    private static Vector<Agent> instances = new Vector<Agent>();
    private String bodyId;
    PHATEventManager eventManager;

    abstract protected void initAutomaton();

    public static void shout(String word, Vector3f location) {
        for (Agent instance : instances) {
            instance.listened(word, location);
        }
    }

    public String getId() {
        return bodyId;
    }

    public Agent(String bodyId) {
        this.bodyId = bodyId;
        instances.add(this);
        eventManager = new PHATEventManager(this);
    }

    protected Vector3f haveIHeard(String word) {
        Vector<String> candidates = new Vector<String>();
        for (String c : listened.keySet()) {
            if (c.toLowerCase().indexOf(word.toLowerCase()) >= 0) {
                candidates.add(c);
            }
        }
        if (candidates.isEmpty()) {
            return null;
        }
        return listened.get(candidates.firstElement());
    }

    public void listened(String word, Vector3f source) {
        this.listened.put(word, source);
    }

    public String getCurrentAction() {
        if (automaton != null) {
            return automaton.getCurrentAction();
        }
        return "";
    }

    public Automaton getAutomaton() {
        return automaton;
    }

    public void setAutomaton(Automaton automaton) {
        this.automaton = automaton;
    }

    @Override
    public void update(PHATInterface phatInterface) {
        if (!init) {
            initAutomaton();
            init = true;
        }
        if(getId().equals("E3Cleaner") && getCurrentAction().startsWith("DoNothing")) {
        	System.out.println(getId()+": "+getCurrentAction());
        }
        if(eventManager.areEvents()) {
            eventManager.process(phatInterface);
        }
        if (automaton != null) {
        	//System.out.println(bodyId+": "+automaton.getCurrentAction());
            automaton.nextState(phatInterface);
        }
    }

    public Vector3f getLocation() {
        return agentsAppState.getBodiesAppState().getLocation(bodyId);
    }

    public void runCommand(PHATCommand command) {
    	agentsAppState.getBodiesAppState().runCommand(command);
    }

    public PHATCalendar getTime() {
        return agentsAppState.getBodiesAppState().getTime();
    }

    public void setAgentsAppState(AgentsAppState agentsAppState) {
        this.agentsAppState = agentsAppState;
    }
    
    public AgentsAppState getAgentsAppState() {
    	return agentsAppState;
    }
    
    public PHATEventManager getEventManager() {
        return eventManager;
    }
    
    public boolean isInTheWorld() {
        return agentsAppState.getBodiesAppState().isBodyInTheWorld(bodyId);
    }
    
    public BodiesAppState getBodiesAppState() {
        return agentsAppState.getBodiesAppState();
    }
}