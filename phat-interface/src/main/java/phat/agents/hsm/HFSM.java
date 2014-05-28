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
public abstract class HFSM {
    public enum State {Initiation, Running, Paused, Restarted, Finished}
    public enum Result {Undeterminated, Fail, Success}
    
    State state = State.Initiation;
    Result result = Result.Undeterminated;
    
    HFSM parent = null;
    
    public void proccess() {
        switch(state) {
            case Finished:
                finished();
                break;
            case Initiation:
                init();
                break;
            case Running:
                run();
                break;
            case Paused:
                pause();
                break;
            case Restarted:
                restarted();
                break;
        }
    }
    
    public abstract void init();
    
    public abstract void run();
    
    public abstract void pause();
    
    public abstract void restarted();
    
    public abstract void finished();
    
    public State getState() {
        return state;
    }
    
    public void setState(State state) {
        this.state = state;
    }
    
    public Result getResult() {
        return result;
    }
    
    public HFSM getParent() {
        return parent;
    }
}
