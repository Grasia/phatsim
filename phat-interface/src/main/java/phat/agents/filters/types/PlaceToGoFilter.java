/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
        System.out.println("applying PlaceToGoFilter..."+automaton.getClass().getSimpleName());
        if(automaton instanceof MoveToSpace) {
            System.out.println("destiny changed! "+destiny);
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
