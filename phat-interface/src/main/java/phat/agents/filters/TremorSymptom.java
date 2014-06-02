/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.agents.filters;

import phat.agents.Agent;
import static phat.agents.filters.Symptom.Level.High;
import static phat.agents.filters.Symptom.Level.Low;
import static phat.agents.filters.Symptom.Level.Medium;
import static phat.agents.filters.Symptom.Level.None;
import phat.body.commands.TremblingHandCommand;
import phat.body.commands.TremblingHeadCommand;

/**
 *
 * @author pablo
 */
public class TremorSymptom extends Symptom {
    
    public TremorSymptom(String symptomType) {
        super(symptomType);
    }
    
    @Override
    public void setCurrentLevel(Level currentLevel) {
        if (this.currentLevel.equals(currentLevel)) {
            return;
        }
        this.currentLevel = currentLevel;
        Agent agent = diseaseManager.getAgent();
        switch (this.currentLevel) {
            case None:
                agent.runCommand(new TremblingHeadCommand(agent.getId(), true));
                agent.runCommand(new TremblingHandCommand(agent.getId(), false, true));
                agent.runCommand(new TremblingHandCommand(agent.getId(), false, false));
                break;
            case Low:
                agent.runCommand(new TremblingHandCommand(agent.getId(), true, false));
                break;
            case Medium:
                agent.runCommand(new TremblingHeadCommand(agent.getId(), true));
                agent.runCommand(new TremblingHandCommand(agent.getId(), true, false));
                break;
            case High:
                TremblingHeadCommand head = new TremblingHeadCommand(agent.getId(), true);
                head.setAngular(new Float(Math.PI*0.5f));
                agent.runCommand(head);
                agent.runCommand(new TremblingHandCommand(agent.getId(), true, true));
                agent.runCommand(new TremblingHandCommand(agent.getId(), true, false));
                break;
        }
    }
    
}
