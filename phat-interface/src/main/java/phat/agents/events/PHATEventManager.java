package phat.agents.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phat.PHATInterface;
import phat.agents.Agent;
import phat.agents.automaton.ActivityAutomaton;
import phat.agents.automaton.Automaton;

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
            System.out.println("Event = " + event.getId());
            System.out.println("isPerceptible = " + event.isPerceptible(agent));
            if (event.isPerceptible(agent)) {
                EventProcessor ep = eventsMapping.get(event.getId());
                System.out.println("EventProcessor = " + ep);
                if (ep != null) {
                    Automaton automaton = ep.process(agent);
                    if (automaton != null) {
                        System.out.println("\tBehavior=" + automaton.getName());
                        ActivityAutomaton aa = agent.getAutomaton()
                                .getCurrentUpperAutomatonByType(
                                ActivityAutomaton.class);
                        if (aa != null) {
                            System.out.println("Activity = " + aa.getName());
                            Automaton currentAction = aa.getLeafAutomaton();
                            if (currentAction == null || currentAction.isCanBeInterrupted()) {                                
                                if(currentAction != null) {
                                    System.out.println("Current task = "
                                    + aa.getLeafAutomaton().getName());
                                    currentAction.interrupt();
                                }
                                aa.interrupt();
                                aa.addTransition(automaton, true);
                                aa.initState(phatInterface);
                                aa.printPendingTransitions();
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
