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

import phat.agents.Agent;
import phat.agents.automaton.SayAutomaton;
import ingenias.exception.NotFound;
import ingenias.generator.browser.Browser;
import ingenias.generator.browser.Graph;
import ingenias.generator.browser.GraphAttribute;
import ingenias.generator.browser.GraphEntity;
import ingenias.generator.datatemplate.Repeat;
import ingenias.generator.datatemplate.Sequences;
import ingenias.generator.datatemplate.Var;

public class TaskGenerator {

    final static String SEQ_TASK_DIAG = "SequentialTaskDiagram";
    final static String TYPE_GET_UP_FROM_BED_TASK = "BGetUpFromBed";
    final static String TYPE_GO_INTO_BED_TASK = "GoIntoBed";
    final static String TYPE_GO_TO_TASK = "BGoToTask";
    final static String TYPE_USE_TASK = "BUseTask";
    final static String TYPE_STAND_UP_TASK = "StandUp";
    private Browser browser;
    private Sequences sequence;

    public TaskGenerator(Browser browser, Sequences sequence) {
        super();
        this.browser = browser;
        this.sequence = sequence;
    }

    public void generateAllSeqTasks() throws NotFound {
        System.out.println("generateAllSeqTasks.............................");
        for (Graph std : Utils.getGraphsByType(SEQ_TASK_DIAG, browser)) {
            System.out.println(">" + std.getType() + ":" + std.getID());
            Repeat rep = new Repeat("tasks");
            rep.add(new Var("taskName", std.getID()));
            sequence.addRepeat(rep);

            generateSeqTaskDiagram(std, rep);
        }
        System.out.println(".............................generateAllSeqTasks");
    }

    private void generateSeqTaskDiagram(Graph std, Repeat repFather) throws NotFound {
        GraphEntity task = Utils.getFirstEntity(std);
        while (task != null) {
            String sentence = getNewTaskInstanceSentence(task);
            sentence += ".setMetadata(\"SOCIAALML_ENTITY_ID\",\""+task.getID()+"\")\n"
                    + ".setMetadata(\"SOCIAALML_ENTITY_TYPE\",\""+task.getType()+"\")";
            System.out.println(">>" + sentence);
            if (sentence != null) {
                Repeat rep = new Repeat("subTasks");
                rep.add(new Var("subTaskInst", sentence));
                repFather.add(rep);

                GraphEntity event = Utils.getTargetEntity(task, "ProducesEvent");
                if (event != null) {
                    Repeat eRep = new Repeat("events");
                    eRep.add(new Var("eventName", event.getID()));
                    rep.add(eRep);
                }
            }
            task = nextTask(task, std);
        }
    }

    public GraphEntity nextTask(GraphEntity task, Graph std) {
        return Utils.getTargetEntity(task, "NextSeqTask", std.getRelationships());
    }

