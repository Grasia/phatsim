/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.agents.automaton;

import phat.PHATInterface;
import phat.agents.Agent;
import phat.agents.automaton.uses.UseObjectAutomatonFactory;

/**
 *
 * @author pablo
 */
public class UseObjectAutomaton extends SimpleState {
    String objToBeUsedId;
    Automaton indirectAutomaton;
    
    public UseObjectAutomaton(Agent agent, String objToBeUsedId) {
        super(agent, 0, "UseObjectAutomaton-"+objToBeUsedId);
        this.objToBeUsedId = objToBeUsedId;
    }
    
    
    @Override
    public boolean isFinished(PHATInterface phatInterface) {
        return indirectAutomaton != null && indirectAutomaton.isFinished(phatInterface);
    }

    @Override
    public void initState(PHATInterface phatInterface) {
        indirectAutomaton = UseObjectAutomatonFactory.getAutomaton(agent, objToBeUsedId);
        indirectAutomaton.setFinishCondition(finishCondition);
    }

    @Override
    public void simpleNextState(PHATInterface phatInterface) {
        indirectAutomaton.nextState(phatInterface);
    }
    
    @Override
    public void interrupt() {
        indirectAutomaton.interrupt();
    }
}
