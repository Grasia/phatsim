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
package phat.agents;

import java.util.ArrayList;

import phat.PHATInterface;
import phat.agents.automaton.Automaton;

/**
 * A lazy evaluation of an agent to be determined in runtime. It is useful to parameterize
 * operations over an agent which is not known yet
 * 
 * @author escalope
 *
 */
public abstract class LazyEvalAgent extends Agent {

	public LazyEvalAgent() {
		super(null);	
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public abstract Agent getAgent();
	@Override
	public void update(PHATInterface phatInterface) {
		getAgent().update(phatInterface);
	}

	@Override
	public String getId() {
		return getAgent().getId();
	}
	
	@Override
	public String getCurrentAction() {
		return getAgent().getCurrentAction();
	}

	@Override
	public Automaton getAutomaton() {
		return getAgent().getAutomaton();
	}

	@Override
	public void setAutomaton(Automaton automaton) {
		getAgent().setAutomaton(automaton);
	}
}
