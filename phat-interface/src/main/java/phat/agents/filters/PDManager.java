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
package phat.agents.filters;

import phat.agents.Agent;
import phat.agents.automaton.FallAutomaton;
import phat.agents.automaton.conditions.ProbCondition;
import phat.agents.filters.types.DelayFilter;
import phat.agents.filters.types.ReplaceTaskFilter;
import phat.agents.filters.types.SelectorFilter;

/**
 *
 * @author pablo
 */
public class PDManager extends DiseaseManager {

    public PDManager(Agent agent) {
        super(agent);
        initSymptoms();
    }
    
    private void initSymptoms() {
        Symptom equilibrium = new Symptom("Equilibrium");
        add(equilibrium);
        equilibrium.setCurrentLevel(Symptom.Level.High);
        
        SelectorFilter sf = new SelectorFilter();
        sf.add("MoveToSpace");
        
        DelayFilter delay = new DelayFilter();
        delay.setDelay(0f);
        
        ReplaceTaskFilter rtf = new ReplaceTaskFilter().setTask(new FallAutomaton(agent, "FallingTaskFilter"));
        rtf.setCondition(new ProbCondition(0f));
        sf.setNextFilter(rtf);
        
        equilibrium.add(Symptom.Level.Medium, sf);
        agent.getAutomaton().addListener(this);
    }
}
