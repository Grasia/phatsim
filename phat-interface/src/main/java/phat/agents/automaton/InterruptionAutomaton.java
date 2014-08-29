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

import com.jme3.scene.Node;
import java.util.ArrayList;
import java.util.List;
import phat.PHATInterface;
import phat.agents.Agent;
import phat.body.BodyUtils;
import phat.body.BodyUtils.BodyPosture;
import phat.body.commands.GoIntoBedCommand;
import phat.body.commands.SitDownCommand;

/**
 *
 * @author pablo
 */
public class InterruptionAutomaton extends Automaton {

    Automaton firstAutomaton;
    Automaton resumedAutomaton;
    BodyPosture bodyPosture;
    Automaton preAutomaton;

    public InterruptionAutomaton(Agent agent, Automaton firstAutomaton, Automaton resumedAutomaton) {
        super(agent);
        this.firstAutomaton = firstAutomaton;
        this.resumedAutomaton = resumedAutomaton;
        super.name = getClass().getSimpleName();
        bodyPosture = agent.getBodyPosture();

        if (BodyUtils.isBodyPosture(
                agent.getBodiesAppState().getBody(agent.getId()), BodyPosture.Sitting)) {
            SitDownCommand sit = agent.getBodiesAppState().getLastCommand(SitDownCommand.class);
            if (sit != null) {
                preAutomaton = new SitDownAutomaton(agent, sit.getPlaceId());
            }
        } else if (BodyUtils.isBodyPosture(
                agent.getBodiesAppState().getBody(agent.getId()), BodyPosture.Lying)) {
            GoIntoBedCommand goInto = agent.getBodiesAppState().getLastCommand(GoIntoBedCommand.class);
            if (goInto != null) {
                preAutomaton = new GoIntoBedAutomaton(agent, goInto.getBedId());
            }
        }
    }

    @Override
    public void initState(PHATInterface phatInterface) {
        if (bodyPosture != null && (BodyUtils.isBodyPosture(
                agent.getBodiesAppState().getBody(agent.getId()), BodyUtils.BodyPosture.Sitting)
                || BodyUtils.isBodyPosture(
                agent.getBodiesAppState().getBody(agent.getId()), BodyUtils.BodyPosture.Lying))) {
            addTransition(new StandUpAutomaton(agent, "StandUpAutomaton"), false);
        }
        addTransition(firstAutomaton, false);
    }

    @Override
    public Automaton getDefaultState(PHATInterface phatInterface) {
        FSM result = new FSM(agent);
        if (resumedAutomaton != null) {
            resumedAutomaton.resume(phatInterface);
            result.registerStartState(preAutomaton);
            if (preAutomaton != null) {
                result.registerTransition(preAutomaton, resumedAutomaton);
            }
        }
        result.registerFinalState(resumedAutomaton);
        return result;
    }

    @Override
    public ArrayList<Automaton> createNewTransitions(PHATInterface phatInterface) {
        return null;
    }
}
