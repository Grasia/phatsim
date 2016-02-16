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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ActivityGenerator {

    final static Logger logger = Logger.getLogger(ActivityGenerator.class.getName());
    static final String ADL_SPEC_DIAGRAM = "ADLSpecDiagram";
    static final String ACTIVITY_DIAGRAM = "ActivityDiagram";
    static final String ACTIVITY_SPEC_FIELD = "ActivitySpecField";
    static final String ACTIVITY_TYPE = "BActivity";
    static final String NEXT_ACTIVITY_REL = "NextActivity";
    static final String TRUE_FLOW_REL = "TrueFlow";
    static final String FALSE_FLOW_REL = "FalseFlow";
    static final String IF_FLOW_CONTROL_TYPE = "IFFlowControl";
    static final String IF_FLOW_COND_REL = "cond";
    static final String SEQ_TASK_DIAGRAM_FIELD = "SeqTaskDiagramField";
    Browser browser;

    public ActivityGenerator(Browser browser) {
        this.browser = browser;
    }

    public void generateTimeIntervals(Sequences seq) throws NullEntity,
            NotFound {
        // System.out.println("generateTimeIntervals");
        generateActivityClasses(seq);
        generateTimeIntervalClasses(seq);

    }

    public void generateActivityClasses(Sequences seq) throws NullEntity,
            NotFound {
        for (GraphEntity ge : browser.getAllEntities()) {
            if (ge.getType().equals(ACTIVITY_TYPE)) {
                GraphAttribute ga = ge
                        .getAttributeByName(SEQ_TASK_DIAGRAM_FIELD);
                if (ga != null) {
                    String actDiagId = Utils.replaceBadChars(ga.getSimpleValue());
                    if (actDiagId != null && !actDiagId.equals("")) {
                        Repeat rep = new Repeat("activities");
                        seq.addRepeat(rep);
                        rep.add(new Var("aID", Utils.replaceBadChars(ge.getID())));
                        rep.add(new Var("aType", Utils.replaceBadChars(ge.getType())));
                        rep.add(new Var("aDesc", Utils.getAttributeByName(ge, "Description")));

                        rep.add(new Var("stID", Utils.replaceBadChars(actDiagId)));
                        continue;
                    }
                }
                logger.log(Level.SEVERE, "The {0} activity doesn't have sequential task diagram!",
                        new Object[]{ge.getID()});
                System.exit(0);
            }
        }
    }

    private void generateTimeIntervalClasses(Sequences seq) throws NullEntity,
            NotFound {
        for (Graph diagram : Utils.getGraphsByType(ADL_SPEC_DIAGRAM, browser)) {
            System.out.println("ADLDiagramName: " + diagram.getID());
            for (GraphEntity ge : diagram.getEntities()) {
                if (ge.getType().equals("TimeInterval")) {
                    // Define class structure
                    String timeIntervalID = Utils.replaceBadChars(ge.getID());
                    String timeIntervalType = Utils.replaceBadChars(ge.getType());
                    Repeat rep = new Repeat("tis");
                    rep.add(new Var("tisName", timeIntervalID));
                    rep.add(new Var("tisType", timeIntervalType));
                    seq.addRepeat(rep);
                    GraphAttribute ga = ge
                            .getAttributeByName(ACTIVITY_SPEC_FIELD);
                    if (ga != null) {
                        String actDiagId = ga.getSimpleValue();
                        if (actDiagId != null && !actDiagId.equals("")) {
                            Graph actDiagram = Utils.getGraphByName(actDiagId,
                                    browser);
                            if (actDiagram != null) {
                                initActivitiesAndDependences(actDiagram, rep);
                            }
                        } else {
                            // No activities are specified
                            Repeat repDoNothing = new Repeat("noActivities");
                            rep.add(repDoNothing);
                        }
                    }
                    String defState = Utils.getAttributeByName(ge, "DefaultActivityField");
                    if (defState != null && !defState.equals("")) {
                        Repeat setDefaultState = new Repeat("setDefaultState");
                        rep.add(setDefaultState);
                        setDefaultState.add(new Var("defState", Utils.replaceBadChars(defState)));
                    }
                }
            }
        }
    }

    private void initActivitiesAndDependences(Graph adlSpec, Repeat repFather)
            throws NotFound, NullEntity {

        GraphEntity ge = Utils.getFirstEntity(adlSpec);
        if (ge == null) {
            logger.log(Level.SEVERE, "The diagram {0} is empty or doesn't know "
                    + "which entity is the first one!",
                    new Object[]{adlSpec.getID()});
            System.exit(0);
        }

        Repeat repFirst = new Repeat("firstActivity");
        repFather.add(repFirst);
        repFirst.add(new Var("actName", Utils.replaceBadChars(ge.getID())));

        generateActivityInstances(adlSpec, repFather);
        generateCondition(adlSpec, repFather);
        generateTransitions(adlSpec, repFather);
    }

    private void generateActivityInstances(Graph adlSpec, Repeat repFather) throws NullEntity {
        for (GraphEntity activity : adlSpec.getEntities()) {
            System.out.println(">>>>entity ->" + activity.getType() + ":"
                    + activity.getID());
            if (activity.getType().equals(ACTIVITY_TYPE)) {
                String activityName = activity.getID();
                System.out.println("ActivityName = " + activityName);

                // Defines the activity
                Repeat rep = new Repeat("activities");
                repFather.add(rep);
                rep.add(new Var("actName", Utils.replaceBadChars(activityName)));

                Collection<GraphEntity> nextEntities = Utils.getTargetsEntity(activity,
                        NEXT_ACTIVITY_REL);
                if (nextEntities.isEmpty()) {
                    // It is a last activity, the automaton should finish after the execution
                    Repeat rep2 = new Repeat("regLastActivityRep");
                    repFather.add(rep2);
                    rep2.add(new Var("finalActivity", Utils.replaceBadChars(activityName)));
                }
            }
        }
    }

    private void generateCondition(Graph adlSpec, Repeat repFather) throws NullEntity {
        for (GraphEntity condition : adlSpec.getEntities()) {
            if (condition.getType().equals(IF_FLOW_CONTROL_TYPE)) {
                Collection<GraphEntity> conds = Utils.getTargetsEntity(condition, IF_FLOW_COND_REL);
                String condSentence = ConditionGenerator.generateAndCondition(conds);
                String condId = condition.getID();
                
                Repeat conditions = new Repeat("conditions");
                repFather.add(conditions);
                conditions.add(new Var("condId", condId));
                conditions.add(new Var("condInst", condSentence));
            }
        }
    }
    
    private void generateTransitions(Graph adlSpec, Repeat repFather) throws NullEntity {
        for (GraphEntity activity : adlSpec.getEntities()) {
            if (activity.getType().equals(ACTIVITY_TYPE)) {
                generateDirectTransitions(activity, repFather);
                generateCondTransitions(activity, repFather);
            }
        }
    }

    private void generateDirectTransitions(GraphEntity activity, Repeat repFather) {
        for (GraphEntity previousAct : Utils.getSourcesEntity(activity, NEXT_ACTIVITY_REL)) {
            if (previousAct.getType().equals(ACTIVITY_TYPE)) {
                // registers a transition between activities without any
                // condition
                Repeat rep2 = new Repeat("regTrans");
                repFather.add(rep2);
                rep2.add(new Var("actSource", Utils.replaceBadChars(previousAct.getID())));
                rep2.add(new Var("actTarget", Utils.replaceBadChars(activity.getID())));
            }
        }
    }

    private void generateCondTransitions(GraphEntity activity, Repeat repFather) {
        for (GraphEntity previousIf : Utils.getSourcesEntity(activity, TRUE_FLOW_REL)) {
            if (previousIf.getType().equals(IF_FLOW_CONTROL_TYPE)) {
                propagateCond(activity, previousIf, "new CompositeAndCondition(" + previousIf.getID() + ")", repFather);
            }
        }
        for (GraphEntity previousIf : Utils.getSourcesEntity(activity, FALSE_FLOW_REL)) {
            if (previousIf.getType().equals(IF_FLOW_CONTROL_TYPE)) {
                propagateCond(activity, previousIf, "new CompositeAndCondition(new NegateCondition(" + previousIf.getID() + "))", repFather);
            }
        }
    }

    private void propagateCond(GraphEntity targetActivity, GraphEntity cIf, String condition, Repeat repFather) {
        for (GraphEntity previousAct : Utils.getSourcesEntity(cIf, NEXT_ACTIVITY_REL)) {
            regCondTrans(previousAct, targetActivity, condition, repFather, false);
        }
        for (GraphEntity previousIf : Utils.getSourcesEntity(cIf, TRUE_FLOW_REL)) {
            if (previousIf.getType().equals(IF_FLOW_CONTROL_TYPE)) {
                propagateCond(targetActivity, previousIf, condition + ".add(" + previousIf.getID() + ")", repFather);
            }
        }
        for (GraphEntity previousIf : Utils.getSourcesEntity(cIf, FALSE_FLOW_REL)) {
            if (previousIf.getType().equals(IF_FLOW_CONTROL_TYPE)) {
                propagateCond(targetActivity, previousIf, condition + ".add(" + "new NegateCondition(" + previousIf.getID() + "))", repFather);
            }
        }
    }

    private GraphEntity processCondition(Graph graph, GraphEntity ifnode, Repeat repFather)
            throws NotFound {
        List<GraphEntity> sources = new ArrayList<GraphEntity>();
        sources.addAll(Utils.getSourcesEntity(ifnode, NEXT_ACTIVITY_REL));
        sources.addAll(Utils.getSourcesEntity(ifnode, TRUE_FLOW_REL));
        sources.addAll(Utils.getSourcesEntity(ifnode, FALSE_FLOW_REL));

        Collection<GraphEntity> conds = Utils.getTargetsEntity(ifnode,
                IF_FLOW_COND_REL);

        GraphEntity nextTrueGE = Utils.getTargetEntity(ifnode, TRUE_FLOW_REL);
        GraphEntity nextFalseGE = Utils.getTargetEntity(ifnode, FALSE_FLOW_REL);

        String condSentence = ConditionGenerator.generateAndCondition(conds);
        Repeat repCond = new Repeat("regCondTrans");
        repFather.add(repCond);
        repCond.add(new Var("condInst", condSentence));
        for (GraphEntity source : sources) {
            if (nextTrueGE != null) {
                Repeat rep = new Repeat("regTrueTrans");
                repCond.add(rep);
                rep.add(new Var("actSource", Utils.replaceBadChars(source.getID())));
                rep.add(new Var("actTarget", Utils.replaceBadChars(nextTrueGE.getID())));
            }
            if (nextFalseGE != null) {
                Repeat rep = new Repeat("regFalseTrans");
                repCond.add(rep);
                rep.add(new Var("actSource", Utils.replaceBadChars(source.getID())));
                rep.add(new Var("actTarget", Utils.replaceBadChars(nextFalseGE.getID())));
            }
        }
        return null;
    }

    private void regCondTrans(GraphEntity targetS, GraphEntity targetA, String condition, Repeat repFather, boolean negate) {
        Repeat repCond = new Repeat("regCondTrans");
        repFather.add(repCond);
        repCond.add(new Var("condInst", condition));
        if (!negate) {
            Repeat rep = new Repeat("regTrueTrans");
            repCond.add(rep);
            rep.add(new Var("actSource", Utils.replaceBadChars(targetS.getID())));
            rep.add(new Var("actTarget", Utils.replaceBadChars(targetA.getID())));
        } else {
            Repeat rep = new Repeat("regFalseTrans");
            repCond.add(rep);
            rep.add(new Var("actSource", Utils.replaceBadChars(targetS.getID())));
            rep.add(new Var("actTarget", Utils.replaceBadChars(targetA.getID())));
        }
    }
}
