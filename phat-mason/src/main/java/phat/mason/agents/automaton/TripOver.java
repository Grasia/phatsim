/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.mason.agents.automaton;

import phat.mason.agents.Agent;
import sim.engine.SimState;

/**
 *
 * @author pablo
 */
public class TripOver extends SimpleState {
    private boolean tripOver = false;
    
    public TripOver( Agent agent, int priority, int duration, String name) {
        super(agent, priority, duration, name);
    }
    
    @Override
    public void nextState(SimState state) {
        agent.getPhysicsActor().tripOver();        
        
        tripOver = true;
    }
    
    @Override
    public boolean isFinished(SimState simState) {
        return tripOver;
    }
}
