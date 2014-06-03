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

import java.util.HashMap;
import java.util.Map;
import phat.agents.Agent;
import phat.agents.automaton.Automaton;
import phat.agents.automaton.AutomatonListener;
import phat.agents.filters.types.Filter;
import phat.body.commands.SetStoopedBodyCommand;

/**
 *
 * @author pablo
 */
public class DiseaseManager implements AutomatonListener {

    Agent agent;
    Map<String, Symptom> symptomMap = new HashMap<>();
    String stage;

    public DiseaseManager(Agent agent) {
        this.agent = agent;
        agent.runCommand(new SetStoopedBodyCommand(agent.getId(), true));
    }

    public void add(Symptom symptom) {
        symptom.setDiseaseManager(this);
        symptomMap.put(symptom.getSymptomType(), symptom);
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public Agent getAgent() {
        return agent;
    }
    
    @Override
    public void preInit(Automaton automaton) {
        if (!Filter.hasBeenFiltered(automaton)) {
            Automaton alternative = automaton;
            for (Symptom symptom : symptomMap.values()) {
                System.out.println("Symptom = " + symptom.getSymptomType() + ": " + symptom.getCurrentLevel());
                alternative = symptom.processFilters(agent, automaton);
                System.out.println("Alternative = " + alternative);
                if (!alternative.equals(automaton)) {
                    automaton.getParent().replaceCurrentAutomaton(alternative);
                    automaton = alternative;
                }
            }
            Filter.markFiltered(automaton);
        }
    }

    @Override
    public void nextAutomaton(Automaton previousAutomaton, Automaton nextAutomaton) {
        nextAutomaton.addListener(this);
    }

    @Override
    public void automatonFinished(Automaton automaton, boolean isSuccessful) {
    }

    @Override
    public void postInit(Automaton automaton) {
    }
}
