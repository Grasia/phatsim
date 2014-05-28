/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.agents.hsm;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author pablo
 */
public abstract class HFSMComposite extends HFSM {
    List<HFSM> children;
    HFSM currentState = null;

    @Override
    public void init() {
        children = new ArrayList<>();
        currentState = nextHFSM(currentState);
        setState(State.Running);
    }

    @Override
    public void run() {
        
    }

    @Override
    public void pause() {
        
    }

    @Override
    public void restarted() {
        
    }

    @Override
    public void finished() {
        
    }
    
    public abstract HFSM nextHFSM(HFSM currentState);
}
