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
package phat.codeproc;

import ingenias.exception.NotFound;
import ingenias.exception.NullEntity;
import ingenias.generator.browser.Browser;
import ingenias.generator.browser.Graph;
import ingenias.generator.browser.GraphAttribute;
import ingenias.generator.browser.GraphEntity;
import ingenias.generator.datatemplate.Repeat;
import ingenias.generator.datatemplate.Var;
import java.util.Vector;

import java.util.Collection;

public class InteractionDiagramGenerator {

    final static String TIME_INTERVAL_TYPE = "TimeInterval";
    final static String INTERVAL_CLOCK_REL = "IntervalClockTime";
    final static String INTERACTION_PROFILE_SPEC_DIAGRAM = "InteractionProfile";
    Browser browser;

    public InteractionDiagramGenerator(Browser browser) {
        this.browser = browser;
    }

    public void generateEventProcessor(String humanId, Repeat repFather) throws NotFound,
            NullEntity {

        Vector<GraphEntity> ips = Utils.getProfilesTypeOf(humanId, INTERACTION_PROFILE_SPEC_DIAGRAM, browser);
        for (GraphEntity ip : ips) {
            GraphAttribute ga = ip.getAttributeByName("InteractionSpecDiagField");
            if (ga == null || ga.getSimpleValue().equals("")) {
                return;
            }

            String interactionDiagName = ga.getSimpleValue();
            Graph interactionSpec = browser.getGraph(interactionDiagName);
            if (interactionSpec != null && interactionSpec.getEntities().length > 0) {
                for (GraphEntity ge : Utils.getEntities(interactionSpec, "EventProcessor")) {
                    GraphEntity event = Utils.getTargetEntity(ge, "RelatedEvent");
                    String eventId = Utils.replaceBadChars(event.getID());
                    if (event.getType().equals("VibrateEvent")) {
                        GraphAttribute deviceSource = event.getAttributeByName("DeviceSource");
                        if (deviceSource == null || deviceSource.getSimpleValue().equals("")) {
                            System.exit(-1);
                        }
                        GraphAttribute state = event.getAttributeByName("DeviceState");
                        if (deviceSource == null || deviceSource.getSimpleValue().equals("")) {
                            System.exit(-1);
                        }
                        eventId = deviceSource.getSimpleValue() + "-Vibrator-" + state.getSimpleValue();
                    } else if (event.getType().equals("CallStateEvent")) {
                        GraphAttribute deviceSource = event.getAttributeByName("DeviceSource");
                        if (deviceSource == null || deviceSource.getSimpleValue().equals("")) {
                            System.exit(-1);
                        }
                        GraphAttribute state = event.getAttributeByName("CallStateField");
                        if (deviceSource == null || deviceSource.getSimpleValue().equals("")) {
                            System.exit(-1);
                        }
                        eventId = deviceSource.getSimpleValue() + "-Call-" + state.getSimpleValue();
                    } else if (event.getType().equals("MessageListenedEvent")) {
                        GraphAttribute message = event.getAttributeByName("Message");
                        if (message == null || message.getSimpleValue().equals("")) {
                            System.exit(-1);
                        }
                        eventId = humanId + ":";
                        String[] words = message.getSimpleValue().split(" ");
                        for (int i = 0; i < words.length - 1; i++) {
                            eventId += words[i] + "-";
                        }
                        eventId += words[words.length - 1];
                    }
                    GraphEntity activity = Utils.getTargetEntity(ge, "ActivityAttached");
                    Collection<GraphEntity> conds = Utils.getTargetsEntity(ge, "ConditionNeeded");
                    Repeat repEP = new Repeat("eventProcessor");
                    repFather.add(repEP);
                    repEP.add(new Var("eventId", eventId));
                    repEP.add(new Var("eventCondition", ConditionGenerator.generateAndCondition(conds)));
                    repEP.add(new Var("acticity", Utils.replaceBadChars(activity.getID())));
                }
            }
        }
    }
}
