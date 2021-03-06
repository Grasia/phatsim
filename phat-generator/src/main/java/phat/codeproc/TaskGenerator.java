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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        entityToAutomatonMap.put("SwitchLightTask", "SwitchLight");
        entityToAutomatonMap.put("DropObj", "DropObjTask");
        entityToAutomatonMap.put("PlayAnimationTask", "PlayAnimation");
        entityToAutomatonMap.put("BWakeUpTask", "DoNothing");
        entityToAutomatonMap.put("SwipeTask", "SlideFingerOnScreenAutomaton");
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
            System.out.println("Task = " + task.getID());
            List<String> params = fillConstructorParams(task);

            Repeat rep = new Repeat("subTasks");
            repFather.add(rep);
            rep.add(new Var("className", Utils.replaceBadChars(params.get(0))));
            rep.add(new Var("interrup", isCanBeInterrupted(task)));
            rep.add(new Var("desc", Utils.getFieldValue(task, "Description", "", false)));
            rep.add(new Var("eID", Utils.replaceBadChars(task.getID())));
            rep.add(new Var("eType", Utils.replaceBadChars(task.getType())));

            if (task.getType().equals("BSequentialTask") || task.getType().equals("BRandomTask")) {
                try {
                    ActivityGenerator.addPararms(task, rep);
                } catch (NullEntity ex) {
                    Logger.getLogger(TaskGenerator.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

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
                GraphEntity var = Utils.getTargetEntity(task, "durationVar");
                String duration = Utils.getFieldValue(task, "BTaskDuration", "-1", false);
                if (var != null || (!duration.equals("") && Double.parseDouble(duration) > 0)) {
                    Repeat durRep = new Repeat("durRep");
                    if (var != null) {
                        durRep.add(new Var("durVar", Utils.replaceBadChars(var.getID())));
                    }
                    durRep.add(new Var("duration", duration));
                    rep.add(durRep);
                }
            }

            if (hasField(task, "SpeedField")) {
                GraphEntity var = null;
                if (task.getType().equals("BGoToTask")) {
                    var = Utils.getTargetEntity(task, "goToSpeedVar");
                } else if (task.getType().equals("GoToBodyLoc")) {
                    var = Utils.getTargetEntity(task, "goToBodySpeedVar");
                }
                String speed = Utils.getFieldValue(task, "SpeedField", "-1", false);
                if (var != null || (!speed.equals("") && Double.parseDouble(speed) > 0)) {
                    Repeat speedRep = new Repeat("speedRep");
                    if (var != null) {
                        speedRep.add(new Var("speedVar", Utils.replaceBadChars(var.getID())));
                    }
                    speedRep.add(new Var("speed", speed));
                    rep.add(speedRep);
                }
            }

            if (hasField(task, "XPosOnScreen")) {
                Repeat xyParams = new Repeat("setXY");
                String x = getVarValue(task, "tapXVar", Utils.getFieldValue(task, "XPosOnScreen", "null", true));
                xyParams.add(new Var("x", x));
                String y = getVarValue(task, "tapYVar", Utils.getFieldValue(task, "YPosOnScreen", "null", true));
                xyParams.add(new Var("y", y));
                rep.add(xyParams);
            }
            
            if(hasField(task, "StartXField")) {
                Repeat coordsParams = new Repeat("setCoords");
                String xSource = getVarValue(task, "swipeXSource", Utils.getFieldValue(task, "StartXField", "null", true));
                coordsParams.add(new Var("xSource", xSource));
                String ySource = getVarValue(task, "swipeYSource", Utils.getFieldValue(task, "StartYField", "null", true));
                coordsParams.add(new Var("ySource", ySource));
                String xTarget = getVarValue(task, "swipeXTarget", Utils.getFieldValue(task, "EndXField", "null", true));
                coordsParams.add(new Var("xTarget", xTarget));
                String yTarget = getVarValue(task, "swipeYTarget", Utils.getFieldValue(task, "EndYField", "null", true));
                coordsParams.add(new Var("yTarget", yTarget));
                rep.add(coordsParams);
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
        return true;//d != null && !d.getSimpleValue().equals("");
    }

    public static List<String> fillConstructorParams(GraphEntity taskGE)
            throws NotFound {

        List params = new ArrayList<>();
        String className = "";
        if (taskGE.getType().equals("BSequentialTask")) {
            className = Utils.replaceBadChars(Utils.getFieldValue(taskGE, "SeqTaskDiagramField", "null", true)) + "Task";
        } else {
            className = entityToAutomatonMap.get(taskGE.getType());
        }
        params.add(className);

        if (taskGE.getType().equals("GoIntoBed")) {
            params.add("\"Bed1\"");
        } else if (taskGE.getType().equals("OpenTask")) {
            String open = getVarValue(taskGE, "openObjVar", Utils.getFieldValue(taskGE, "OpenCloseObjField", "null", true));
            params.add(open);
        } else if (taskGE.getType().equals("CloseTask")) {
            String close = getVarValue(taskGE, "closeObjVar", Utils.getFieldValue(taskGE, "OpenCloseObjField", "null", true));
            params.add(close);
        } else if (taskGE.getType().equals("BGoToTask")) {
            String go = getVarValue(taskGE, "goToPlaceVar", Utils.getFieldValue(taskGE, "SpaceToGoField", "null", true));
            params.add(go);
        } else if (taskGE.getType().equals("GoToBodyLoc")) {
            String go = getVarValue(taskGE, "goToBodyVar", Utils.getFieldValue(taskGE, "HumanTarget", "null", true));
            params.add(go);
        } else if (taskGE.getType().equals("WaitForBodyClose")) {
            String body = getVarValue(taskGE, "waitForHumanVar", Utils.getFieldValue(taskGE, "HumanTarget", "null", false));
            params.add(body);
        } else if (taskGE.getType().equals("BUseTask")) {
            String use = getVarValue(taskGE, "useObjVar", Utils.getFieldValue(taskGE, "BUseObjectField", "null", true));
            params.add(use);
        } else if (taskGE.getType().equals("TakeOffTask")) {
            String puton = getVarValue(taskGE, "putOnWearableVar", Utils.getFieldValue(taskGE, "WearableObjField", "null", false));
            params.add(puton);
        } else if (taskGE.getType().equals("PutOnTask")) {
            String putoff = getVarValue(taskGE, "putOffWearableVar", Utils.getFieldValue(taskGE, "WearableObjField", "null", false));
            params.add(putoff);
        } else if (taskGE.getType().equals("SitDown")) {
            String sit = getVarValue(taskGE, "sitDownOnSeatVar", Utils.getFieldValue(taskGE, "SeatField", "null", false));
            params.add(sit);
        } else if (taskGE.getType().equals("BPickUpTask")) {
            String pickUp = getVarValue(taskGE, "pickUpObjVar", Utils.getFieldValue(taskGE, "PysicalMobObjField", "null", true));
            params.add(pickUp);
        } else if (taskGE.getType().equals("BLeaveTask")) {
            String obj = getVarValue(taskGE, "leaveObjVar", Utils.getFieldValue(taskGE, "PysicalMobObjField", "null", true));
            params.add(obj);
            String destiny = getVarValue(taskGE, "leaveDestinyVar", Utils.getFieldValue(taskGE, "DestinyField", "null", true));
            params.add(destiny);
        } else if (taskGE.getType().equals("Drink")) {
            params.add("\"" + Utils.getFieldValue(taskGE, "DrinkItemField", "null", false) + "\"");
        } else if (taskGE.getType().equals("Eat")) {
            params.add("\"" + Utils.getFieldValue(taskGE, "EatableItemField", "null", false) + "\"");
        } else if (taskGE.getType().equals("SayTask")) {
            String message = getVarValue(taskGE, "messageVar", Utils.getFieldValue(taskGE, "MessageField", "null", true));
            params.add(message);
        } else if (taskGE.getType().equals("TapXYTask")) {
            String device = getVarValue(taskGE, "tapDeviceVar", Utils.getFieldValue(taskGE, "TargetSmartphone", "null", true));
            params.add(device);
        } else if (taskGE.getType().equals("SwipeTask")) {
            String device = getVarValue(taskGE, "swipeDeviceVar", Utils.getFieldValue(taskGE, "TargetSmartphone", "null", true));
            params.add(device);
        } else if (taskGE.getType().equals("DropObj")) {
            String obj = getVarValue(taskGE, "dropObjVar", Utils.getFieldValue(taskGE, "PysicalMobObjField", "null", true));
            params.add(obj);
        } else if (taskGE.getType().equals("SwitchLightTask")) {
            String switchRoomVar = getVarValue(taskGE, "switchRoomVar", Utils.getFieldValue(taskGE, "RoomField", "null", true));
            params.add(switchRoomVar);
            params.add("\"" + Utils.getFieldValue(taskGE, "ONOFFStateField", "null", true) + "\"");
        } else if (taskGE.getType().equals("PlayAnimationTask")) {
            String switchRoomVar = getVarValue(taskGE, "animVar", Utils.getFieldValue(taskGE, "AnimNameField", "null", true));
            params.add(switchRoomVar);
        }
        return params;
    }

    private static String getVarValue(GraphEntity task, String varRel, String value) {
        GraphEntity ge = Utils.getTargetEntity(task, varRel);
        String resultValue = value.equals("null") ? "null" : "\"" + value + "\"";
        if (ge != null) {
            String varValue = "getParent() != null && getParent().getMetadata(\"" + Utils.replaceBadChars(ge.getID()) + "\")";
            return varValue + " != null ? " + varValue + " : " + resultValue;
        }
        return resultValue;
    }
}
