/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.agents.automaton;

import phat.PHATInterface;
import phat.agents.Agent;

/**
 *
 * @author pablo
 */
public class SwitchLight extends SimpleState {
    private String roomName;
    private boolean on;
    private boolean done;
    
    public SwitchLight( Agent agent, int priority, String name, String roomName, boolean on) {
        super(agent, priority, name);
        this.roomName = roomName;
        this.on = on;
    }
    
    @Override
    public boolean isFinished(PHATInterface phatInterface) {
        return done;
    }

    @Override
    public void simpleNextState(PHATInterface phatInterface) {
        //phatInterface.getMasonAppState().getHouseAdapter().switchLight(roomName, on);
    }

    @Override
    public void initState(PHATInterface phatInterface) {
        this.done = false;
    }
}
