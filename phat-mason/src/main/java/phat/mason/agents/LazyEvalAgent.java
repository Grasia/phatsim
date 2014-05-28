package phat.mason.agents;

import java.util.ArrayList;

import phat.mason.agents.automaton.Automaton;
import sim.engine.SimState;
import sim.util.Double3D;

/**
 * A lazy evaluation of an agent to be determined in runtime. It is useful to parameterize
 * operations over an agent which is not known yet
 * 
 * @author escalope
 *
 */
public abstract class LazyEvalAgent extends Agent {

	public LazyEvalAgent() {
		super(null, null);	
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public abstract Agent getAgent();
	@Override
	public void step(SimState ss) {
		// TODO Auto-generated method stub
		getAgent().step(ss);
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return getAgent().getName();
	}

	@Override
	public PhysicsActor getPhysicsActor() {
		// TODO Auto-generated method stub
		return getAgent().getPhysicsActor();
	}

	@Override
	public void setPhysicsActor(PhysicsActor physicsActor) {
		// TODO Auto-generated method stub
		getAgent().setPhysicsActor(physicsActor);
	}

	@Override
	public String getCurrentAction() {
		// TODO Auto-generated method stub
		return getAgent().getCurrentAction();
	}

	@Override
	public Automaton getAutomaton() {
		// TODO Auto-generated method stub
		return getAgent().getAutomaton();
	}

	@Override
	public void setAutomaton(Automaton automaton) {
		// TODO Auto-generated method stub
		getAgent().setAutomaton(automaton);
	}

	@Override
	public ArrayList<Agent> getAgentInSameLocation() {
		// TODO Auto-generated method stub
		return getAgent().getAgentInSameLocation();
	}

}
