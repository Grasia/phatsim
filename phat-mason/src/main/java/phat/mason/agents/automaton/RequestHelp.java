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
package phat.mason.agents.automaton;

import phat.mason.agents.Agent;
import phat.mason.agents.PhysicsActor;
import sim.engine.SimState;
import sim.util.Bag;
import sim.util.Double3D;

/**
 *
 * @author pablo
 */
public class RequestHelp extends SimpleState {
    private boolean init = false;
    
    public RequestHelp( Agent agent, int priority, int duration, String name) {
        super(agent, priority, duration, name);
    }
    
    @Override
    public void nextState(SimState state) {
        if(!init) {
            Agent anyAgent = getAgent();
            
            Automaton moveTo = new MoveTo(anyAgent, 1, 0, "MoveToHelp", agent.getPhysicsActor().getLocation(), 1.5f);
            Automaton help = new DoNothing(anyAgent, 1, 3, "Helping");
            
            Automaton standUp = new StandUp(agent, 10, 0, "StandUp");
            Automaton sayThanks = new Say(agent, 10, 0, "Say: Thank you!", "Thank you!", 1.5f);
            FSM fsmMe = new FSM(agent);
            fsmMe.registerStartState(standUp);
            fsmMe.registerTransition(standUp, sayThanks);
            fsmMe.registerFinalState(sayThanks);
            Automaton request = new RequestAnAgentDoSomething(anyAgent, 1, 0, "StandUpHelp", agent, fsmMe);
                    
            FSM fsm = new FSM(anyAgent);
            fsm.registerStartState(moveTo);
            fsm.registerTransition(moveTo, help);
            fsm.registerTransition(help, request);
            fsm.registerFinalState(request);
            
            anyAgent.setAutomaton(fsm);
            
            
            init = true;
        }
    }
    
    private Agent getAgent() {
        Double3D loc = agent.getPhysicsActor().getLocation();
        Bag bag = agent.getPhysicsActor().world().getAllObjects();
        for(int i = 0; i < bag.numObjs; i++) {
            if(bag.get(i) instanceof PhysicsActor) {
                PhysicsActor pa = (PhysicsActor)bag.get(i);
                if(pa.agent() != agent) {
                    return pa.agent();
                }
            }
        }
        return null;
    }
    
    @Override
    public boolean isFinished(SimState state) {
        return init;
    }
}
