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

import phat.agents.automaton.conditions.AutomatonCondition;
import phat.agents.automaton.conditions.EmptyCondition;

public class Transition {

	private AutomatonCondition ac=null;
	private Automaton target=null;
	
	public Transition(AutomatonCondition ac, Automaton target)  {
		this.ac=ac;
		this.target=target;
	}
	
	public Transition(Automaton target)  {
		this.ac=new EmptyCondition();
		this.target=target;
	}
	
	public void setCondition(AutomatonCondition ac) {
		this.ac = ac;
	}
        
        public AutomatonCondition getCondition() {
            return this.ac;
        }
	
	public Automaton getTarget(){
		return target;
	}
	
	public boolean evaluate(){
		return ac.evaluate(target.getAgent());
	}
}
