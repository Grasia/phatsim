/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.agents.filters.types;

import phat.agents.Agent;
import phat.agents.automaton.Automaton;
import phat.agents.automaton.conditions.AutomatonCondition;

/**
 *
 * @author pablo
 */
public abstract class Filter {
    public static String FILTER_TAG = "FILTER_FLAG";
    AutomatonCondition condition;
    Filter nextFilter;
    Filter alterrnativeFilter;
    
    public Automaton process(Agent agent, Automaton automaton) {        
        System.out.println("Process Filter "+getClass().getSimpleName());
        Automaton result = automaton;
        if(checkCondition(agent, automaton)) {
            System.out.println("condition ok");
            System.out.println("applying...");
            result = apply(agent, automaton);
            if(nextFilter != null) {
                System.out.println("NextFilter...");
                return nextFilter.process(agent, result);
            }
        } else if(alterrnativeFilter != null) {
            return alterrnativeFilter.process(agent, automaton);
        }
        return result;
    }
    
    public static boolean hasBeenFiltered(Automaton automaton) {
        return automaton.getMetadata(FILTER_TAG) != null;
    }
    
    public static void markFiltered(Automaton automaton) {
        automaton.setMetadata(FILTER_TAG, "1");
    }
    
    public boolean checkCondition(Agent agent, Automaton automaton) {
        if(condition == null)
            return true;
        return condition.evaluate(agent);
    }

    public void setCondition(AutomatonCondition condition) {
        this.condition = condition;
    }
    
    public abstract Automaton apply(Agent agent, Automaton automaton);

    public void setNextFilter(Filter nextFilter) {
        this.nextFilter = nextFilter;
    }

    public void setAlterrnativeFilter(Filter alterrnativeFilter) {
        this.alterrnativeFilter = alterrnativeFilter;
    }
}
