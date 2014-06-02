/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.agents.filters.types;

import phat.agents.Agent;
import phat.agents.automaton.Automaton;
import phat.agents.automaton.MoveToSpace;
import phat.agents.automaton.conditions.AutomatonCondition;
import phat.agents.automaton.conditions.TimerFinishedCondition;
import static phat.agents.filters.types.Filter.FILTER_TAG;

/**
 *
 * @author pablo
 */
public class DelayFilter extends Filter {

    float delay;

    public float getDelay() {
        return delay;
    }

    public void setDelay(float delay) {
        this.delay = delay;
    }

    @Override
    public Automaton apply(Agent agent, Automaton automaton) {
        System.out.println("Applying Delay Filter..."+automaton.getMetadata("SOCIAALML_ENTITY_ID"));
        System.out.println("Automaton is instance of " +automaton.getClass().getName());
        if (automaton instanceof MoveToSpace) {
            MoveToSpace move = (MoveToSpace) automaton;
            System.out.println("delay = "+delay);
            float speed = agent.getBodiesAppState().getSpeed(agent.getId());
            if(speed > 0) {
                move.setSpeed(speed/delay);
            }
            System.out.println("speed = "+move.getSpeed());
            
        } else {
            AutomatonCondition ac = automaton.getFinishCondition();
            if (ac != null && ac instanceof TimerFinishedCondition) {
                TimerFinishedCondition tfc = (TimerFinishedCondition) ac;
                tfc.setSeconds(Math.round(delay * tfc.getSeconds()));
            }
        }
        System.out.println("....Applying Delay Filter");
        return automaton;
    }
}
