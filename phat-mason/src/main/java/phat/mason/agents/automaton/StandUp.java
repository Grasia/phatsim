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
public class StandUp extends SimpleState {
    private boolean init = false;
    
    public StandUp( Agent agent, int priority, int duration, String name) {
        super(agent, priority, duration, name);
    }
    
    @Override
    public void nextState(SimState state) {
        if(!init) {
            agent.getPhysicsActor().standUp();
            init = true;
        }
    }
    
    @Override
    public boolean isFinished(SimState state) {
        return init;
    }
}
