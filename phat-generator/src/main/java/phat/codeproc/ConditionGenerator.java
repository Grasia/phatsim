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
import ingenias.generator.browser.GraphAttribute;
import ingenias.generator.browser.GraphEntity;
import java.util.ArrayList;

import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConditionGenerator {

    static final Logger logger = Logger.getLogger(ConditionGenerator.class.getName());
    static final String CTIME_TYPE = "CTime";
    static final String CTIMER_TYPE = "CTimer";
    final static String HOURS_FIELD = "HoursField";
    final static String MINS_FIELD = "MinutesField";
    final static String SECS_FIELD = "SecondsField";
    final static String CINSIDE_HOUSE_TYPE = "CInside";
    final static String COUTSIDE_HOUSE_TYPE = "COutside";
    final static String CPROB_TYPE = "CProb";
    final static String CEVENT_TYPE = "CEvent";
    final static String CSYMP_TYPE = "CSymptom";
    final static String CDAY_OF_THE_WEEK = "CDayOfTheWeek";
    final static String CWEIGHT = "CObjWeight";
    final static String CBODY_STATE = "BodyStateCondition";
    final static String THING_IN_ROOM_STATE = "IsThingInRoomCondition";
    final static String NOT_CONDITION = "NotCondition";
    final static String CSAY = "CSay";

    public static String generateAndCondition(Collection<GraphEntity> conds) {
        if (!conds.isEmpty()) {
            List<GraphEntity> conditions = new ArrayList<>(conds);
            if (conditions.size() == 1) {
                return getCondition(conditions.get(0));
            } else {
                String result = "new CompositeAndCondition(";
                for (int i = 0; i < conditions.size(); i++) {
                    GraphEntity geCond = conditions.get(i);
                    result += getCondition(geCond);
                    if (i != conditions.size() - 1) {
                        result += ",";
                    }
                }
                result += ")";
                return result;
            }
        }
        return "new EmptyCondition()";
    }

    private static String getCondition(GraphEntity geCond) {
        String type = geCond.getType();
        if (type.equals(CTIME_TYPE)) {
            GraphAttribute gaHours = null;
            try {
                gaHours = geCond.getAttributeByName(HOURS_FIELD);
            } catch (NotFound ex) {
                logger.log(Level.SEVERE,
                        "Entity {0} hasn't got attribute {1}", new Object[]{geCond.getID(), HOURS_FIELD});
                System.exit(-1);
            }
            int hours = 0;
            if (!gaHours.getSimpleValue().equals("")) {
                hours = Integer.parseInt(gaHours.getSimpleValue());
            } else {
                logger.log(Level.WARNING,
                        "Attribute {1} of entity {0} is no set. Defulat value \"0\".",
                        new Object[]{HOURS_FIELD, geCond.getID()});
            }

            GraphAttribute gaMins = null;
            try {
                gaMins = geCond
                        .getAttributeByName("MinutesField");
            } catch (NotFound ex) {
                logger.log(Level.SEVERE,
                        "Entity {0} hasn't got attribute {1}", new Object[]{geCond.getID(), "MinutesField"});
                System.exit(-1);
            }
            int mins = 0;
            if (!gaMins.getSimpleValue().equals("")) {
                mins = Integer.parseInt(gaMins.getSimpleValue());
            } else {
                logger.log(Level.WARNING,
                        "Attribute {1} of entity {0} is no set. Defulat value \"0\".",
                        new Object[]{"MinutesField", geCond.getID()});
            }

            GraphAttribute gaSecs = null;
            try {
                gaSecs = geCond
                        .getAttributeByName(SECS_FIELD);
            } catch (NotFound ex) {
                logger.log(Level.SEVERE,
                        "Entity {0} hasn't got attribute {1}", new Object[]{geCond.getID(), SECS_FIELD});
                System.exit(-1);
            }
            int secs = 0;
            if (!gaSecs.getSimpleValue().equals("")) {
                secs = Integer.parseInt(gaSecs.getSimpleValue());
            } else {
                logger.log(Level.WARNING,
                        "Attribute {1} of entity {0} is no set. Defulat value \"0\".",
                        new Object[]{SECS_FIELD, geCond.getID()});
            }
            return "new PastTimeCondition(" + String.valueOf(hours) + "," + String.valueOf(mins) + "," + String.valueOf(secs) + ")";
        } else if (type.equals(CTIMER_TYPE)) {
            GraphAttribute gaHours = null;
            try {
                gaHours = geCond.getAttributeByName(HOURS_FIELD);
            } catch (NotFound ex) {
                logger.log(Level.SEVERE,
                        "Entity {0} hasn't got attribute {1}", new Object[]{geCond.getID(), HOURS_FIELD});
                System.exit(-1);
            }
            int hours = 0;
            if (!gaHours.getSimpleValue().equals("")) {
                hours = Integer.parseInt(gaHours.getSimpleValue());
            } else {
                logger.log(Level.WARNING,
                        "Attribute {1} of entity {0} is no set. Defulat value \"0\".",
                        new Object[]{HOURS_FIELD, geCond.getID()});
            }

            GraphAttribute gaMins = null;
            try {
                gaMins = geCond
                        .getAttributeByName("MinutesField");
            } catch (NotFound ex) {
                logger.log(Level.SEVERE,
                        "Entity {0} hasn't got attribute {1}", new Object[]{geCond.getID(), "MinutesField"});
                System.exit(-1);
            }
            int mins = 0;
            if (!gaMins.getSimpleValue().equals("")) {
                mins = Integer.parseInt(gaMins.getSimpleValue());
            } else {
                logger.log(Level.WARNING,
                        "Attribute {1} of entity {0} is no set. Defulat value \"0\".",
                        new Object[]{"MinutesField", geCond.getID()});
            }

            GraphAttribute gaSecs = null;
            try {
                gaSecs = geCond
                        .getAttributeByName(SECS_FIELD);
            } catch (NotFound ex) {
                logger.log(Level.SEVERE,
                        "Entity {0} hasn't got attribute {1}", new Object[]{geCond.getID(), SECS_FIELD});
                System.exit(-1);
            }
            int secs = 0;
            if (!gaSecs.getSimpleValue().equals("")) {
                secs = Integer.parseInt(gaSecs.getSimpleValue());
            } else {
                logger.log(Level.WARNING,
                        "Attribute {1} of entity {0} is no set. Defulat value \"0\".",
                        new Object[]{SECS_FIELD, geCond.getID()});
            }
            return "new TimerFinishedCondition(" + String.valueOf(hours) + "," + String.valueOf(mins) + "," + String.valueOf(secs) + ")";
        } else if (type.equals(CINSIDE_HOUSE_TYPE) || type.equals(COUTSIDE_HOUSE_TYPE)) {
            GraphAttribute gaHuman = null;
            String humanId = null;
            try {
                gaHuman = geCond.getAttributeByName("HumanTarget");
                if (gaHuman != null && !gaHuman.getSimpleValue().equals("")) {
                    humanId = gaHuman.getSimpleValue();
                }
            } catch (NotFound ex) {
                logger.log(Level.WARNING,
                        "Entity {0} hasn't got attribute {1}", new Object[]{geCond.getID(), "HumanTarget"});

            }
            String sentence;
            if (humanId != null) {
                sentence = "new IsInsideHouseCondition(\"" + humanId + "\")";
            } else {
                sentence = "new IsInsideHouseCondition()";
            }
            if (type.equals(COUTSIDE_HOUSE_TYPE)) {
                sentence = negate(sentence);
            }
            return sentence;
        } else if (type.equals(CPROB_TYPE)) {
            try {
                GraphAttribute gaProb = geCond.getAttributeByName("ProbVarField");
                if (gaProb.getSimpleValue().equals("")) {
                    logger.log(Level.SEVERE,
                            "Attribute {0} of entity {1} is not set.",
                            new Object[]{"ProbVarField", geCond.getID()});
                    System.exit(-1);
                }
                float prob = Float.parseFloat(gaProb.getSimpleValue());
                return "new ProbCondition(" + prob + "f)";
            } catch (NotFound ex) {
                Logger.getLogger(ConditionGenerator.class.getName()).log(Level.SEVERE,
                        "Entity {0} hasn't got attribute {1}", new Object[]{geCond.getID(), "ProbVarField"});
                System.exit(-1);
            }
        } else if (type.equals(CEVENT_TYPE)) {
            GraphAttribute eventField = null;
            try {
                eventField = geCond.getAttributeByName("EventField");
            } catch (NotFound ex) {
                logger.log(Level.SEVERE, "Attribute {0} of entity {1} doesn't exit!",
                        new Object[]{"EventField", geCond.getID()});
                System.exit(-1);
            }
            if (eventField != null) {
                GraphEntity eventEntity = null;
                try {
                    eventEntity = eventField.getEntityValue();
                } catch (NullEntity ex) {
                    logger.log(Level.SEVERE, "Attribute {0} of entity {1} doesn't exit!",
                            new Object[]{"EventField", geCond.getID()});
                    System.exit(-1);
                }
                if (eventEntity != null) {
                    return "new EventCondition(\"" + Utils.replaceBadChars(eventEntity.getID()) + "\")";
                }
            }
        } else if (type.equals(CSYMP_TYPE)) {
            String symptomName = null;
            try {
                GraphAttribute ga = geCond.getAttributeByName("PDSymptomTypeField");
                if (ga.getSimpleValue().equals("")) {
                    throw new NotFound();
                }
                symptomName = Utils.replaceBadChars(ga.getSimpleValue());
            } catch (NotFound ex) {
                logger.log(Level.SEVERE, "Attribute {0} of entity {1} is not set",
                        new Object[]{"PDSymptomTypeField", geCond.getID()});
                System.exit(-1);
            }
            String symptomLevel = "";
            try {
                GraphAttribute ga = geCond.getAttributeByName("IntensityLevelField");
                if (ga.getSimpleValue().equals("")) {
                    throw new NotFound();
                }
                symptomLevel = Utils.replaceBadChars(ga.getSimpleValue());
            } catch (NotFound ex) {
                logger.log(Level.SEVERE, "Attribute {0} of entity {1} is not set",
                        new Object[]{"IntensityLevelField", geCond.getID()});
                System.exit(-1);
            }
            String bodyId = "";
            try {
                GraphAttribute ga = geCond.getAttributeByName("HumanTarget");
                if (ga.getSimpleValue().equals("")) {
                    return "new SymptomCondition(\"" + symptomName + "\", \"" + symptomLevel + "\")";
                }
                bodyId = Utils.replaceBadChars(ga.getSimpleValue());
                return "new SymptomCondition(\"" + symptomName + "\", \"" + symptomLevel + "\", \"" + bodyId + "\")";
            } catch (NotFound ex) {
                logger.log(Level.SEVERE, "Attribute {0} of entity {1} is not set",
                        new Object[]{"HumanTarget", geCond.getID()});
                System.exit(-1);
            }
        } else if (type.equals(CBODY_STATE)) {
            String bodyState = "";
            try {
                GraphAttribute ga = geCond.getAttributeByName("BodyStateField");
                if (ga.getSimpleValue().equals("")) {
                    throw new NotFound();
                }
                bodyState = Utils.replaceBadChars(ga.getSimpleValue());
            } catch (NotFound ex) {
                logger.log(Level.SEVERE, "Attribute {0} of entity {1} is not set",
                        new Object[]{"BodyStateField", geCond.getID()});
                System.exit(-1);
            }
            String bodyId = "";
            try {
                GraphAttribute ga = geCond.getAttributeByName("HumanTarget");
                if (ga.getSimpleValue().equals("")) {
                    return "new AgentBodyStateCondition(\"" + bodyState + "\")";
                }
                bodyId = Utils.replaceBadChars(ga.getSimpleValue());
                return "new AgentBodyStateCondition(\"" + bodyId + "\", \"" + bodyState + "\")";
            } catch (NotFound ex) {
                logger.log(Level.SEVERE, "Attribute {0} of entity {1} is not set",
                        new Object[]{"IntensityLevelField", geCond.getID()});
                System.exit(-1);
            }
        } else if (type.equals(CSAY)) {
            String message = Utils.getFieldValue(geCond, "MessageField", null, true);
            String result = "new SomeoneSayCondition(\""+message+"\")";
            String human = Utils.getFieldValue(geCond, "HumanTarget", null, false);
            if(human != null) {
                result += ".setHumanAgentSourceId(\""+human+"\")";
            }
            return result;
        } else if (type.equals(THING_IN_ROOM_STATE)) {
            String roomName = "";
            try {
                GraphAttribute ga = geCond.getAttributeByName("RoomIdField");
                if (ga.getSimpleValue().equals("")) {
                    throw new NotFound();
                }
                roomName = Utils.replaceBadChars(ga.getSimpleValue());
            } catch (NotFound ex) {
                logger.log(Level.SEVERE, "Attribute {0} of entity {1} is not set",
                        new Object[]{"RoomIdField", geCond.getID()});
                System.exit(-1);
            }
            String objId = "";
            try {
                GraphAttribute ga = geCond.getAttributeByName("ThingField");
                if (ga.getSimpleValue().equals("")) {
                    return "new IsObjInRoomCondition(\"" + roomName + "\")";
                }
                objId = Utils.replaceBadChars(ga.getSimpleValue());
                return "new IsObjInRoomCondition(\"" + objId + "\", \"" + roomName + "\")";
            } catch (NotFound ex) {
                logger.log(Level.SEVERE, "Attribute {0} of entity {1} is not set",
                        new Object[]{"ThingField", geCond.getID()});
                System.exit(-1);
            }
        } else if (type.equals(CDAY_OF_THE_WEEK)) {
            try {
                GraphAttribute gaProb = geCond.getAttributeByName("DayOfTheWeekField");
                if (gaProb.getSimpleValue().equals("")) {
                    logger.log(Level.SEVERE,
                            "Attribute {0} of entity {1} is not set.",
                            new Object[]{"DayOfTheWeekField", geCond.getID()});
                    System.exit(-1);
                }
                return "new DayCondition(DayCondition.DAY_OF_THE_WEEK." + gaProb.getSimpleValue() + ")";
            } catch (NotFound ex) {
                Logger.getLogger(ConditionGenerator.class.getName()).log(Level.SEVERE,
                        "Entity {0} hasn't got attribute {1}", new Object[]{geCond.getID(), "ProbVarField"});
                System.exit(-1);
            }
        } else if (type.equals(CWEIGHT)) {
            try {
                GraphAttribute gaWeight = geCond.getAttributeByName("ObjWeightInGramsField");
                if (gaWeight.getSimpleValue().equals("")) {
                    logger.log(Level.SEVERE,
                            "Attribute {0} of entity {1} is not set.",
                            new Object[]{"ObjWeightInGramsField", geCond.getID()});
                    System.exit(-1);
                }
                return "new ObjectWeightCondition(\"obj\"," + gaWeight.getSimpleValue() + "f)";
            } catch (NotFound ex) {
                Logger.getLogger(ConditionGenerator.class.getName()).log(Level.SEVERE,
                        "Entity {0} hasn't got attribute {1}", new Object[]{geCond.getID(), "ProbVarField"});
                System.exit(-1);
            }
        } else if (type.equals(NOT_CONDITION)) {
            GraphEntity condition = Utils.getTargetEntity(geCond, "relatedCondition");
            if (condition == null) {
                logger.log(Level.SEVERE,
                        "{0} Entity is not connected with any condition.",
                        new Object[]{geCond.getID()});
                System.exit(-1);
            }
            return negate(getCondition(condition));
        } else {
            logger.log(Level.SEVERE, "Condition {0} is not supported.",
                    new Object[]{geCond.getID()});
            System.exit(-1);
        }
        return "";
    }

    public static String negate(String condSentence) {
        return "new NegateCondition(" + condSentence + ")";
    }
}
