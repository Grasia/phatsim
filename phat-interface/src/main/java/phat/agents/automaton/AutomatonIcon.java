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
        iconMapping.put("DrinkAutomaton", "Textures/SociaalmlImages/behaviour/tasks/Drink.png");
        iconMapping.put("EatAnimation", "Textures/SociaalmlImages/behaviour/tasks/Eat.png");
        iconMapping.put("DoNothing", "Textures/SociaalmlImages/behaviour/tasks/Wait.png");
        iconMapping.put("SleepAutomaton", "Textures/SociaalmlImages/behaviour/tasks/FallSleepTask.png");
        iconMapping.put("FallAutomaton", "Textures/SociaalmlImages/behaviour/tasks/Fall.png");
        iconMapping.put("OpenObjectAutomaton", "Textures/SociaalmlImages/behaviour/tasks/Open.png");
        iconMapping.put("CloseObjectAutomaton", "Textures/SociaalmlImages/behaviour/tasks/Close.png");
        iconMapping.put("GoIntoBedAutomaton", "Textures/SociaalmlImages/behaviour/tasks/GoIntoBed.png");
        iconMapping.put("MoveToLazyLocation", "Textures/SociaalmlImages/behaviour/tasks/GoToTask.png");
        iconMapping.put("MoveToBodyLocAutomaton", "Textures/SociaalmlImages/behaviour/tasks/GoToBodyLoc.png");
        iconMapping.put("MoveToSpace", "Textures/SociaalmlImages/behaviour/tasks/GoToTask.png");
        iconMapping.put("SayAutomaton", "Textures/SociaalmlImages/behaviour/tasks/Say.png");
        iconMapping.put("SitDownAutomaton", "Textures/SociaalmlImages/behaviour/tasks/SitDown.png");
        iconMapping.put("StandUpAutomaton", "Textures/SociaalmlImages/behaviour/tasks/StandUp.png");
        iconMapping.put("UseObjectAutomaton", "Textures/SociaalmlImages/behaviour/tasks/UseTask.png");
        iconMapping.put("HaveAShowerAutomaton", "Textures/SociaalmlImages/behaviour/tasks/UseTask.png");
        iconMapping.put("UseCommonObjectAutomaton", "Textures/SociaalmlImages/behaviour/tasks/UseTask.png");
        iconMapping.put("UseDoorbellAutomaton", "Textures/SociaalmlImages/behaviour/tasks/UseTask.png");
        iconMapping.put("UseWCAutomaton", "Textures/SociaalmlImages/behaviour/tasks/UseTask.png");
        iconMapping.put("PutOnClothingAutomaton", "Textures/SociaalmlImages/behaviour/tasks/PutOn.png");
        iconMapping.put("TakeOffClothingAutomaton", "Textures/SociaalmlImages/behaviour/tasks/TakeOff.png");
        iconMapping.put("PressOnScreenXYAutomaton", "Textures/SociaalmlImages/behaviour/tasks/TapXY.png");
        iconMapping.put("WaitForCloseToBodyAutomaton", "Textures/SociaalmlImages/behaviour/tasks/WaitForBodyClose.png");
    }
    
    @Override
    public void automatonFinished(Automaton automaton, boolean isSuccessful) {
        removeIcon(automaton);
    }

    @Override
    public void automatonInterrupted(Automaton automaton) {
        removeIcon(automaton);
    }
    
    private void removeIcon(Automaton automaton) {
        String iconPath = iconMapping.get(automaton.getClass().getSimpleName());
        if(iconPath != null) {
            automaton.getAgent().runCommand(new AttachIconCommand(automaton.getAgent().getId(), iconPath, false));
        }
    }
    
    @Override
    public void nextAutomaton(Automaton previousAutomaton, Automaton nextAutomaton) {
        nextAutomaton.addListener(this);
    }

    @Override
    public void preInit(Automaton automaton) {
        
    }

    @Override
    public void postInit(Automaton automaton) {
        addIcon(automaton);
    }

    @Override
    public void automatonResumed(Automaton resumedAutomaton) {
        addIcon(resumedAutomaton);
    }

    private void addIcon(Automaton automaton) {
        String iconPath = iconMapping.get(automaton.getClass().getSimpleName());
        if(iconPath != null) {
            automaton.getAgent().runCommand(new AttachIconCommand(automaton.getAgent().getId(), iconPath, true));
        }
    }
}
