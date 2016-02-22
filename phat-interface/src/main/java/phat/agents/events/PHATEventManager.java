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
    List<EventRecord> eventHistory = new ArrayList<>();
    PHATEvent currentEvent;

    public PHATEventManager(Agent agent) {
        this.agent = agent;
    }

    public synchronized void add(PHATEvent event) {
        System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<< ADD: "+event.getId() +">>>>>>>>>>>>>>>>>>>>>>>>>");
        events.add(event);
    }

    public synchronized void process(PHATInterface phatInterface) {
        System.out.println(agent.getId()+": events = "+events.size());
        for (PHATEvent event : events) {
            if (event.isPerceptible(agent)) {
                System.out.println(agent.getId()+":"+event+"<<<<<<<<<<<<<<<<<<<<< is perceptible >>>>>>>>>>>>>>>>>>>");
                EventProcessor ep = eventsMapping.get(event.getId());

                if (ep != null) {
                    System.out.println(agent.getId()+":"+event+"<<<<<<<<<<<<<<<<<<<<< process >>>>>>>>>>>>>>>>>>>");
                    Automaton automaton = ep.process(agent);
                    if (automaton != null) {
                        System.out.println(agent.getId()+":"+event+"<<<<<<<<<<<<<<<<<<<<< automaton >>>>>>>>>>>>>>>>>>>");
                        System.out.println(automaton);
                        //Automaton currentAction = agent.getAutomaton().getCurrentAutomaton();
                        //InterruptionAutomaton ia = new InterruptionAutomaton(agent, automaton, currentAction);
                        automaton.setPriority(10);
                        agent.getAutomaton().addTransition(automaton, true);
                        event.setEventState(PHATEvent.State.Assigned);
                    } else {
                        event.setEventState(PHATEvent.State.Ignored);
                    }
                }
            }
            eventHistory.add(new EventRecord(phatInterface.getSimTime().getMillisecond(), event));
        }
        events.clear();
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

    public List<EventRecord> getLastEvents(long millisecondsAgo, long currentTimeInMilliSecs) {
        List<EventRecord> result = new ArrayList<>();
        for(int i = eventHistory.size()-1; i >= 0; i--) {
            EventRecord er = eventHistory.get(i);
            if(currentTimeInMilliSecs - er.getTimestamp() > millisecondsAgo) {
                break;
            }
            result.add(er);
        }
        return result;
    }
    
    public boolean contains(List<EventRecord> eRecords, String id) {
        for(EventRecord er: eRecords) {
            if(er.getEvent().getId().equals(id)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean areEvents() {
        return !events.isEmpty();
    }

    public void addMap(String eventId, EventProcessor behavior) {
        eventsMapping.put(eventId, behavior);
    }
}
