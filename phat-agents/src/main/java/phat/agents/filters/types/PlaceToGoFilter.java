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
package phat.agents.filters.types;

import phat.agents.Agent;
import phat.agents.automaton.Automaton;
import phat.agents.automaton.MoveToSpace;

/**
 *
 * @author pablo
 */
public class PlaceToGoFilter extends Filter {
    String destiny;
    
    public PlaceToGoFilter(String destiny) {
        this.destiny = destiny;
    }
    
    @Override
    public Automaton apply(Agent agent, Automaton automaton) {
        //System.out.println("applying PlaceToGoFilter..."+automaton.getClass().getSimpleName());
        if(automaton instanceof MoveToSpace) {
            //System.out.println("destiny changed! "+destiny);
            MoveToSpace move = (MoveToSpace) automaton;
            move.setDestinyName(destiny);
        }
        return automaton;
    }

    public void setDestiny(String destiny) {
        this.destiny = destiny;
    }

    public String getDestiny() {
        return destiny;
    }
    
    
}
