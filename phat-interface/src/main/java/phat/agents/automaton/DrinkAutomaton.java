package phat.agents.automaton;

import phat.PHATInterface;
import phat.agents.Agent;
import phat.body.commands.PlayBodyAnimationCommand;
import phat.body.control.animation.BasicCharacterAnimControl;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.world.PHATCalendar;

/**
 *
 * @author pablo
 */
public class DrinkAutomaton extends SimpleState implements PHATCommandListener {

    boolean drinking = false;
    PlayBodyAnimationCommand playAnimCommand;
    PHATCalendar lastDrink = null;
    float drinkRate = 10f;
    
    public DrinkAutomaton(Agent agent) {
        super(agent, 0, "DrinkAutomaton");
    }

    @Override
    public boolean isFinished(PHATInterface phatInterface) {
        return super.isFinished(phatInterface) && !drinking;
    }
    
    @Override
    public void commandStateChanged(PHATCommand command) {
        if (command == playAnimCommand
                && (command.getState().equals(PHATCommand.State.Success) ||
                		command.getState().equals(PHATCommand.State.Fail))) {
            drinking = false;
        }
    }

    @Override
    public void simpleNextState(PHATInterface phatInterface) {
        if(!drinking) {
            int secs = (int)lastDrink.spentTimeTo(phatInterface.getSimTime());
            if(secs >= drinkRate) {
                drink(phatInterface);
            }
        }
    }

    @Override
    public void initState(PHATInterface phatInterface) {
        drink(phatInterface);
    }

    private void drink(PHATInterface phatInterface) {
        drinking = true;
        playAnimCommand = new PlayBodyAnimationCommand(agent.getId(), 
                BasicCharacterAnimControl.AnimName.DrinkStanding.name(), this);
        agent.runCommand(playAnimCommand);
        lastDrink = (PHATCalendar) phatInterface.getSimTime().clone();
    }
}
