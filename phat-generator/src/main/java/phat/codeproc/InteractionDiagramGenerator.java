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

		GraphEntity ip = getInteractionProfile(humanId, browser);
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

	public static GraphEntity getInteractionProfile(String humanId, Browser browser) {
		GraphEntity result = null;
		try {
			GraphEntity[] entities = browser.getAllEntities();
			for (GraphEntity adl : entities) {
				if (adl.getType().equalsIgnoreCase(INTERACTION_PROFILE_SPEC_DIAGRAM)) {
					GraphEntity human = Utils.getTargetEntity(adl, "ProfileOf");
					if (human.getID().equals(humanId)) {
						return adl;
					}
				}
			}
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return result;
	}
}
