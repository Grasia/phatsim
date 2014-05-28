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
public class PlayAnimation extends SimpleState {
    private boolean init = false;
    private String animationName;
    
    public PlayAnimation( Agent agent, int priority, int duration, String name, String animationName) {
        super(agent, priority, duration, name);
        this.animationName = animationName;
    }
    
    @Override
    public void nextState(SimState state) {
        System.out.println("PlayAnimation...");
        if(!init) {
            if(agent.getPhysicsActor().hasAnimation(animationName)) {                
                agent.getPhysicsActor().playAnimation(animationName);
            } else {
                System.out.println("No animation "+animationName);
            }
            init = true;
        }
    }
    
    @Override
    public boolean isFinished(SimState state) {
        return init && !animationName.equals(agent.getPhysicsActor().currentAnimName());
    }
}
