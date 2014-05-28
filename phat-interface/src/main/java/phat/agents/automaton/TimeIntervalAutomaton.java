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
public abstract class TimeIntervalAutomaton extends Automaton {

    public TimeIntervalAutomaton(Agent agent, String name) {
        super(agent, 0, name);
    }
    
    public abstract void initSubAutomaton();

    @Override
    public void initState(PHATInterface phatInterface) {
        initSubAutomaton();
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
