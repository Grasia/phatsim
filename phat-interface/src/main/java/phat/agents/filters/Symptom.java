/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.agents.filters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import phat.agents.Agent;
import phat.agents.automaton.Automaton;
import phat.agents.filters.types.Filter;
import phat.body.commands.TremblingHandCommand;
import phat.body.commands.TremblingHeadCommand;

/**
 *
 * @author pablo
 */
public class Symptom {

    DiseaseManager diseaseManager;

    public enum Level {

        None, Low, Medium, High
    };
    Map<Level, List<Filter>> filters = new HashMap<>();
    String symptomType;
    Level currentLevel = Level.None;

    public Symptom(String symptomType) {
        this.symptomType = symptomType;
    }

    public void add(Level level, Filter filter) {
        List<Filter> filterList = filters.get(level);
        if (filterList == null) {
            filterList = new ArrayList<>();
        }
        filterList.add(filter);
        filters.put(level, filterList);
    }

    public Automaton processFilters(Agent agent, Automaton automaton) {
        Automaton result = automaton;
        List<Filter> levelFilters = filters.get(currentLevel);
        System.out.println("Filters of symptom " + getSymptomType() + " = " + levelFilters);
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
}
