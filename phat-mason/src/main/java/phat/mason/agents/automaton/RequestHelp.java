/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
