package phat.codeproc;

import java.util.Collection;

import phat.agents.automaton.conditions.PastTimeCondition;
import ingenias.exception.NotFound;
import ingenias.generator.browser.GraphAttribute;
import ingenias.generator.browser.GraphEntity;
import ingenias.generator.datatemplate.Repeat;
import ingenias.generator.datatemplate.Var;

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
