/*
 * Copyright (C) 2014 Pablo Campillo-Sanchez <pabcampi@ucm.es>
 *
 * This software has been developed as part of the 
 * SociAAL project directed by Jorge J. Gomez Sanz
 * (http://grasia.fdi.ucm.es/sociaal)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
