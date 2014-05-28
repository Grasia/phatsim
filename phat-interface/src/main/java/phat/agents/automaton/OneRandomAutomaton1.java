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
public class OneRandomAutomaton1 extends Automaton {

    public OneRandomAutomaton1(Agent agent) {
        super(agent);
    }
    
    @Override
    protected Automaton nextAutomaton(
			Automaton stateToBeReplaced, PHATInterface phatInterface) {
        Automaton automaton = null;
        int size = pendingTransitions.size();
        if(stateToBeReplaced == null && size > 0) {
            int randomIndex = phatInterface.getRandom().nextInt(size);
            automaton = pendingTransitions.get(randomIndex);
            pendingTransitions.clear();
        }
        return automaton;
    }
    
    @Override
    public void initState(PHATInterface phatInterface) {
        
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
