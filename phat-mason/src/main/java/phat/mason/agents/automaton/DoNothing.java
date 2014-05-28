/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.mason.agents.automaton;

import phat.mason.agents.Agent;
import sim.engine.SimState;

/**
 *
 * @author Pablo
 */
public class DoNothing extends SimpleState {
    /**
     * Esta clase es un estado de comportamiento. Como no tiene autómata subordinado se extiende SimpleState.
     * Se le puede pasar prioridad, duración  y nombre. El nombre sirve para implementar varios estados con una misma clase.

     * @param personImplementingAutomaton
     * @param name
     */
    public DoNothing(Agent agent, int priority, int duration,  String name){
        super(agent,priority,duration,name);

    }
  /**
   * No hace nada a parte de esperar a que se acabe la duración (con -1 nunca acaba, en cuyo caso debe tener una prioridad baja
   * para que se tomen otros estados).
   * @param state
   */
    @Override
    public void nextState(SimState state){

    }
}
