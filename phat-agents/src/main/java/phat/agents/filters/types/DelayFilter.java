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
package phat.agents.filters.types;

import phat.agents.Agent;
import phat.agents.HumanAgent;
import phat.agents.automaton.Automaton;
import phat.agents.automaton.MoveToSpace;
import phat.agents.automaton.conditions.AutomatonCondition;
import phat.agents.automaton.conditions.TimerFinishedCondition;

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
        if (agent instanceof HumanAgent) {
            HumanAgent humanAgent = (HumanAgent) agent;
            if (automaton instanceof MoveToSpace) {
                MoveToSpace move = (MoveToSpace) automaton;
                //System.out.println("delay = "+delay);
                float speed = humanAgent.getBodiesAppState().getSpeed(humanAgent.getId());
                //System.out.println("speed = "+speed);
                if (speed > 0) {
                    move.setSpeed(speed / delay);
                }
                //System.out.println("speed = "+move.getSpeed());

            } else {
                AutomatonCondition ac = automaton.getFinishCondition();
                if (ac != null && ac instanceof TimerFinishedCondition) {
                    TimerFinishedCondition tfc = (TimerFinishedCondition) ac;
                    tfc.setSeconds(Math.round(delay * tfc.getSeconds()));
                }
            }
        }
        //System.out.println("....Applying Delay Filter");
        return automaton;
    }
}
