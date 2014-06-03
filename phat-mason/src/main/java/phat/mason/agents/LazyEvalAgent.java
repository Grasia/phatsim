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
