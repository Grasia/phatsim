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
public abstract class SeqTaskAutomaton extends Automaton {
	
    private boolean init = false;

    public SeqTaskAutomaton(Agent agent, String name) {
        super(agent, 0, name);
    }
    
    public abstract void initTasks();
    
    @Override
    public void initState(PHATInterface phatInterface) {
        initTasks();
    }

    @Override
    public Automaton getDefaultState(PHATInterface phatInterface) {
        return null;
    }

    @Override
    public ArrayList<Automaton> createNewTransitions(PHATInterface phatInterface) {
        return null;
    }
}
