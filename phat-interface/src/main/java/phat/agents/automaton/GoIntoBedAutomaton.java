/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.agents.automaton;

import phat.PHATInterface;
import phat.agents.Agent;
import phat.body.commands.GoIntoBedCommand;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;

/**
 *
 * @author pablo
 */
public class GoIntoBedAutomaton extends SimpleState implements PHATCommandListener {
    GoIntoBedCommand goIntoBedCommand;
    boolean finished;
    String bedId;
    
    public GoIntoBedAutomaton(Agent agent, String bedId) {
        super(agent, 0, "GoIntoBedAutomaton");
        this.bedId = bedId;
    }
    
    @Override
    public boolean isFinished(PHATInterface phatInterface) {
        return super.isFinished(phatInterface) || finished;
    }

    @Override
    public void commandStateChanged(PHATCommand command) {
        if (command == goIntoBedCommand
                && command.getState().equals(PHATCommand.State.Success)) {
            finished = true;
        }
    }

    @Override
    public void simpleNextState(PHATInterface phatInterface) {
        
    }

    @Override
    public void initState(PHATInterface phatInterface) {
        goIntoBedCommand = new GoIntoBedCommand(agent.getId(), bedId, this);
        agent.runCommand(goIntoBedCommand);
    }
}
