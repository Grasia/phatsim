/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.agents.automaton;

import phat.PHATInterface;
import phat.agents.Agent;
import phat.body.commands.SayASentenceBodyCommand;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;

/**
 *
 * @author pablo
 */
public class SayAutomaton extends SimpleState implements PHATCommandListener {    
    private boolean done = false;
    SayASentenceBodyCommand sayASentenceBodyCommand;
    String text;
    float volume;
    
    public SayAutomaton(Agent agent, String name, String text, float volume) {
        super(agent, 0, name);
        this.text = text;
        this.volume = volume;
    }
    
    @Override
    public boolean isFinished(PHATInterface phatInterface) {
        return super.isFinished(phatInterface) || done;
    }

	@Override
	public void commandStateChanged(PHATCommand command) {
		if(command == sayASentenceBodyCommand && 
				command.getState().equals(PHATCommand.State.Success)) {
			Agent.shout(sayASentenceBodyCommand.getMessage(), agent.getLocation());
			done = true;
		}
	}

    @Override
    public void simpleNextState(PHATInterface phatInterface) {
        
    }

    @Override
    public void initState(PHATInterface phatInterface) {
    	sayASentenceBodyCommand = new SayASentenceBodyCommand(agent.getId(), text, this);
        sayASentenceBodyCommand.setVolume(volume);
        agent.runCommand(sayASentenceBodyCommand);
    }
}