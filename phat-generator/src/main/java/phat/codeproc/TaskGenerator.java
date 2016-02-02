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
import ingenias.generator.browser.Browser;
import ingenias.generator.browser.Graph;
import ingenias.generator.browser.GraphAttribute;
import ingenias.generator.browser.GraphEntity;
import ingenias.generator.datatemplate.Repeat;
import ingenias.generator.datatemplate.Sequences;
import ingenias.generator.datatemplate.Var;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import phat.codeproc.pd.PDGenerator;

public class TaskGenerator {

    final static Logger logger = Logger.getLogger(TaskGenerator.class.getName());

    enum EntityType {

        SequentialTaskDiagram
    };
    static Map<String, String> entityToAutomatonMap = new HashMap();
    private Browser browser;
    private Sequences sequence;

    public TaskGenerator(Browser browser, Sequences sequence) {
        super();
        this.browser = browser;
        this.sequence = sequence;
        entityToAutomatonMap.put("BGetUpFromBed", "StandUpAutomaton");
        entityToAutomatonMap.put("GoIntoBed", "GoIntoBedAutomaton");
        entityToAutomatonMap.put("OpenTask", "OpenObjectAutomaton");
        entityToAutomatonMap.put("CloseTask", "CloseObjectAutomaton");
        entityToAutomatonMap.put("BGoToTask", "MoveToSpace");
        entityToAutomatonMap.put("GoToBodyLoc", "MoveToBodyLocAutomaton");
        entityToAutomatonMap.put("WaitForBodyClose", "WaitForCloseToBodyAutomaton");
        entityToAutomatonMap.put("BUseTask", "UseObjectAutomaton");
        entityToAutomatonMap.put("BSequentialTask", "");
        entityToAutomatonMap.put("TakeOffTask", "TakeOffClothingAutomaton");
        entityToAutomatonMap.put("PutOnTask", "PutOnClothingAutomaton");
        entityToAutomatonMap.put("SitDown", "SitDownAutomaton");
        entityToAutomatonMap.put("BPickUpTask", "PickUpSomething");
        entityToAutomatonMap.put("BLeaveTask", "LeaveSomethingIn");
        entityToAutomatonMap.put("Drink", "DrinkAutomaton");
        entityToAutomatonMap.put("Eat", "EatAutomaton");
        entityToAutomatonMap.put("StandUp", "StandUpAutomaton");
        entityToAutomatonMap.put("FallSleep", "SleepAutomaton");
        entityToAutomatonMap.put("WaitTask", "DoNothing");
        entityToAutomatonMap.put("SayTask", "SayAutomaton");
        entityToAutomatonMap.put("FallTask", "FallAutomaton");
        entityToAutomatonMap.put("TapXYTask", "PressOnScreenXYAutomaton");

    }

    public void generateAllSeqTasks() throws NotFound {
        System.out.println("generateAllSeqTasks.............................");
        for (Graph std : Utils.getGraphsByType("SequentialTaskDiagram", browser)) {
            System.out.println(">" + std.getType() + ":" + Utils.replaceBadChars(std.getID()));
            Repeat rep = new Repeat("tasks");
            rep.add(new Var("stID", Utils.replaceBadChars(std.getID())));
            rep.add(new Var("stType", Utils.replaceBadChars(std.getType())));
            sequence.addRepeat(rep);

            generateSeqTaskDiagram(std, rep);
        }
        System.out.println(".............................generateAllSeqTasks");
    }

    private void generateSeqTaskDiagram(Graph std, Repeat repFather)
            throws NotFound {
        GraphEntity task = Utils.getFirstEntity(std);
        while (task != null) {
            List<String> params = fillConstructorParams(task);

            Repeat rep = new Repeat("subTasks");
            repFather.add(rep);
            rep.add(new Var("className", Utils.replaceBadChars(params.get(0))));
            rep.add(new Var("interrup", isCanBeInterrupted(task)));
            rep.add(new Var("desc", getFieldValue(task, "Description", "", false)));
            rep.add(new Var("eID", Utils.replaceBadChars(task.getID())));
            rep.add(new Var("eType", Utils.replaceBadChars(task.getType())));

            if (params.size() > 1) {
                Repeat r3params = new Repeat("3params");
                r3params.add(new Var("v3", params.get(1)));
                rep.add(r3params);
                if (params.size() > 2) {
                    Repeat r4params = new Repeat("4params");
                    r4params.add(new Var("v4", params.get(2)));
                    r3params.add(r4params);
                }
            }

            if (hasField(task, "BTaskDuration")) {
                String duration = getFieldValue(task, "BTaskDuration", "0", false);
                if (!duration.equals("") && Integer.parseInt(duration) > 0) {
                    Repeat durRep = new Repeat("durRep");
                    durRep.add(new Var("duration", duration));
                    rep.add(durRep);
                }
            }

            if (hasField(task, "SpeedField")) {
                Repeat speedRep = new Repeat("speedRep");
                speedRep.add(new Var("speed", getFieldValue(task, "SpeedField", "null", false)));
                rep.add(speedRep);
            }

            if (hasField(task, "XPosOnScreen")) {
                Repeat xyParams = new Repeat("setXY");
                xyParams.add(new Var("x", getFieldValue(task, "XPosOnScreen", "null", true)));
                xyParams.add(new Var("y", getFieldValue(task, "YPosOnScreen", "null", true)));
                rep.add(xyParams);
            }

            GraphEntity event = Utils
                    .getTargetEntity(task, "ProducesEvent");
            if (event != null) {
                Repeat eRep = new Repeat("events");
                eRep.add(new Var("eventName", Utils.replaceBadChars(event.getID())));
                rep.add(eRep);
            }
            task = nextTask(task, std);
        }
    }

