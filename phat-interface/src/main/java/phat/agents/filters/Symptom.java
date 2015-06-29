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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import phat.agents.Agent;
import phat.agents.automaton.Automaton;
import phat.agents.filters.types.Filter;

/**
 *
 * @author pablo
 */
public class Symptom {

    DiseaseManager diseaseManager;

    public enum Level {
        NONE, LOW, MEDIUM, HIGH
    };
    Map<Level, List<Filter>> filters = new HashMap<>();
    String symptomType;
    Level currentLevel = Level.NONE;
    SymptomEvolution symptomEvolution;

    public Symptom(String symptomType) {
        this.symptomType = symptomType;
    }

    public void add(Level level, Filter filter) {
        List<Filter> filterList = filters.get(level);
        if (filterList == null) {
            filterList = new ArrayList<>();
            filters.put(level, filterList);
        }
        filterList.add(filter);
    }

    public Automaton processFilters(Agent agent, Automaton automaton) {
        Automaton result = automaton;
        List<Filter> levelFilters = filters.get(currentLevel);
        if (levelFilters != null) {
            for (Filter filter : levelFilters) {
                result = filter.process(agent, result);
            }
        }
        return result;
    }

    public Level getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(Level currentLevel) {
        if (this.currentLevel.equals(currentLevel)) {
            return;
        }
        this.currentLevel = currentLevel;
    }

    public String getSymptomType() {
        return symptomType;
    }

    public void setDiseaseManager(DiseaseManager diseaseManager) {
        this.diseaseManager = diseaseManager;
    }

    public SymptomEvolution getSymptomEvolution() {
        return symptomEvolution;
    }

    public void setSymptomEvolution(SymptomEvolution symptomEvolution) {
        this.symptomEvolution = symptomEvolution;
    }
}
