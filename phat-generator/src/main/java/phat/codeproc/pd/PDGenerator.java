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
package phat.codeproc.pd;

import ingenias.exception.NotFound;
import ingenias.exception.NullEntity;
import ingenias.generator.browser.Browser;
import ingenias.generator.browser.Graph;
import ingenias.generator.browser.GraphAttribute;
import ingenias.generator.browser.GraphCollection;
import ingenias.generator.browser.GraphEntity;
import ingenias.generator.browser.GraphRelationship;
import ingenias.generator.datatemplate.Repeat;
import ingenias.generator.datatemplate.Sequences;
import ingenias.generator.datatemplate.Var;

import java.util.Collection;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import phat.codeproc.ConditionGenerator;
import phat.codeproc.Utils;

/**
 *
 * @author pablo
 */
public class PDGenerator {

    final static Logger logger = Logger.getLogger(PDGenerator.class.getName());
    static final String PARKINSON_PROFILE = "ParkinsonsProfile";
    static final String PARKINSON_PROFILE_SPEC_DIAGRAM = "ParkinsonSpecDiagram";
    static final String PD_STAGE = "PDDiseaseStage";
    static final String PD_SYMPTOM = "PDSymptom";
    static final String LOW_FILTER = "LOWTaskFilterR";
    static final String MEDIUM_FILTER = "MEDIUMTaskFilterR";
    static final String HIGH_FILTER = "HIGHTaskFilterR";
    static final String SYMPTOMS = "Symptoms";
    static final String LIMITATIONS = "Limitations";
    static final String TASK_ALLOWED = "TaskAllowed";
    static final String FILTER_DIAGRAM = "FilterDiagram";
    static final String ALLOWED_TASK_REL = "AllowedTask";
    static final String NEXT_FILTER_REL = "NextFilter";
    static final String ALTERNATIVE_REL = "FAlternative";
    static final String PRECONDITION_REL = "FPrecondition";
    static final String SELECTOR_FILTER_TYPE = "FTaskSelectorFilter";
    static final String DELAY_FILTER_TYPE = "FDelayFilter";
    static final String PLACE_FILTER_TYPE = "FModifyPlaceFilter";
    static final String REPLACE_TASK_FILTER_TYPE = "FReplaceTaskFilter";
    static final String TARGET_OBJ_FILTER_TYPE = "FChangeTargetObjFilter";
    static final String CHANGE_TOOL_FILTER_TYPE = "FChangeToolFilter";
    static final String UNABLE_FILTER_TYPE = "FUnableFilter";
    Browser browser;

    public PDGenerator(Browser browser) {
        this.browser = browser;
    }

