/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