    public GraphEntity nextTask(GraphEntity task, Graph std) {
        return Utils.getTargetEntity(task, "NextSeqTask",
                std.getRelationships());
    }

    private static String isCanBeInterrupted(GraphEntity task) {
        try {
            GraphAttribute ga = task
                    .getAttributeByName("CanBeInterruptedField");
            String value = ga.getSimpleValue();
            if (value.equals("No")) {
                return "false";
            }
        } catch (NotFound e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "true";
    }

    private static boolean hasField(GraphEntity task, String fieldName) {
        GraphAttribute d;
        try {
            d = task.getAttributeByName(fieldName);
        } catch (NotFound ex) {
            return false;
        }
        return d != null && !d.getSimpleValue().equals("");
    }

    private static String getFieldValue(GraphEntity task, String fieldName, String def, boolean mandatory) {
        GraphAttribute at = null;
        try {
            at = task.getAttributeByName(fieldName);
        } catch (NotFound ex) {
        }
        if (at == null || at.getSimpleValue().equals("")) {
            if (mandatory) {
                logger.log(Level.SEVERE, "Attribute {0} of {1} is empty!",
                        new Object[]{fieldName, task.getID()});
                System.exit(0);
            } else {
                logger.log(Level.WARNING, "Attribute {0} of {1} is empty!",
                        new Object[]{fieldName, task.getID()});
                return def;
            }
        }
        return at.getSimpleValue();
    }

    public static List<String> fillConstructorParams(GraphEntity taskGE)
            throws NotFound {

        List params = new ArrayList<>();
        String className = "";
        if (taskGE.getType().equals("BSequentialTask")) {
            className = Utils.replaceBadChars(getFieldValue(taskGE, "SeqTaskDiagramField", "null", true)) + "Task";
        } else {
            className = entityToAutomatonMap.get(taskGE.getType());
        }
        params.add(className);

        if (taskGE.getType().equals("GoIntoBed")) {
        } else if (taskGE.getType().equals("OpenTask") || taskGE.getType().equals("CloseTask")) {
            params.add(getFieldValue(taskGE, "OpenCloseObjField", "null", true));
        } else if (taskGE.getType().equals("BGoToTask")) {
            params.add(getFieldValue(taskGE, "SpaceToGoField", "null", true));
        } else if (taskGE.getType().equals("GoToBodyLoc")) {
            params.add(getFieldValue(taskGE, "HumanTarget", "null", true));
        } else if (taskGE.getType().equals("WaitForBodyClose")) {
            params.add(getFieldValue(taskGE, "HumanTarget", "null", false));
        } else if (taskGE.getType().equals("BUseTask")) {
            params.add(getFieldValue(taskGE, "BUseObjectField", "null", true));
        } else if (taskGE.getType().equals("TakeOffTask") || taskGE.getType().equals("PutOnTask")) {
            params.add(getFieldValue(taskGE, "WearableObjField", "null", false));
        } else if (taskGE.getType().equals("SitDown")) {
            params.add(getFieldValue(taskGE, "SeatField", "null", false));
        } else if (taskGE.getType().equals("BPickUpTask")) {
            params.add(getFieldValue(taskGE, "PysicalMobObjField", "null", true));
        } else if (taskGE.getType().equals("BLeaveTask")) {
            params.add(getFieldValue(taskGE, "PysicalMobObjField", "null", true));
            params.add(getFieldValue(taskGE, "DestinyField", "null", true));
        } else if (taskGE.getType().equals("Drink")) {
            params.add(getFieldValue(taskGE, "DrinkItemField", "null", false));
        } else if (taskGE.getType().equals("Eat")) {
            params.add(getFieldValue(taskGE, "EatableItemField", "null", false));
        } else if (taskGE.getType().equals("SayTask")) {
            params.add(getFieldValue(taskGE, "MessageField", "null", true));
        } else if (taskGE.getType().equals("TapXYTask")) {
            params.add(getFieldValue(taskGE, "TargetSmartphone", "null", true));
        }
        return params;
    }
}
