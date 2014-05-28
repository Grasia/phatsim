package phat.agents.events;

import phat.agents.Agent;

import com.jme3.math.Vector3f;

public class AgentEventSource implements EventSource {

	Agent agent;
	
	public AgentEventSource(Agent agent) {
		this.agent = agent;
	}
	
	@Override
	public Vector3f getLocation() {
		return agent.getLocation();
	}

}
