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
import ingenias.generator.datatemplate.Sequences;
import ingenias.generator.datatemplate.Var;
import phat.codeproc.pd.PDGenerator;

public class AgentsGenerator {
	static final String HUMAN_PROFILE_SPEC_DIAGRAM = "HumanProfileSpecDiagram";
	static final String ADLProfile_SPEC_DIAGRAM = "ADLProfile";
	static final String SIMULATION_DIAGRAM = "SimulationDiagram";

	Browser browser;

	public AgentsGenerator(Browser browser) {
		this.browser = browser;
	}

	public void generateAgents(Sequences seq) throws NullEntity, NotFound {
		for (Graph diagram : Utils.getGraphsByType(HUMAN_PROFILE_SPEC_DIAGRAM,
				browser)) {
			for (GraphEntity actor : Utils.getEntities(diagram, "Human")) {
				Repeat rep = new Repeat("actors");
				seq.addRepeat(rep);
				rep.add(new Var("actorname", actor.getID()));

				String humanId = actor.getID();
				new TimeIntervalsGenerator(browser).generateADL(humanId, rep);
				new InteractionDiagramGenerator(browser).generateEventProcessor(humanId, rep);
                                PDGenerator.linkPDManager(humanId, rep, browser);
			}
		}
	}
}
