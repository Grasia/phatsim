/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.mason.agents;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;

import phat.mason.PHATSimState;
import phat.mason.agents.automaton.Automaton;
import phat.mason.space.Util;
import phat.structures.houses.House;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Bag;
import sim.util.Double3D;

/**
 *
 * @author Pablo
 */
public abstract class Agent implements Steppable {
    protected PhysicsActor physicsActor;
    protected Automaton automaton;
    private Hashtable<String, Double3D> listened=new Hashtable<String,Double3D>();
    private static Vector<Agent> instances=new  Vector<Agent>();
    PHATSimState state;
    
    public static void shout(String word,Double3D location){
    	for (Agent instance:instances)
    		instance.listened(word,location);
    }
    
    public Agent(PhysicsActor physicsActor, PHATSimState state) {
        this.physicsActor = physicsActor;
        physicsActor.putAgent(this);
        instances.add(this);
        this.state=state;
        initAutomaton();
    }
    
	protected Double3D haveIHeard(String word) {
		Vector<String> candidates=new Vector<String>();
		for (String c:listened.keySet()){
			if (c.toLowerCase().indexOf(word.toLowerCase())>=0)
				candidates.add(c);				
		}
		if (candidates.isEmpty())
			return null;
		return listened.get(candidates.firstElement());		
	}
	
	public void listened(String word, Double3D source){
		this.listened.put(word,source);
	}
    
    @Override
    public void step(SimState ss) {
        PHATSimState simState = (PHATSimState) ss;
        if(automaton != null) {
            automaton.nextState(simState);
        }
    }
    
    public String getName() {
        return physicsActor.getName();
    }

    public PhysicsActor getPhysicsActor() {
        return physicsActor;
    }

    public void setPhysicsActor(PhysicsActor physicsActor) {
        this.physicsActor = physicsActor;
    }

    public String getCurrentAction() {
        if(automaton != null) {
            return automaton.getCurrentState().getName();
        }
        return "";
    }
    
    abstract protected void initAutomaton();
    
    public Automaton getAutomaton() {
        return automaton;
    }

    public void setAutomaton(Automaton automaton) {
        this.automaton = automaton;
    }
    
    
    public ArrayList<Agent> getAgentInSameLocation() {
    	 
    	ArrayList<Agent>  otherAgents=new  ArrayList<Agent> ();                
        Bag bag = physicsActor.world().getObjectsAtLocation(this.getPhysicsActor().getLocation());
        for(int i = 0; i < bag.numObjs; i++) {
            if(bag.get(i) instanceof PhysicsActor) {
                PhysicsActor pa = (PhysicsActor)bag.get(i);
                if(pa.agent() != this) {
                  		otherAgents.add(pa.agent());
                }
            }
        }
        return otherAgents;
    }
    
    public ArrayList<Agent> getAgentNearThan(float distance) {
        ArrayList<Agent>  otherAgents=new  ArrayList<Agent> ();   
        Double3D loc = getPhysicsActor().getLocation();
        Bag bag = getPhysicsActor().world().getAllObjects();
        for(int i = 0; i < bag.numObjs; i++) {
            if(bag.get(i) instanceof PhysicsActor) {
                PhysicsActor pa = (PhysicsActor)bag.get(i);
                if(pa.agent() != this && loc.distance(pa.getLocation()) < distance) {
                    otherAgents.add(pa.agent());
                }
            }
        }
        return otherAgents;
    }

	private boolean sameRoom(PhysicsActor location1, PhysicsActor location2) {	
		House house = state.getMasonAppState().getHouseAdapter().getHouse();
		String room1=house.getRoomForObject(location1.getName());
		String room2=house.getRoomForObject(location2.getName());
		//Double3D loc = Util.get(masonAppState.getHouseAdapter().getHouse().getCoordenates("Kitchen", "Hob"));
		return room1.equals(room2);
	}
    
    
}
