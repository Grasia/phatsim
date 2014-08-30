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
                agent.runCommand(new TremblingHeadCommand(agent.getId(), false));
                agent.runCommand(new TremblingHandCommand(agent.getId(), false, true));
                agent.runCommand(new TremblingHandCommand(agent.getId(), false, false));
                break;
            case Low:
                agent.runCommand(new TremblingHeadCommand(agent.getId(), false));
                agent.runCommand(new TremblingHandCommand(agent.getId(), false, true));
                agent.runCommand(new TremblingHandCommand(agent.getId(), true, false));
                break;
            case Medium:
                agent.runCommand(new TremblingHeadCommand(agent.getId(), true));
                agent.runCommand(new TremblingHandCommand(agent.getId(), false, true));
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
