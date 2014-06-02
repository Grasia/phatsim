/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.agents.filters.types;

import phat.agents.Agent;
import phat.agents.automaton.Automaton;

/**
 *
 * @author pablo
 */
public class ReplaceTaskFilter extends Filter {
    Automaton task;
    
    @Override
    public Automaton apply(Agent agent, Automaton automaton) {
        return task;
    }

    public ReplaceTaskFilter setTask(Automaton task) {
        this.task = task;
        return this;
    }
}
