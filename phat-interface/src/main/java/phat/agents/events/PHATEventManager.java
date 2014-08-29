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
package phat.agents.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phat.PHATInterface;
import phat.agents.Agent;
import phat.agents.automaton.ActivityAutomaton;
import phat.agents.automaton.Automaton;
import phat.agents.automaton.InterruptionAutomaton;

/**
 *
 * @author pablo
 */
public class PHATEventManager {

    Agent agent;
    Map<String, EventProcessor> eventsMapping = new HashMap<>();
    List<PHATEvent> events = new ArrayList<>();
    PHATEvent currentEvent;

    public PHATEventManager(Agent agent) {
        this.agent = agent;
    }

    public void add(PHATEvent event) {
        events.add(event);
    }

    public void process(PHATInterface phatInterface) {
        for (PHATEvent event : events) {
            if (!event.state.equals(PHATEvent.State.Started)) {
                continue;
            }
            if (event.isPerceptible(agent)) {
                EventProcessor ep = eventsMapping.get(event.getId());
                if (ep != null) {
                    Automaton automaton = ep.process(agent);
                    if (automaton != null) {
                        ActivityAutomaton aa = agent.getAutomaton()
                                .getCurrentUpperAutomatonByType(
                                ActivityAutomaton.class);
                        if (aa != null) {
                            Automaton currentAction = aa.getLeafAutomaton();
                            if (currentAction == null || currentAction.isCanBeInterrupted()) {
                                agent.getAutomaton().interrupt();
                                agent.setAutomaton(
                                new InterruptionAutomaton(agent, automaton, agent.getAutomaton()));
                                
                                /*if(currentAction != null) {
                                    System.out.println("Current task = "
                                    + aa.getLeafAutomaton().getName());
                                    currentAction.interrupt();
                                }
                                aa.interrupt();
                                aa.addTransition(automaton, true);
                                aa.initState(phatInterface);
                                aa.printPendingTransitions();*/
                                agent.getAutomaton().printPendingTransitions();
                                event.setEventState(PHATEvent.State.Assigned);
                            } else {
                                System.out.println(currentAction.getName() + " NO Interrupted!");
                            }
                        }
                    } else {
                        event.setEventState(PHATEvent.State.Ignored);
                    }
                }
            }
        }
    }

    public boolean areEvents() {
        return !events.isEmpty();
    }

    public void addMap(String eventId, EventProcessor behavior) {
        eventsMapping.put(eventId, behavior);
    }
}
