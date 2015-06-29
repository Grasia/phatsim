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

import ingenias.exception.NullEntity;
import ingenias.generator.browser.Browser;
import ingenias.generator.browser.Graph;
import ingenias.generator.browser.GraphEntity;
import ingenias.generator.browser.GraphRelationship;
import ingenias.generator.browser.GraphRole;
import ingenias.generator.datatemplate.Repeat;
import ingenias.generator.datatemplate.Sequences;
import ingenias.generator.datatemplate.Var;
import java.util.ArrayList;
import java.util.Collection;

import java.util.logging.Level;
import java.util.logging.Logger;
import phat.agents.filters.Symptom;
import phat.codeproc.ConditionGenerator;
import phat.codeproc.Utils;

public class SymptomEvolutionGenerator {

    final static Logger logger = Logger.getLogger(SymptomEvolutionGenerator.class.getName());
    final static String SYMPTOM_TRANSITION_REL = "SymptomTransition";
    final static String NONE_SYMP_LEVEL_STATE = "NONESympLevelState";
    final static String LOW_SYMP_LEVEL_STATE = "LOWSympLevelState";
    final static String MEDIUM_SYMP_LEVEL_STATE = "MEDIUMSympLevelState";
    final static String HIGH_SYMP_LEVEL_STATE = "HIGHSympLevelState";
    final static String SYMPTOM_EVOLUTION_DIAGRAM = "SymptomEvolutionDiagram";
    Browser browser;

    public SymptomEvolutionGenerator(Browser browser) {
        this.browser = browser;
    }

    public void generateFSMSymptomEvolutionClasses(Sequences seq) {
        for (Graph diagran : Utils.getGraphsByType(SYMPTOM_EVOLUTION_DIAGRAM, browser)) {
            logger.log(Level.INFO, "Processing diagram {0}...", new Object[]{diagran.getID()});
            Repeat rep = new Repeat("symptomEvolution");
            rep.add(new Var("seName", Utils.replaceBadChars(diagran.getID())));
            seq.addRepeat(rep);
            try {
                GraphEntity[] diagramEntities = diagran.getEntities();

                // Generate FSMSymptomEvolution classes
                for (GraphEntity sympState : diagramEntities) {
                    if (sympState.getType().equals(NONE_SYMP_LEVEL_STATE)) {
                        logger.log(Level.INFO, "Building state {0} with type {1}...",
                                new Object[]{sympState.getID(), sympState.getType()});
                        Repeat appStateRep = createNewSympState(
                                sympState.getID(), Symptom.Level.NONE.name());
                        rep.add(appStateRep);
                    } else if (sympState.getType().equals(LOW_SYMP_LEVEL_STATE)) {
                        logger.log(Level.INFO, "Building state {0} with type {1}...",
                                new Object[]{sympState.getID(), sympState.getType()});
                        Repeat appStateRep = createNewSympState(
                                sympState.getID(), Symptom.Level.LOW.name());
                        rep.add(appStateRep);
                    } else if (sympState.getType().equals(MEDIUM_SYMP_LEVEL_STATE)) {
                        logger.log(Level.INFO, "Building state {0} with type {1}...",
                                new Object[]{sympState.getID(), sympState.getType()});
                        Repeat appStateRep = createNewSympState(
                                sympState.getID(), Symptom.Level.MEDIUM.name());
                        rep.add(appStateRep);
                    } else if (sympState.getType().equals(HIGH_SYMP_LEVEL_STATE)) {
                        logger.log(Level.INFO, "Building state {0} with type {1}...",
                                new Object[]{sympState.getID(), sympState.getType()});
                        Repeat appStateRep = createNewSympState(
                                sympState.getID(), Symptom.Level.HIGH.name());
                        rep.add(appStateRep);
                    }
                }

                // Generate Transitions
                for (GraphRelationship gr : diagran.getRelationships()) {
                    Collection<GraphEntity> conds = new ArrayList<>();
                    GraphEntity source = null;
                    GraphEntity target = null;
                    System.out.println("\trel=" + gr.getID() + ":" + gr.getType());
                    for (GraphRole gRole : gr.getRoles()) {
                        System.out.println("\t\trole=" + gRole.getID() + ":" + gRole.getName()
                                + ":" + gRole.getPlayer().getID() + ":" + gRole.getPlayer().getType());
                        if (gRole.getName().startsWith("PreCondSymptomMR")) {
                            if (gRole.getPlayer().getType().endsWith("SympLevelState")) {
                                source = gRole.getPlayer();
                            } else {
                                conds.add(gRole.getPlayer());
                            }
                        } else if (gRole.getName().startsWith("PostCondSymptomMR")) {
                            target = gRole.getPlayer();
                        }
                    }
                    if (target != null && source != null) {
                        String condSentence = ConditionGenerator.generateAndCondition(conds);
                        Repeat sympStateRep = new Repeat("symStatesTrans");
                        sympStateRep.add(new Var("stateSource", source.getID()));
                        sympStateRep.add(new Var("stateTarget", target.getID()));
                        sympStateRep.add(new Var("condInst", condSentence));
                        rep.add(sympStateRep);

                    }
                }
            } catch (NullEntity ex) {
                logger.log(Level.SEVERE, "Diagram {0} is empty!", new Object[]{diagran.getID()});
                System.exit(-1);
            }
        }
    }

    private Repeat createNewSympState(String sympStateName, String levelName) {
        Repeat sympStateRep = new Repeat("symStates");
        sympStateRep.add(new Var("ssName", Utils.replaceBadChars(sympStateName)));
        sympStateRep.add(new Var("levelName", levelName));
        return sympStateRep;
    }
}
