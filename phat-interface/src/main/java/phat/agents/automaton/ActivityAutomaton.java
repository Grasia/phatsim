/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.agents.automaton;

import java.util.ArrayList;
import phat.PHATInterface;
import phat.agents.Agent;

/**
 *
 * @author pablo
 */
public abstract class ActivityAutomaton extends Automaton {

    public ActivityAutomaton(Agent agent, String name) {
        super(agent, 0, name);
    }
    
    @Override
    public void initState(PHATInterface phatInterface) {
        initTasks();
    }
    
    public abstract void initTasks();

    @Override
    public Automaton getDefaultState(PHATInterface phatInterface) {
        return null;
    }

    @Override
    public ArrayList<Automaton> createNewTransitions(PHATInterface phatInterface) {
        return null;
    }
}
