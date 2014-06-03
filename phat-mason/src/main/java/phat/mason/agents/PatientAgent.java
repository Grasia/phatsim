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

import phat.mason.PHATSimState;
import phat.mason.agents.automaton.Automaton;
import phat.mason.agents.automaton.DoNothing;
import phat.mason.agents.automaton.FSM;
import phat.mason.agents.automaton.MasonAgentCondition;
import phat.mason.agents.automaton.MoveTo;
import phat.mason.agents.automaton.PlayAnimation;
import phat.mason.agents.automaton.RequestAnAgentDoSomething;
import phat.mason.agents.automaton.RequestHelp;
import phat.mason.agents.automaton.Say;
import phat.mason.agents.automaton.Slip;
import phat.mason.agents.automaton.StandUp;
import phat.mason.agents.automaton.SwitchLight;
import phat.mason.agents.automaton.Transition;
import phat.mason.agents.automaton.TripOver;
import phat.mason.space.PhysicsObject;
import phat.mason.space.Util;
import phat.structures.houses.House;
import sim.util.Double3D;

/**
 *
 * @author Pablo
 */
public class PatientAgent extends Agent {
    protected RelativeAgent relativeAgent;
    
    public PatientAgent(PhysicsActor physicsActor, PHATSimState state) {
        super(physicsActor, state);
    }

	@Override
	protected void initAutomaton() {
		//Automaton.setEcho(true);
        Automaton start = new DoNothing(this, 0, 1, "PatientIdle");
        Double3D loc = Util.get(state.getMasonAppState().getHouseAdapter().getHouse().getCoordenates("BathRoom1", "Basin"));
        Automaton moveTo = new MoveTo(this, 0, 0, "MoveToBathRoom", loc);
        Automaton switchLights = new SwitchLight(this, 0, 0, "SwitchOn BathRoom1 lights", "BathRoom1", true);
        Automaton washingHands = new DoNothing(this, 0, 1, "PatientIdle");
        Automaton lookBehindR = new PlayAnimation(this, 0, 0, "LookAtTheMirror", "LookBehindR");
        Automaton slip = new Slip(this, 0, 0, "Slip");
        Automaton tryStandUp = new DoNothing(this, 0, 5, "TryToStandUp");
        Automaton say = new Say(this, 0, 0, "Say: I need help, please!", "I need help, please!", 2f);
        Automaton requestHelp = new RequestHelp(this, 0, 0,"RequestHelp");
        Automaton wait = new DoNothing(this, 0, 200, "Waiting");
        Automaton help = new DoNothing(this, 1, 3, "BeingHelped");            
        Automaton standUp = new StandUp(this, 10, 0, "StandUp");            
        Automaton sayThanks = new Say(this, 10, 0, "Say: Thank you!", "Thank you!", 1.5f);
                       
        
        FSM fsm = new FSM(this);
        fsm.registerStartState(start);
        fsm.registerTransition(start, moveTo);
        fsm.registerTransition(moveTo, switchLights);
        fsm.registerTransition(switchLights, washingHands);
        fsm.registerTransition(washingHands, lookBehindR);
        fsm.registerTransition(lookBehindR, slip);
        fsm.registerTransition(slip, tryStandUp);
        fsm.registerTransition(tryStandUp, say);
        fsm.registerTransition(say, new Transition(new MasonAgentCondition(this) {			
			@Override
			public boolean evaluate() {
				ArrayList<Agent> agentsInSameLocation = getAgentNearThan(1f);
				return !agentsInSameLocation.isEmpty();
			}
		}, help));
        fsm.registerTransition(help, standUp);
        fsm.registerTransition(standUp, wait);
        fsm.registerTransition(wait, sayThanks);
        
        fsm.registerFinalState(sayThanks);  
        
        setAutomaton(fsm);
	}
}
