/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package phat.mason.agents.automaton;

import java.util.ArrayList;
import phat.mason.agents.Agent;
import sim.engine.SimState;

/**
 * Estados que no tienen un autómata asociado. Es decir, autómatas más bajos de la jerarquía.
 * En ellos getDefaultState y createNewTransitions devuelven null. Por otro lado, nextState
 * debe ser redefinido.
  * @author Juan A. Botía, Pablo Campillo, Francisco Campuzano, and Emilio Serrano
 */
public abstract class  SimpleState extends Automaton{

    public SimpleState(Agent agent, int priority, int duration, String name){
        super(agent,priority, duration, name );
    }

    @Override
    public Automaton getDefaultState(SimState simState) {
       return null;

    }

    @Override
    public ArrayList<Automaton> createNewTransitions(SimState simState) {
        return null;
    }

    /**
     * Se obliga a implementar un nextState ya que en automaton depende del autómata subordinado.
     * @param state
     */
    @Override
    public abstract void nextState(SimState state);

}
