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
public class PutOnClothingAutomaton  extends SimpleState {
    /**
     * Esta clase es un estado de comportamiento. Como no tiene autómata subordinado se extiende SimpleState.
     * Se le puede pasar prioridad, duración  y nombre. El nombre sirve para implementar varios estados con una misma clase.

     * @param personImplementingAutomaton
     * @param name
     */
    public PutOnClothingAutomaton(Agent agent, String name){
        super(agent,0,name);

    }

    @Override
    public void simpleNextState(PHATInterface phatInterface) {
    }
    
    @Override
    public void interrupt() {
        super.interrupt();
        setFinished(true);
    }

    @Override
    public void initState(PHATInterface phatInterface) {
    }
}