    public void generatePD(Sequences seq, GraphEntity actor) throws NullEntity, NotFound {
        Vector<Graph> diagramsPD = getPDDiagramsForActor(actor);

        for (Graph diagram : diagramsPD) {
            Repeat rep = new Repeat("diseaseProfile");
            seq.addRepeat(rep);
            rep.add(new Var("actorname", Utils.replaceBadChars(actor.getID())));
            rep.add(new Var("dpName", Utils.replaceBadChars(diagram.getID())));
            List<GraphEntity> stages = Utils.getEntities(diagram, PD_STAGE);
            if (stages.size() == 1) {
                GraphEntity stageGE = stages.get(0);
                GraphAttribute stageGA = stageGE.getAttributeByName("NamePDStageField");
                rep.add(new Var("stageName", stageGA.getSimpleValue()));
                for (GraphEntity symptom : Utils.getTargetsEntity(stageGE, SYMPTOMS)) {
                    System.out.println("\tSymptom = " + symptom.getID());
                    Repeat symptoms = new Repeat("symptoms");
                    rep.add(symptoms);
                    symptoms.add(new Var("symptomName", Utils.replaceBadChars(symptom.getID())));
                    symptoms.add(new Var("symptomType", getSymptomClass(symptom)));
                    symptoms.add(new Var("sympEvoInstance", getSymptomEvoName(symptom)));

                    Vector<String[]> simulationSymptomLevels = getSymptomLevel(symptom, actor);
                    for (String[] pairSymptonAndSim : simulationSymptomLevels) {
                        Repeat simlevels = new Repeat("siminit");
                        symptoms.add(simlevels);
                        simlevels.add(new Var("symptomLevel", pairSymptonAndSim[0]));
                        simlevels.add(new Var("simname", pairSymptonAndSim[1]));
                        for (GraphEntity filter : Utils.getTargetsEntity(symptom, LIMITATIONS)) {
                            String level = getLevelOfFilter(filter.getType());
                            Repeat filterSet = new Repeat("createFiltersSeq");
                            simlevels.add(filterSet);
                            filterSet.add(new Var("symplevel", level));
                            if (level != null) {
                                GraphAttribute filters = filter.getAttributeByName(TASK_ALLOWED);
                                GraphCollection filterCollection = filters.getCollectionValue();
                                for (int i = 0; i < filterCollection.size(); i++) {
                                    GraphEntity ge = filterCollection.getElementAt(i);
                                    GraphAttribute filterRef = ge.getAttributeByName("modelID");
                                    String filterDiagName = filterRef.getSimpleValue();
                                    Graph filtersGraph = Utils.getGraphByName(filterDiagName, browser);
                                    if (filtersGraph != null) {
                                        generateFilters(filterSet, filtersGraph, level);
                                        generateDependencies(filterSet, filtersGraph, level);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private String getSymptomEvoName(GraphEntity symptom) {
        String result = Utils.getAttributeByName(symptom, "SymptomEvoField");
        if(result.equals("")) {
            logger.log(Level.WARNING, "There are not Symptom Evolution Diagram for symptom {0}", 
                    new Object[]{symptom.getID()});
            return "null";
        } else {
            return "new "+Utils.replaceBadChars(result)+
                    "(agent,"+Utils.replaceBadChars(symptom.getID())+")";
        }
    }
            
    private Vector<Graph> getPDDiagramsForActor(GraphEntity actor) {
        Vector<Graph> patientGraphs = new Vector<Graph>();
        Vector<GraphRelationship> rels = actor.getAllRelationships("ProfileOf");
        for (GraphRelationship rel : rels) {
            GraphEntity target;
            try {
                target = Utils.getSourceEntity(actor, rel);
                if (target.getType().equals("ParkinsonsProfile")) {
                    GraphAttribute diagNameAtt = target.getAttributeByName("ParkinsonSpecDiag");
                    if (diagNameAtt != null && diagNameAtt.getSimpleValue() != null
                            && browser.getGraph(diagNameAtt.getSimpleValue()) != null) {
                        patientGraphs.add(browser.getGraph(diagNameAtt.getSimpleValue()));
                    }
                }
            } catch (NotFound e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        return patientGraphs;
    }

    private String getSymptomClass(GraphEntity symptom) {
        String result = "Symptom";
        try {
            GraphAttribute typeGA = symptom.getAttributeByName("PDSymptomTypeField");
            String symptType = typeGA.getSimpleValue();
            if (!symptType.equals("")) {
                if (symptType.equals("Tremor")) {
                    result = "TremorSymptom";
                }
            }
        } catch (NotFound ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        return result;
    }

    private Vector<String[]> getSymptomLevel(GraphEntity symptom, GraphEntity actor) {
        String result = "phat.agents.filters.Symptom.Level.NONE";
        Vector<String[]> results = new Vector<String[]>();
        Collection<Graph> graphs = Utils.getGraphsByType("SimulationDiagram", browser);
        for (Graph graph : graphs) {
            if (Utils.contains(graph, actor)) {
                String level = getActorSymptomInSimdiagram(graph, actor, symptom);
                if (level != null) {
                    results.add(new String[]{level, graph.getID()});
                } else {
                    results.add(new String[]{"phat.agents.filters.Symptom.Level.NONE", graph.getID()});
                }
            }
        }
        return results;
    }

    private String getActorSymptomInSimdiagram(Graph graph, GraphEntity actor, GraphEntity symptom) {
        try {
            for (GraphEntity entity : graph.getEntities()) {
                if (entity.getType().equals("HumanInitialization")
                        && Utils.getRelatedElementsVectorInSameDiagram(entity, "RelatedHuman", "RelatedHumantarget").contains(actor)) {
                    Vector<GraphEntity> symptomsInitialized = Utils.getRelatedElementsVectorInSameDiagram(entity, "InitializesSymptom", "InitializesSymptomtarget");
                    for (GraphEntity symptominialization : symptomsInitialized) {
                        boolean initializedSympom = Utils.getRelatedElementsVectorInSameDiagram(symptominialization, "InitializedSymptom", "InitializedSymptomtarget").contains(symptom);
                        if (initializedSympom) {
                            String levelValue = symptominialization.getAttributeByName("SymptomLevel").getSimpleValue();
                            switch (levelValue) {
                                case "LOW":
                                    return "phat.agents.filters.Symptom.Level.LOW";
                                case "MEDIUM":
                                    return "phat.agents.filters.Symptom.Level.MEDIUM";
                                case "HIGH":
                                    return "phat.agents.filters.Symptom.Level.HIGH";
                            }
                        }

                    }
                }

            }
        } catch (NullEntity | NotFound e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private void generateDependencies(Repeat filterSet, Graph filterGraph, String level) {
        try {
            for (GraphEntity entity : filterGraph.getEntities()) {
                System.out.println("generateDependencies:" + entity.getID() + ", " + entity.getType());
                if (entity.getType().equals(SELECTOR_FILTER_TYPE)
                        || entity.getType().equals(DELAY_FILTER_TYPE)
                        || entity.getType().equals(PLACE_FILTER_TYPE)
                        || entity.getType().equals(REPLACE_TASK_FILTER_TYPE)
                        || entity.getType().equals(TARGET_OBJ_FILTER_TYPE)
                        || entity.getType().equals(CHANGE_TOOL_FILTER_TYPE)
                        || entity.getType().equals(UNABLE_FILTER_TYPE)) {
                    GraphEntity targetGA = Utils.getTargetEntity(entity, "NextFilter");
                    System.out.println("\tgenerateDependencies.NextFilter:" + targetGA);
                    if (targetGA != null) {
                        System.out.println("\t\tgenerateDependencies.NextFilter:" + targetGA.getID());
                        Repeat selectRep = new Repeat("nextFilterRep");
                        filterSet.add(selectRep);
                        selectRep.add(new Var("sourceFilter", entity.getID()));
                        selectRep.add(new Var("targetFilter", targetGA.getID()));
                    }
                    targetGA = Utils.getTargetEntity(entity, "FAlternative");
                    System.out.println("\tgenerateDependencies.FAlternative:" + targetGA);
                    if (targetGA != null) {
                        System.out.println("\t\tgenerateDependencies.FAlternative:" + targetGA.getID());
                        Repeat selectRep = new Repeat("alternativeFilterRep");
                        filterSet.add(selectRep);
                        selectRep.add(new Var("sourceFilter", entity.getID()));
                        selectRep.add(new Var("targetFilter", targetGA.getID()));
                    }
                }
            }
        } catch (NullEntity ex) {
            Logger.getLogger(PDGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void generateFilters(Repeat filterSet, Graph filterGraph, String level) {
        try {
            for (GraphEntity filterGE : Utils.getEntities(filterGraph, SELECTOR_FILTER_TYPE)) {
                Repeat selectRep = new Repeat(SELECTOR_FILTER_TYPE);
                filterSet.add(selectRep);
                selectRep.add(new Var("filterName", Utils.replaceBadChars(filterGE.getID())));
                selectRep.add(new Var("filterCond", getCondition(filterGraph, filterGE)));
                String byType = Utils.getAttributeByName(filterGE, "ByType");
                if(byType == null || byType.equals("")) {
                    logger.log(Level.WARNING, "ByType field of {0} is not set. Default value is \"Yes\".", 
                            new  Object[]{filterGE.getID()});
                    byType = "Yes";
                }
                selectRep.add(new Var("byType", Utils.yesNoToTrueFalse(byType)));
                for (GraphEntity taskGE : Utils.getTargetsEntity(filterGE, "AllowedTask")) {
                    Repeat allowedTaskRep = new Repeat("allowedTaskRep");
                    selectRep.add(allowedTaskRep);
                    allowedTaskRep.add(new Var("taskType", Utils.replaceBadChars(taskGE.getType())));
                    allowedTaskRep.add(new Var("taskId", Utils.replaceBadChars(taskGE.getID())));
                }
            }
            for (GraphEntity filterGE : Utils.getEntities(filterGraph, DELAY_FILTER_TYPE)) {
                Repeat selectRep = new Repeat(DELAY_FILTER_TYPE);
                selectRep.add(new Var("filterName", Utils.replaceBadChars(filterGE.getID())));
                selectRep.add(new Var("filterCond", getCondition(filterGraph, filterGE)));
                selectRep.add(new Var("level", level));
                try {
                    GraphAttribute delayGA = filterGE.getAttributeByName("DelayPercentageField");
                    selectRep.add(new Var("delayValude", delayGA.getSimpleValue() + "f/100f"));
                    filterSet.add(selectRep);
                } catch (NotFound ex) {
                    Logger.getLogger(PDGenerator.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            for (GraphEntity filterGE : Utils.getEntities(filterGraph, PLACE_FILTER_TYPE)) {
                Repeat selectRep = new Repeat(PLACE_FILTER_TYPE);
                filterSet.add(selectRep);
                selectRep.add(new Var("filterName", Utils.replaceBadChars(filterGE.getID())));
                selectRep.add(new Var("filterCond", getCondition(filterGraph, filterGE)));
            }
            for (GraphEntity filterGE : Utils.getEntities(filterGraph, REPLACE_TASK_FILTER_TYPE)) {
                Repeat selectRep = new Repeat(REPLACE_TASK_FILTER_TYPE);
                selectRep.add(new Var("filterName", Utils.replaceBadChars(filterGE.getID())));
                selectRep.add(new Var("filterCond", getCondition(filterGraph, filterGE)));
                try {
                    String taskSentence = "null";
                    GraphAttribute diagRef = filterGE.getAttributeByName("SeqTaskDiagramField");
                    if (!diagRef.getSimpleValue().equals("")) {
                        taskSentence = "new " + Utils.replaceBadChars(diagRef.getSimpleValue()) + "Task(agent)";
                    }
                    selectRep.add(new Var("taskSentence", taskSentence));
                    selectRep.add(new Var("taskSentenceClass", Utils.replaceBadChars(diagRef.getSimpleValue()) + "Task.class"));
                    filterSet.add(selectRep);
                } catch (NotFound ex) {
                    Logger.getLogger(PDGenerator.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            for (GraphEntity filterGE : Utils.getEntities(filterGraph, TARGET_OBJ_FILTER_TYPE)) {
                Repeat selectRep = new Repeat(TARGET_OBJ_FILTER_TYPE);
                filterSet.add(selectRep);
                selectRep.add(new Var("filterName", Utils.replaceBadChars(filterGE.getID())));
                selectRep.add(new Var("filterCond", getCondition(filterGraph, filterGE)));
            }
            for (GraphEntity filterGE : Utils.getEntities(filterGraph, CHANGE_TOOL_FILTER_TYPE)) {
                Repeat selectRep = new Repeat(CHANGE_TOOL_FILTER_TYPE);
                filterSet.add(selectRep);
                selectRep.add(new Var("filterName", Utils.replaceBadChars(filterGE.getID())));
                selectRep.add(new Var("filterCond", getCondition(filterGraph, filterGE)));
            }
            for (GraphEntity filterGE : Utils.getEntities(filterGraph, UNABLE_FILTER_TYPE)) {
                Repeat selectRep = new Repeat(UNABLE_FILTER_TYPE);
                filterSet.add(selectRep);
                selectRep.add(new Var("filterName", Utils.replaceBadChars(filterGE.getID())));
                selectRep.add(new Var("filterCond", getCondition(filterGraph, filterGE)));
            }
            for (GraphEntity firstFilter : Utils.getFirstEntities(filterGraph)) {
                System.out.println("FIRST ENTITY => " + Utils.replaceBadChars(firstFilter.getID()));
                Repeat selectRep = new Repeat("setFirstFilter");
                filterSet.add(selectRep);
                selectRep.add(new Var("firstFilter", Utils.replaceBadChars(firstFilter.getID())));
            }
        } catch (NullEntity ex) {
            Logger.getLogger(PDGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String getCondition(Graph graph, GraphEntity entity) {
        String cond = ConditionGenerator.generateAndCondition(Utils.getTargetsEntity(entity, "FPrecondition"));
        return cond;
    }

    private void processFilterSequence(Graph graph, GraphEntity firstFilter, Repeat rep) {
        String cond = ConditionGenerator.generateAndCondition(Utils.getTargetsEntity(firstFilter, "FPrecondition"));
        rep.add(new Var("filterCond", cond));
        rep.add(new Var("filterCond", cond));

        GraphEntity nextFilter = Utils.getTargetEntity(firstFilter, NEXT_FILTER_REL, firstFilter.getRelationships());
        if (nextFilter != null) {
            processFilterSequence(graph, nextFilter, rep);
        }
    }

    private String getLevelOfFilter(String filterType) {
        if (filterType.equals(LOW_FILTER)) {
            return "phat.agents.filters.Symptom.Level.LOW";
        } else if (filterType.equals(MEDIUM_FILTER)) {
            return "phat.agents.filters.Symptom.Level.MEDIUM";
        } else if (filterType.equals(HIGH_FILTER)) {
            return "phat.agents.filters.Symptom.Level.HIGH";
        }
        return null;
    }

    private String getFilterType(String type) {
        String result = null;

        return result;
    }

    public static void linkPDManager(String humanId, Repeat repFather, Browser browser) {
        GraphEntity dmGraph = Utils.getProfileTypeOf(humanId, PARKINSON_PROFILE, browser);
        if (dmGraph != null) {
            try {
                GraphAttribute pdSpec = dmGraph.getAttributeByName("ParkinsonSpecDiag");
                Repeat rep = new Repeat("filterManager");
                repFather.add(rep);
                rep.add(new Var("fmName", Utils.replaceBadChars(pdSpec.getSimpleValue())));
            } catch (NotFound ex) {
                Logger.getLogger(PDGenerator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
