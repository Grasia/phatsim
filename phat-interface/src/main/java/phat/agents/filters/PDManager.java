/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
