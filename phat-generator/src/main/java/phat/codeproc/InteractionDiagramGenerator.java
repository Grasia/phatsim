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

import java.util.Collection;

import ingenias.exception.NotFound;
import ingenias.exception.NullEntity;
import ingenias.generator.browser.Browser;
import ingenias.generator.browser.Graph;
import ingenias.generator.browser.GraphAttribute;
import ingenias.generator.browser.GraphEntity;
import ingenias.generator.browser.GraphRelationship;
import ingenias.generator.browser.GraphRole;
import ingenias.generator.datatemplate.Repeat;
import ingenias.generator.datatemplate.Var;

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

		GraphEntity ip = Utils.getProfileTypeOf(humanId, INTERACTION_PROFILE_SPEC_DIAGRAM, browser);
		if(ip == null)
			return;
		GraphAttribute ga = ip.getAttributeByName("InteractionSpecDiagField");
		if (ga == null || ga.getSimpleValue().equals("")) {
			return;
		}

		String interactionDiagName = ga.getSimpleValue();
		Graph interactionSpec = browser.getGraph(interactionDiagName);
		if (interactionSpec != null && interactionSpec.getEntities().length > 0) {
			for(GraphEntity ge: Utils.getEntities(interactionSpec, "EventProcessor")) {
				GraphEntity event = Utils.getTargetEntity(ge, "RelatedEvent");
				GraphEntity activity = Utils.getTargetEntity(ge, "ActivityAttached");
				Collection<GraphEntity> conds = Utils.getTargetsEntity(ge, "ConditionNeeded");
				Repeat repEP = new Repeat("eventProcessor");
				repFather.add(repEP);
				repEP.add(new Var("eventId", event.getID()));
				repEP.add(new Var("eventCondition", ConditionGenerator.generateCondition(conds)));
				repEP.add(new Var("acticity", activity.getID()));
			}
		}
	}
}
