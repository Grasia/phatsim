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
        //System.out.println("*************************************Process Filter "+getClass().getSimpleName());
        //System.out.println("Automaton ("+automaton.getMetadata("SOCIAALML_ENTITY_ID")+")");
        Automaton result = automaton;
        if(checkCondition(agent, automaton)) {
            //System.out.println("Condition ok!");
            result = apply(agent, automaton);
            //System.out.println("result = "+result.toString());
            if(nextFilter != null) {
                //System.out.println("nextFilter...");
                return nextFilter.process(agent, result);
            }
        } else if(alterrnativeFilter != null) {
            //System.out.println("alterrnativeFilter...");
            return alterrnativeFilter.process(agent, result);
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
