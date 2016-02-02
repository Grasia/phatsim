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
    
    public SayAutomaton(Agent agent, String name, String text) {
        super(agent, 0, name);
        this.text = text;
        this.volume = 1;
    }
    
    public SayAutomaton(Agent agent, String name, String text, float volume) {
        super(agent, 0, name);
        this.text = text;
        this.volume = volume;
    }
    
    public SayAutomaton setVolume(float volume) {
        this.volume = volume;
        return this;
    }
    
    public SayAutomaton setText(String text) {
        this.text = text;
        return this;
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