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
public class RequestAnAgentDoSomething extends SimpleState {
    private boolean init = false;
    protected Agent agentTarget;
    protected Automaton action;
    
    public RequestAnAgentDoSomething( Agent agent, int priority, 
    		int duration, String name, Agent agentTarget,
    		Automaton action) {
        super(agent, priority, duration, name);
        this.agentTarget = agentTarget;
        this.action = action;
    }
    
    @Override
    public void nextState(SimState state) {
        if(!init) {     
            /*
            Automaton standUp = new PlayAnimation(agent, 0, -1, "StandUp", "StandUp");
            Automaton sayThanks = new Say(agentTarget, 2, 0, "MoveToHelp", "Thank you!", 0.5f);
            
            FSM fsm = new FSM(agentTarget);
            fsm.registerStartState(standUp);
            fsm.registerTransition(standUp, sayThanks);
            fsm.registerFinalState(sayThanks);
            */
            System.out.println("Request "+agentTarget.getName()+" do "+action.getName());
            agentTarget.setAutomaton(action);
            
            
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