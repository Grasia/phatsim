/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.agents.automaton;

import phat.PHATInterface;
import phat.agents.Agent;
import phat.body.commands.StandUpCommand;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;

/**
 *
 * @author pablo
 */
public class StandUpAutomaton extends SimpleState implements PHATCommandListener {
    StandUpCommand standUpCommand;
    boolean standUpfinished = false;
    
    public StandUpAutomaton( Agent agent, String name) {
        super(agent, 0, name);
    }
        
    @Override
    public boolean isFinished(PHATInterface phatInterface) {
        return super.isFinished(phatInterface) || standUpfinished;
    }

    @Override
    public void commandStateChanged(PHATCommand command) {
        if (command == standUpCommand
                && command.getState().equals(PHATCommand.State.Success)) {
            standUpfinished = true;
        }
    }

    @Override
    public void simpleNextState(PHATInterface phatInterface) {
        
    }

    @Override
    public void initState(PHATInterface phatInterface) {
        standUpCommand = new StandUpCommand(agent.getId(), this);
        agent.runCommand(standUpCommand);
    }
}
