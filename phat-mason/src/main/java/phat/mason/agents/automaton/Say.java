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
public class Say extends SimpleState {    
    private boolean done = false;
    private String text;
    private float volume;
    
    public Say(Agent agent, int priority, int duration, String name, String text, float volume) {
        super(agent, priority, duration, name);
        this.text = text;
        this.volume = volume;
    }
    
    @Override
    public void nextState(SimState state) {
        if(!done) {
            Agent.shout(text, agent.getPhysicsActor().getLocation());
            agent.getPhysicsActor().say(text, volume);
            done = true;
        }
    }
    
    @Override
    public boolean isFinished(SimState state) {
        return done;
    }
}