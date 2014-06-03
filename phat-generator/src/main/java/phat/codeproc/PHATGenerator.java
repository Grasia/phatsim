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

import ingenias.editor.Log;
import ingenias.editor.ModelJGraph;
import ingenias.editor.ProjectProperty;
import ingenias.exception.NotFound;
import ingenias.exception.NullEntity;
import ingenias.generator.browser.Browser;
import ingenias.generator.browser.Graph;
import ingenias.generator.browser.GraphAttribute;
import ingenias.generator.browser.GraphEntity;
import ingenias.generator.browser.GraphRelationship;
import ingenias.generator.browser.GraphRole;
import ingenias.generator.datatemplate.Repeat;
import ingenias.generator.datatemplate.Sequences;
import ingenias.generator.datatemplate.Var;

import java.awt.Frame;
import java.io.File;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.Vector;
import phat.codeproc.pd.PDGenerator;

public class PHATGenerator extends
		ingenias.editor.extension.BasicCodeGeneratorImp {

	static final String HUMAN_PROFILE_SPEC_DIAGRAM = "HumanProfileSpecDiagram";
	static final String ADLProfile_SPEC_DIAGRAM = "ADLProfile";

	public PHATGenerator(String file) throws Exception {
		super(file);
		this.addTemplate("templates/agents2.xml");
		this.addTemplate("templates/scenario2.xml");
		this.addTemplate("templates/timeinterval.xml");
		this.addTemplate("templates/activities.xml");
		this.addTemplate("templates/tasks.xml");
                this.addTemplate("templates/disease_profile.xml");
	}

	public PHATGenerator(Browser browser) throws Exception {
		super(browser);
		this.addTemplate("templates/agents2.xml");
		this.addTemplate("templates/scenario2.xml");
		this.addTemplate("templates/timeinterval.xml");
		this.addTemplate("templates/activities.xml");
		this.addTemplate("templates/tasks.xml");
                this.addTemplate("templates/disease_profile.xml");
	}

	public String getVersion() {
		return "@modhtmldoc.ver@";
	}

	public static void main(String[] args) throws Exception {
		System.out
				.println("INGENIAS HTML Document Generator  (C) 2012 Jorge Gomez");
		System.out
				.println("This program comes with ABSOLUTELY NO WARRANTY; for details check www.gnu.org/copyleft/gpl.html.");
		System.out
				.println("This is free software, and you are welcome to redistribute it under certain conditions;; for details check www.gnu.org/copyleft/gpl.html.");

		if (args.length == 0) {
			System.err
					.println("The first argument (mandatory) has to be the specification file and the second "
							+ "the outputfolder folder");
		} else {

			if (args.length >= 2) {
				ingenias.editor.Log.initInstance(new PrintWriter(System.out));
				ModelJGraph.disableAllListeners(); // this disable layout
													// listeners that slow down
													// code generation
				// it is a bug of the platform which will be addressed in the
				// future

				ingenias.editor.Log.initInstance(new PrintWriter(System.out));
				PHATGenerator generator = new PHATGenerator(args[0]);
				Properties props = generator.getBrowser().getState().prop;
				new File(args[1]).mkdirs();
				generator.setProperty("output", args[1]);
				generator.run();
				if (ingenias.editor.Log.getInstance().areThereErrors()) {
					for (Frame f : Frame.getFrames()) {
						f.dispose();

					}
					throw new RuntimeException(
							"There are the following code generation errors: "
									+ Log.getInstance().getErrors());
				}
			} else {
				System.err
						.println("The first argument (mandatory) has to be the specification file and the second  "
								+ "the outputfolder");
			}

		}
		for (Frame f : Frame.getFrames()) {
			f.dispose();
		}

	}

	public void generateActivities(Graph activitiesSpec, Sequences seq)
			throws NotFound, NullEntity {
		GraphEntity ge = Utils.getFirstEntity(activitiesSpec);

		if (ge.getType().equals("BActivity")) {
			GraphEntity nActivity = Utils.getTargetEntity(ge, "NextActivity");
			if (nActivity == null) {
				return;
			}
			GraphAttribute ga = ge.getAttributeByName("SeqTaskDiagramField");
			Graph taskSpecDiagram = Utils.getGraphByName(ga.getSimpleValue(),
					getBrowser());

			/*if (taskSpecDiagram != null) {
				TaskGenerator taskGenerator = new TaskGenerator(getBrowser(),
						seq);
				taskGenerator.generate("", taskSpecDiagram);
			}*/
		}
	}

	/**
	 * Generates HTML code
	 * 
	 * @exception Exception
	 *                XML exception
	 */
	public Sequences generate() {
		Sequences seq = new Sequences();

		seq.addVar(new Var("output", this.getProperty("output").value));
		try {
			new AgentsGenerator(browser).generateAgents(seq);
			new TaskGenerator(getBrowser(), seq).generateAllSeqTasks();
			new ActivityGenerator(getBrowser()).generateTimeIntervals(seq);
			new SimulationGenerator(browser).generateSimulations(seq);
                        new PDGenerator(browser).generatePD(seq);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return seq;
	}

	public String getName() {
		return "phatgenerator";
	}

	public String getDescription() {
		return "It generates PHAT instantiation";
	}

	public Vector<ProjectProperty> defaultProperties() {
		Vector<ProjectProperty> result = new Vector<ProjectProperty>();
		Properties p = new Properties();
		result.add(new ingenias.editor.ProjectProperty(this.getName(),
				"output", "PHAT output folder", "target",
				"The folder that will contain the output"));

		/*
		 * result.add( new
		 * ingenias.editor.ProjectProperty(this.getName(),"htmldoc",
		 * "HTML document folder", "html",
		 * "The document folder that will contain HTML version of this specification"
		 * ));
		 */
		return result;
	}

}
