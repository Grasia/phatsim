package phat.agents.events;

import phat.agents.Agent;
import phat.agents.automaton.Automaton;
import phat.agents.automaton.AutomatonListener;

public class PHATAudioEventAutomatonFinishedListener extends PHATAudioEvent implements AutomatonListener {

    Agent agent;

    public PHATAudioEventAutomatonFinishedListener(Agent agent, String id, EventSource eventSource) {
        super(id, eventSource);
        this.agent = agent;
    }

    @Override
    public void automatonFinished(Automaton automaton, boolean isSuccessful) {
        if (isSuccessful) {
            agent.getAgentsAppState().add(this);
        }
    }

    @Override
    public void nextAutomaton(Automaton previousAutomaton, Automaton nextAutomaton) {
    }

    @Override
    public void preInit(Automaton automaton) {
        
    }

    @Override
    public void postInit(Automaton automaton) {
        
    }
}
