package phat.agents.commands;

import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;

public abstract class PHATAgentCommand extends PHATCommand {

	public PHATAgentCommand(PHATCommandListener listener) {
		super(listener);
	}
}
