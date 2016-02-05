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
package phat.agents.automaton.conditions;

import java.util.ArrayList;
import java.util.List;
import phat.agents.Agent;
import phat.agents.automaton.Automaton;

public class CompositeAndCondition implements AutomatonCondition {

    List<AutomatonCondition> conditions = new ArrayList<>();

    public CompositeAndCondition(AutomatonCondition ... conds) {
        for(AutomatonCondition ac: conds) {
            conditions.add(ac);
        }
    }

    @Override
    public boolean evaluate(Agent agent) {
        for(AutomatonCondition ac: conditions) {
            if(!ac.evaluate(agent)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void automatonInterrupted(Automaton automaton) {
        for(AutomatonCondition ac: conditions) {
            ac.automatonInterrupted(automaton);
        }
    }

    @Override
    public void automatonResumed(Automaton automaton) {
        for(AutomatonCondition ac: conditions) {
            ac.automatonResumed(automaton);
        }
    }
    
    @Override
    public void automatonReset(Automaton automaton) {
        for(AutomatonCondition ac: conditions) {
            ac.automatonReset(automaton);
        }
    }
    
    public CompositeAndCondition add(AutomatonCondition ac) {
        conditions.add(ac);
        return this;
    }
    
    @Override
    public String toString() {
        String result = "CompositeAndCondition(";
        
        for(int i = 0; i < conditions.size(); i++) {
            AutomatonCondition ac = conditions.get(i);
            result+=ac.toString();
            if(i < conditions.size()-1) {
                result += ",";
            }
        }
        
        result += ")";
        return result;
    }
}
