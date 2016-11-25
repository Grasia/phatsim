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
package phat.agents.automaton;

import java.util.HashMap;
import java.util.Map;
import phat.body.commands.AttachIconCommand;

/**
 *
 * @author pablo
 */
public class AutomatonIcon implements AutomatonListener {   
    Map<String, String> iconMapping = new HashMap<>();
    
    public AutomatonIcon() {
        init();
    }
    
    private void init() {
        iconMapping.put("DrinkAutomaton", "images/behaviour/tasks/Drink.png");
        iconMapping.put("EatAnimation", "images/behaviour/tasks/Eat.png");
        iconMapping.put("DoNothing", "images/behaviour/tasks/Wait.png");
        iconMapping.put("SleepAutomaton", "images/behaviour/tasks/FallSleepTask.png");
        iconMapping.put("FallAutomaton", "images/behaviour/tasks/Fall.png");
        iconMapping.put("OpenObjectAutomaton", "images/behaviour/tasks/Open.png");
        iconMapping.put("CloseObjectAutomaton", "imagesbehaviour/tasks/Close.png");
        iconMapping.put("GoIntoBedAutomaton", "images/behaviour/tasks/GoIntoBed.png");
        iconMapping.put("MoveToLazyLocation", "images/behaviour/tasks/GoToTask.png");
        iconMapping.put("MoveToBodyLocAutomaton", "images/behaviour/tasks/GoToBodyLoc.png");
        iconMapping.put("MoveToSpace", "images/behaviour/tasks/GoToTask.png");
        iconMapping.put("SayAutomaton", "images/behaviour/tasks/Say.png");
        iconMapping.put("SitDownAutomaton", "images/behaviour/tasks/SitDown.png");
        iconMapping.put("StandUpAutomaton", "images/behaviour/tasks/StandUp.png");
        iconMapping.put("UseObjectAutomaton", "images/behaviour/tasks/UseTask.png");
        iconMapping.put("HaveAShowerAutomaton", "images/behaviour/tasks/UseTask.png");
        iconMapping.put("UseCommonObjectAutomaton", "images/behaviour/tasks/UseTask.png");
        iconMapping.put("UseDoorbellAutomaton", "images/behaviour/tasks/UseTask.png");
        iconMapping.put("UseWCAutomaton", "images/behaviour/tasks/UseTask.png");
        iconMapping.put("PutOnClothingAutomaton", "images/behaviour/tasks/PutOn.png");
        iconMapping.put("TakeOffClothingAutomaton", "images/behaviour/tasks/TakeOff.png");
        iconMapping.put("PressOnScreenXYAutomaton", "images/behaviour/tasks/TapXY.png");
        iconMapping.put("WaitForCloseToBodyAutomaton", "images/behaviour/tasks/WaitForBodyClose.png");
    }
    
    @Override
    public void stateChanged(Automaton automaton, Automaton.STATE state) {
        switch(state) {
            case FINISHED:
            case INTERRUPTED:
                removeIcon(automaton);
                break;
            case STARTED:
                addIcon(automaton);
                break;
        }
    }
    
    private void removeIcon(Automaton automaton) {
        String iconPath = iconMapping.get(automaton.getClass().getSimpleName());
        if(iconPath != null) {
            automaton.getAgent().runCommand(new AttachIconCommand(automaton.getAgent().getId(), iconPath, false));
        }
    }

    private void addIcon(Automaton automaton) {
        String iconPath = iconMapping.get(automaton.getClass().getSimpleName());
        if(iconPath != null) {
            automaton.getAgent().runCommand(new AttachIconCommand(automaton.getAgent().getId(), iconPath, true));
        }
    }
}
