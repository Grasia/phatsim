/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.mason.agents;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import phat.mason.PHATSimState;
import phat.mason.agents.automaton.Automaton;
import phat.mason.agents.automaton.DoNothing;
import phat.mason.agents.automaton.FSM;
import phat.mason.agents.automaton.MasonAgentCondition;
import phat.mason.agents.automaton.MoveToLazyLocation;
import phat.mason.agents.automaton.PlayAnimation;
import phat.mason.agents.automaton.Transition;
import sim.util.Double3D;

/**
 *
 * @author Pablo
 */
public class RelativeAgent extends Agent {

    public RelativeAgent(PhysicsActor physicsActor, PHATSimState state) {
        super(physicsActor, state);
    }
    
    @Override
    protected void initAutomaton() {
        //Automaton.setEcho(true);
        Automaton start = new DoNothing(this, 0, 3, "PatientIdle");
        Automaton hand2Belly = new PlayAnimation(this, 0, 2, "Play Hands2Hips", "Hands2Hips");
        Automaton nothing1 = new DoNothing(this, 0, 2, "PatientIdle");
        Automaton hands2Hip = new PlayAnimation(this, 0, 2, "Play Yawn", "Yawn");
        Automaton moveTo = new MoveToLazyLocation(this, 1, 0, "MoveToHelp",
                new Lazy<Double3D>() {
            public Double3D getLazy() {
                return haveIHeard("help");
            }
         ;
        }, 0.8f);
        Automaton help = new DoNothing(this, 1, 3, "Helping");

        FSM fsm = new FSM(this);
        fsm.registerStartState(start);
        fsm.registerTransition(start, hand2Belly);
        fsm.registerTransition(hand2Belly, nothing1);
        fsm.registerTransition(nothing1, hands2Hip);
        fsm.registerTransition(hands2Hip, new Transition(
                new MasonAgentCondition(this) {
            @Override
            public boolean evaluate() {
                // listening help
                return haveIHeard("help") != null;
            }
        }, moveTo));
        fsm.registerFinalState(moveTo);
        setAutomaton(fsm);
    }
}
