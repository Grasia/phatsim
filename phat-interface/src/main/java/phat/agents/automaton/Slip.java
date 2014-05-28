/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.agents.automaton;

import phat.PHATInterface;
import phat.agents.Agent;
import phat.body.commands.FallDownCommand;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;

/**
 *
 * @author pablo
 */
public class Slip extends SimpleState implements PHATCommandListener {
    private boolean slip = false;
    FallDownCommand fallDownCommand;
    
    public Slip( Agent agent, int priority, String name) {
        super(agent, priority, name);
    }
    
    @Override
    public boolean isFinished(PHATInterface phatInterface) {
        return slip;
    }
    
    @Override
    public void commandStateChanged(PHATCommand command) {
        if (command == fallDownCommand
                && command.getState().equals(PHATCommand.State.Success)) {
            slip = true;
        }
    }

    @Override
    public void simpleNextState(PHATInterface phatInterface) {
    }

    @Override
    public void initState(PHATInterface phatInterface) {
        slip = false;
        fallDownCommand = new FallDownCommand(agent.getId(), this);
        agent.runCommand(fallDownCommand);
    }
}
