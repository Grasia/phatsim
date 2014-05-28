/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.agents.automaton;

import phat.PHATInterface;
import phat.agents.Agent;
import phat.body.commands.SitDownCommand;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;

/**
 *
 * @author pablo
 */
public class SitDownAutomaton extends SimpleState implements PHATCommandListener {
    SitDownCommand sitDownCommand;
    boolean seated = false;
    
    public SitDownAutomaton( Agent agent, String placeId) {
        super(agent, 0, "SitDownAutomaton");
        sitDownCommand = new SitDownCommand(agent.getId(), placeId, this);
    }

    @Override
    public boolean isFinished(PHATInterface phatInterface) {
        return super.isFinished(phatInterface) || seated;
    }
    
    @Override
    public void commandStateChanged(PHATCommand command) {
        if (command == sitDownCommand
                && command.getState().equals(PHATCommand.State.Success)) {
            seated = true;
        }
    }

    @Override
    public void simpleNextState(PHATInterface phatInterface) {
        
    }

    @Override
    public void initState(PHATInterface phatInterface) {
        agent.runCommand(sitDownCommand);
    }
}