    private static String isCanBeInterrupted(GraphEntity task) {
        try {
            GraphAttribute ga = task.getAttributeByName("CanBeInterruptedField");
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

    public static String getNewTaskInstanceSentence(GraphEntity taskGE) throws NotFound {
        String canBeIterrupted = isCanBeInterrupted(taskGE);
        if (taskGE.getType().equals(TYPE_GET_UP_FROM_BED_TASK)) {
            System.out.println("Task: " + taskGE.getID());
            GraphAttribute durationGA = taskGE.getAttributeByName("BTaskDuration");
            int duration = Integer.parseInt(durationGA.getSimpleValue());
            return "new StandUpAutomaton( agent, " + "\"" + taskGE.getID() + "\"" + "\n"
                    + ").setFinishCondition(new TimerFinishedCondition(0, 0, " + duration + "))"
                    + ".setCanBeInterrupted(" + canBeIterrupted + ")";
        } else if (taskGE.getType().equals(TYPE_GO_INTO_BED_TASK)) {
            System.out.println("Task: " + taskGE.getID());
            GraphAttribute durationGA = taskGE.getAttributeByName("BTaskDuration");
            int duration = Integer.parseInt(durationGA.getSimpleValue());
            return "new GoIntoBedAutomaton( agent, null)" + "\n"
                    + ".setFinishCondition(new TimerFinishedCondition(0, 0, " + duration + "))"
                    + ".setCanBeInterrupted(" + canBeIterrupted + ")";
        } else if (taskGE.getType().equals(TYPE_GO_TO_TASK)) {
            GraphAttribute ga = taskGE.getAttributeByName("SpaceToGoField");
            if (ga.getSimpleValue() != null && !ga.getSimpleValue().equals("")) {
                String automaton = "new MoveToSpace(agent, \"" + taskGE.getID() + "\", \"" + ga.getSimpleValue() + "\")" + "\n";
                GraphAttribute speedGA = taskGE.getAttributeByName("SpeedField");
                if (speedGA.getSimpleValue() != null && !speedGA.getSimpleValue().equals("")) {
                    automaton += ".setSpeed("+speedGA.getSimpleValue() + "f)";
                }
                automaton += ".setCanBeInterrupted(" + canBeIterrupted + ")";
                return automaton;
            }
        } else if (taskGE.getType().equals("GoToBodyLoc")) {
            GraphAttribute humanGA = taskGE.getAttributeByName("HumanTarget");
            if (humanGA.getSimpleValue() != null && !humanGA.getSimpleValue().equals("")) {
                String automaton = "new MoveToBodyLocAutomaton(agent, \"" + taskGE.getID() + "\", \"" + humanGA.getSimpleValue() + "\")" + "\n";
                GraphAttribute speedGA = taskGE.getAttributeByName("SpeedField");
                if (speedGA.getSimpleValue() != null && !speedGA.getSimpleValue().equals("")) {
                    automaton += ".setSpeed("+speedGA.getSimpleValue() + "f)";
                }
                automaton += ".setCanBeInterrupted(" + canBeIterrupted + ")";
                return automaton;
            }
        } else if (taskGE.getType().equals(TYPE_USE_TASK)) {
            GraphAttribute durationGA = taskGE.getAttributeByName("BTaskDuration");
            int duration = Integer.parseInt(durationGA.getSimpleValue());
            GraphAttribute objGA = taskGE.getAttributeByName("BUseObjectField");
            return "UseObjectAutomatonFactory.getAutomaton( agent, " + "\"" + objGA.getSimpleValue() + "\"" + "\n"
                    + ").setFinishCondition(new TimerFinishedCondition(0, 0, " + duration + "))" + "\n"
                    + ".setCanBeInterrupted(" + canBeIterrupted + ")";
        } else if (taskGE.getType().equals("BSequentialTask")) {
            GraphAttribute diagRef = taskGE.getAttributeByName("SeqTaskDiagramField");
            if(!diagRef.getSimpleValue().equals("")) {
            return "new " + diagRef.getSimpleValue() + "Task(agent)" + "\n"
                    + ".setCanBeInterrupted(" + canBeIterrupted + ")";
            } else {
                return "null";
            }
        } else if (taskGE.getType().equals("TakeOffTask")) {
            GraphAttribute durationGA = taskGE.getAttributeByName("BTaskDuration");
            int duration = Integer.parseInt(durationGA.getSimpleValue());
            return "new TakeOffClothingAutomaton(agent, \"" + taskGE.getID() + "\")" + "\n"
                    + ".setFinishCondition(new TimerFinishedCondition(0, 0, " + duration + "))" + "\n"
                    + ".setCanBeInterrupted(" + canBeIterrupted + ")";
        } else if (taskGE.getType().equals("PutOnTask")) {
            GraphAttribute durationGA = taskGE.getAttributeByName("BTaskDuration");
            int duration = Integer.parseInt(durationGA.getSimpleValue());
            return "new PutOnClothingAutomaton(agent, \"" + taskGE.getID() + "\")" + "\n"
                    + ".setFinishCondition(new TimerFinishedCondition(0, 0, " + duration + "))" + "\n"
                    + ".setCanBeInterrupted(" + canBeIterrupted + ")";
        } else if (taskGE.getType().equals("SitDown")) {
            GraphAttribute ga = taskGE.getAttributeByName("SeatField");
            if (ga.getSimpleValue() != null && !ga.getSimpleValue().equals("")) {
                return "new SitDownAutomaton(agent, \"" + ga.getSimpleValue() + "\")" + "\n"
                        + ".setCanBeInterrupted(" + canBeIterrupted + ")";
            }
        } else if (taskGE.getType().equals("Drink")) {
            GraphAttribute durationGA = taskGE.getAttributeByName("BTaskDuration");
            int duration = Integer.parseInt(durationGA.getSimpleValue());
            return "new DrinkAutomaton(agent)" + "\n"
                    + ".setFinishCondition(new TimerFinishedCondition(0, 0, " + duration + "))" + "\n"
                    + ".setCanBeInterrupted(" + canBeIterrupted + ")";
        } else if (taskGE.getType().equals(TYPE_STAND_UP_TASK)) {
            GraphAttribute durationGA = taskGE.getAttributeByName("BTaskDuration");
            int duration = Integer.parseInt(durationGA.getSimpleValue());
            return "new StandUpAutomaton( agent, " + "\"" + taskGE.getID() + "\"" + ")" + "\n"
                    + ".setFinishCondition(new TimerFinishedCondition(0, 0, " + duration + "))" + "\n"
                    + ".setCanBeInterrupted(" + canBeIterrupted + ")";
        } else if (taskGE.getType().equals("WaitTask")) {
            GraphAttribute durationGA = taskGE.getAttributeByName("BTaskDuration");
            int duration = Integer.parseInt(durationGA.getSimpleValue());
            return "new DoNothing(agent,\"" + taskGE.getID() + "\")" + "\n"
                    + ".setFinishCondition(new TimerFinishedCondition(0, 0, " + duration + "))"
                    + ".setCanBeInterrupted(" + canBeIterrupted + ")";
        } else if (taskGE.getType().equals("SayTask")) {
            GraphAttribute message = taskGE.getAttributeByName("MessageField");
            return "new SayAutomaton(agent, \"" + taskGE.getID() + "\", \"" + message.getSimpleValue() + "\", 0.5f)";
        } else if (taskGE.getType().equals("FallTask")) {
            return "new FallAutomaton(agent, \"" + taskGE.getID() + "\")";
        } else if (taskGE.getType().equals("TapXYTask")) {
            GraphAttribute xGA = taskGE.getAttributeByName("XPosOnScreen");
            int x = Integer.parseInt(xGA.getSimpleValue());
            GraphAttribute yGA = taskGE.getAttributeByName("YPosOnScreen");
            int y = Integer.parseInt(yGA.getSimpleValue());
            GraphAttribute tsGA = taskGE.getAttributeByName("TargetSmartphone");
            String deviceId = tsGA.getSimpleValue();
            return "new PressOnScreenXYAutomaton( agent, " + "\"" + taskGE.getID() + "\""
                    + ", \"" + deviceId + "\", " + x + ", " + y + ")";
        }
        return null;
    }
}
