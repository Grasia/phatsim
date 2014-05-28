package phat.agents.automaton;

import java.util.ArrayList;

import phat.PHATInterface;
import phat.agents.Agent;

public class MainAutomaton extends Automaton {

	public MainAutomaton(Agent agent) {
		super(agent);
	}

	@Override
	public void initState(PHATInterface phatInterface) {
		// TODO Auto-generated method stub

	}

	@Override
	public Automaton getDefaultState(PHATInterface phatInterface) {
		return new DoNothing(agent, "Default behaviour: do nothing");
	}

	@Override
	public ArrayList<Automaton> createNewTransitions(PHATInterface phatInterface) {
		return null;
	}

}
