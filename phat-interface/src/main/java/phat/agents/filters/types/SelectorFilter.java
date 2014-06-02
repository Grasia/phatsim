/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.agents.filters.types;

import java.util.ArrayList;
import java.util.List;
import phat.agents.Agent;
import phat.agents.automaton.Automaton;

/**
 *
 * @author pablo
 */
public class SelectorFilter extends Filter {
    List<String> taskTypes = new ArrayList<>();
    
    @Override
    public boolean checkCondition(Agent agent, Automaton automaton) {
        boolean condition = super.checkCondition(agent, automaton);
        
        if(taskTypes.isEmpty()) {
            return condition;
        } 
        
        String entityType = automaton.getMetadata("SOCIAALML_ENTITY_TYPE");
        String entityID = automaton.getMetadata("SOCIAALML_ENTITY_ID");
        if(entityID != null && taskTypes.contains(entityID)) {
            return condition;
        }
        return false;
    }

    @Override
    public Automaton apply(Agent agent, Automaton automaton) {
        return automaton;
    }
    
    public void add(String taskType) {
        taskTypes.add(taskType);
    }
}
