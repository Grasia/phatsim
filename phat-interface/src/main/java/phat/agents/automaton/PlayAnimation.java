/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.agents.automaton;

import phat.PHATInterface;
import phat.agents.Agent;
import phat.body.commands.PlayBodyAnimationCommand;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;

/**
 *
 * @author pablo
 */
public class PlayAnimation extends SimpleState implements PHATCommandListener {

    PlayBodyAnimationCommand playBodyAnimationCommand;
    boolean animFinished = false;

    public PlayAnimation(Agent agent, int priority, String name, String animationName) {
        super(agent, priority, name);

        playBodyAnimationCommand = new PlayBodyAnimationCommand(agent.getId(), animationName, this);
    }

    @Override
    public boolean isFinished(PHATInterface phatInterface) {
        return super.isFinished(phatInterface) || animFinished;
    }

    @Override
    public void commandStateChanged(PHATCommand command) {
        if (command == playBodyAnimationCommand
                && command.getState().equals(PHATCommand.State.Success)) {
            animFinished = true;
        }
    }

    @Override
    public void simpleNextState(PHATInterface phatInterface) {
        
    }

    @Override
    public void initState(PHATInterface phatInterface) {
        agent.runCommand(playBodyAnimationCommand);
    }
}
