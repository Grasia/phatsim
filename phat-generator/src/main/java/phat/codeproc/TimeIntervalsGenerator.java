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
import ingenias.generator.browser.GraphRelationship;
import ingenias.generator.browser.GraphRole;
import ingenias.generator.datatemplate.Repeat;
import ingenias.generator.datatemplate.Var;

public class TimeIntervalsGenerator {
	final static String TIME_INTERVAL_TYPE = "TimeInterval";
	final static String INTERVAL_CLOCK_REL = "IntervalClockTime";
	final static String ADLProfile_SPEC_DIAGRAM = "ADLProfile";

	final static String HOURS_FIELD = "HoursField";
	final static String MINS_FIELD = "MinutesField";
	final static String SECS_FIELD = "SecondsField";

	Browser browser;

	public TimeIntervalsGenerator(Browser browser) {
		this.browser = browser;
	}

	public void generateADL(String humanId, Repeat repFather) throws NotFound,
			NullEntity {

		GraphEntity adl = getADL(humanId, browser);
		if(adl == null)
			return;
		GraphAttribute ga = adl.getAttributeByName("ADLSpecField");
		if (ga == null || ga.getSimpleValue().equals("")) {
			return;
		}

		String adlDiagName = ga.getSimpleValue();
		System.out.println("GENERATE ADL: " + humanId);
		System.out.println("ADL = " + adlDiagName);
		Graph adlSpec = browser.getGraph(adlDiagName);
		if (adlSpec != null && adlSpec.getEntities().length > 0) {
			System.out.println("ADL no empty!");
			GraphEntity ge = Utils.getFirstEntity(adlSpec);
			Repeat repFirst = new Repeat("firstTimeInterval");
			repFather.add(repFirst);
			repFirst.add(new Var("tiname", ge.getID()));

			while (ge != null) {
				System.out.println(ge.getType() + ": " + ge.getID());
				if (ge.getType().equals(TIME_INTERVAL_TYPE)) {
					String timeIntervalName = ge.getID();
					Repeat rep = new Repeat("timeInstance");
					repFather.add(rep);
					rep.add(new Var("tiname", timeIntervalName));
					GraphEntity geClock = Utils.getTargetEntity(ge,
							INTERVAL_CLOCK_REL);
					if (geClock != null) {
						GraphAttribute gaHours = geClock
								.getAttributeByName(HOURS_FIELD);
						int hours = Integer.parseInt(gaHours.getSimpleValue());
						GraphAttribute gaMins = geClock
								.getAttributeByName("MinutesField");
						int mins = Integer.parseInt(gaMins.getSimpleValue());
						GraphAttribute gaSecs = geClock
								.getAttributeByName(SECS_FIELD);
						int secs = Integer.parseInt(gaSecs.getSimpleValue());
						System.out.println(geClock.getID() + ": " + hours + ":"
								+ mins + ":" + secs);
						Repeat rep2 = new Repeat("timeTransition");
						rep.add(rep2);
						rep.add(new Var("hours", String.valueOf(hours)));
						rep.add(new Var("minutes", String.valueOf(mins)));
						rep.add(new Var("seconds", String.valueOf(secs)));
					}
					System.out.println("NextTimeInterval of " + ge.getID());
					GraphEntity geNext = Utils.getTargetEntity(ge,
							"NextTimeInterval");
					if (geNext != null) {
						Repeat rep3 = new Repeat("regTrans");
						repFather.add(rep3);
						rep3.add(new Var("tinameS", ge.getID()));
						rep3.add(new Var("tinameT", geNext.getID()));
					}
					ge = geNext;
				} else {
					System.out.println(ge.getType() + ": " + ge.getID());
					break;
				}
			}
		}
	}

	public static GraphEntity getADL(String humanId, Browser browser) {
		GraphEntity result = null;
		try {
			GraphEntity[] entities = browser.getAllEntities();
			for (GraphEntity adl : entities) {
				if (adl.getType().equalsIgnoreCase(ADLProfile_SPEC_DIAGRAM)) {
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
