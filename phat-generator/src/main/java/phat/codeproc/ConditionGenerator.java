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
import ingenias.generator.browser.GraphAttribute;
import ingenias.generator.browser.GraphEntity;

import java.util.Collection;

public class ConditionGenerator {
	static final String CTIME_TYPE = "CTime";
	final static String HOURS_FIELD = "HoursField";
	final static String MINS_FIELD = "MinutesField";
	final static String SECS_FIELD = "SecondsField";
	final static String CINSIDE_HOUSE_TYPE = "CInside";
	final static String COUTSIDE_HOUSE_TYPE = "COutside";
	final static String CPROB_TYPE = "CProb";
	
	public static String generateCondition(Collection<GraphEntity> conds) throws NotFound {
		if(!conds.isEmpty()) {
			for(GraphEntity geCond: conds) {
				String type = geCond.getType();
				if(type.equals(CTIME_TYPE)) {					
					GraphAttribute gaHours = geCond
							.getAttributeByName(HOURS_FIELD);
					int hours = Integer.parseInt(gaHours.getSimpleValue());
					GraphAttribute gaMins = geCond
							.getAttributeByName("MinutesField");
					int mins = Integer.parseInt(gaMins.getSimpleValue());
					GraphAttribute gaSecs = geCond
							.getAttributeByName(SECS_FIELD);
					int secs = Integer.parseInt(gaSecs.getSimpleValue());
					System.out.println(geCond.getID() + ": " + hours + ":"
							+ mins + ":" + secs);
					return "new PastTimeCondition("+String.valueOf(hours)+","+String.valueOf(mins)+","+String.valueOf(secs)+")";
				} else if(type.equals(CINSIDE_HOUSE_TYPE)) {
					return "new IsInsideHouseCondition()";
				} else if(type.equals(COUTSIDE_HOUSE_TYPE)) {
					return negate("new IsInsideHouseCondition()");
				} else if(type.equals(CPROB_TYPE)) {
					GraphAttribute gaProb = geCond.getAttributeByName("ProbVarField");
					float prob = Float.parseFloat(gaProb.getSimpleValue());
					return "new ProbCondition("+prob+"f)";
				}
			}
		}
		return "null";
	}
	
	public static String negate(String condSentence) {
		return "new NegateCondition("+condSentence+")";
	}
}
