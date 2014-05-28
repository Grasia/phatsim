/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package phat.agents.automaton;

import java.util.ArrayList;

import phat.PHATInterface;
import phat.agents.Agent;

/**
 * Estados que no tienen un autómata asociado. Es decir, autómatas más bajos de la jerarquía.
 * En ellos getDefaultState y createNewTransitions devuelven null. Por otro lado, nextState
 * debe ser redefinido.
  * @author Juan A. Botía, Pablo Campillo, Francisco Campuzano, and Emilio Serrano
 */
public abstract class SimpleState extends Automaton{

    public SimpleState(Agent agent, int priority, String name){
        super(agent, priority, name );
    }

    @Override
    public Automaton getDefaultState(PHATInterface phatInterface) {
       return null;

    }

    @Override
    public ArrayList<Automaton> createNewTransitions(PHATInterface phatInterface) {
        return null;
    }

    /**
     * Se obliga a implementar un nextState ya que en automaton depende del autómata subordinado.
     * @param state
     */
    @Override
    public void nextState(PHATInterface phatInterface) {
        if (!init) {
            initState(phatInterface);
            init = true;
            notifityInitializedListeners();
        }
        if(isFinished(phatInterface))
            return;
        simpleNextState(phatInterface);
    }
    
    public abstract void simpleNextState(PHATInterface phatInterface);

}
