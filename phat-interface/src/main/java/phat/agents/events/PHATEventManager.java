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
import java.util.Hashtable;
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
    Map<String, EventProcessor> eventsMapping = new Hashtable<String, EventProcessor>();
    List<PHATEvent> events = new ArrayList<>();
    List<PHATEvent> eventHistory = new ArrayList<>();
    PHATEvent currentEvent;

    public PHATEventManager(Agent agent) {
        this.agent = agent;
    }

    public synchronized void add(PHATEvent event) {
        events.add(event);
    }

    public synchronized void process(PHATInterface phatInterface) {
	List<PHATEvent> eventsToRemove = new ArrayList<>();
        for (PHATEvent event : events) {
            if (!event.state.equals(PHATEvent.State.Started)) {
                eventHistory.add(event);
                eventsToRemove.add(event);
                continue;
            }
            if (event.isPerceptible(agent)) {
                EventProcessor ep = null;

                ep = eventsMapping.get(event.getId());

                if (ep != null) {
                    Automaton automaton = ep.process(agent);
                    if (automaton != null) {
                        ActivityAutomaton aa = agent.getAutomaton()
                                .getCurrentUpperAutomatonByType(
                                ActivityAutomaton.class);
                        if (aa != null) {
                            Automaton currentAction = aa.getLeafAutomaton();
                            if (currentAction == null || currentAction.isCanBeInterrupted()) {
                                InterruptionAutomaton ia = new InterruptionAutomaton(agent, automaton, agent.getAutomaton());
                                Automaton ca = agent.getAutomaton();
                                ca.interrupt();
                                agent.setAutomaton(ia);
                                System.out.println("agent id = " + agent.getId());
                                System.out.println("Event id = " + event.id);
                                System.out.println("NotofyNextAutomaton1234");
                                ca.notifyNextAutomaton(ia);
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
                                eventHistory.add(event);
                                eventsToRemove.add(event);
                            } else {
                                System.out.println(currentAction.getName() + " NO Interrupted!");
                            }
                        }
                    } else {
                        event.setEventState(PHATEvent.State.Ignored);
                        eventHistory.add(event);
                        eventsToRemove.add(event);
                    }
                }
            }
        }
	events.removeAll(eventsToRemove);
    }

    public PHATEvent getEvent(String id) {
        if (!events.isEmpty()) {
            for (PHATEvent e : events) {
                if (e.getId().equals(id)) {
                    return e;
                }
            }
        }
        return null;
    }

    public boolean areEvents() {
        return !events.isEmpty();
    }

    public void addMap(String eventId, EventProcessor behavior) {
        eventsMapping.put(eventId, behavior);
    }
}
