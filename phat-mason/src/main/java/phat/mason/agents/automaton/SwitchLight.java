/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.mason.agents.automaton;

import phat.mason.PHATSimState;
import phat.mason.agents.Agent;
import sim.engine.SimState;

/**
 *
 * @author pablo
 */
public class SwitchLight extends SimpleState {
    private String roomName;
    private boolean on;
    private boolean done;
    
    public SwitchLight( Agent agent, int priority, int duration, String name, String roomName, boolean on) {
        super(agent, priority, duration, name);
        this.roomName = roomName;
        this.on = on;
        this.done = false;
    }
    
    @Override
    public void nextState(SimState state) {
        PHATSimState phatSimState = (PHATSimState)state;
        
        System.out.println("SwitchLight on!!!!!!!!!!!");
        phatSimState.getMasonAppState().getHouseAdapter().switchLight(roomName, on);
        
        done = true;
    }
    
    @Override
    public boolean isFinished(SimState simState) {
        return done;
    }
}
