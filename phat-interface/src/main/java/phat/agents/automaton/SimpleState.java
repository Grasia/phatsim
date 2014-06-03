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
            notifityPreInitToListeners();
            initState(phatInterface);
            init = true;
            notifityPostInitToListeners();
        }
        if(isFinished(phatInterface))
            return;
        simpleNextState(phatInterface);
    }
    
    public abstract void simpleNextState(PHATInterface phatInterface);

}
