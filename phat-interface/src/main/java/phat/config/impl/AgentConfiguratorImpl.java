package phat.config.impl;

import phat.agents.Agent;
import phat.agents.AgentsAppState;
import phat.config.AgentConfigurator;

public class AgentConfiguratorImpl implements AgentConfigurator {
	AgentsAppState agentsAppState;

	public AgentConfiguratorImpl(AgentsAppState agentsAppState) {
		super();
		this.agentsAppState = agentsAppState;
	}

	public AgentsAppState getAgentsAppState() {
		return agentsAppState;
	}

	@Override
	public void add(Agent agent) {
		agentsAppState.add(agent);
	}
}
