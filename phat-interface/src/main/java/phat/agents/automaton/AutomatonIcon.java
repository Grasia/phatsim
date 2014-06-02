/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
        iconMapping.put("DoNothing", "Textures/SociaalmlImages/behaviour/tasks/Wait.png");
        iconMapping.put("FallAutomaton", "Textures/SociaalmlImages/behaviour/tasks/Fall.png");
        iconMapping.put("GoIntoBedAutomaton", "Textures/SociaalmlImages/behaviour/tasks/GoIntoBed.png");
        iconMapping.put("MoveToLazyLocation", "Textures/SociaalmlImages/behaviour/tasks/GoToTask.png");
        iconMapping.put("MoveToSpace", "Textures/SociaalmlImages/behaviour/tasks/GoToTask.png");
        //iconMapping.put("PlayAnimation", "Textures/SociaalmlImages/behaviour/tasks/Drink.png");
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
    }
    
    @Override
    public void automatonFinished(Automaton automaton, boolean isSuccessful) {
        String iconPath = iconMapping.get(automaton.getClass().getSimpleName());
        System.out.println("automatonFinished("+automaton.getClass().getSimpleName()+","+automaton.getName()+")");
        if(iconPath != null) {
            automaton.getAgent().runCommand(new AttachIconCommand(automaton.getAgent().getId(), iconPath, false));
        }
    }

    @Override
    public void nextAutomaton(Automaton previousAutomaton, Automaton nextAutomaton) {
        //previousAutomaton.removeListener(this);
        System.out.println("nextAutomaton("+previousAutomaton.getClass().getSimpleName()+","+ nextAutomaton.getClass().getSimpleName()+","+nextAutomaton.getName()+")");
        nextAutomaton.addListener(this);
    }

    @Override
    public void preInit(Automaton automaton) {
        
    }

    @Override
    public void postInit(Automaton automaton) {
        String iconPath = iconMapping.get(automaton.getClass().getSimpleName());
        System.out.println("automatonInitialized("+automaton.getClass().getSimpleName()+","+automaton.getName()+")");
        if(iconPath != null) {
            System.out.println("\tIN!");
            automaton.getAgent().runCommand(new AttachIconCommand(automaton.getAgent().getId(), iconPath, true));
        }
    }
    
}
