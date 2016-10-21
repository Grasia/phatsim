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

public class ProgramBehaviorGenerator {

    final static Logger logger = Logger.getLogger(ProgramBehaviorGenerator.class.getName());
    final static String PROGRAM_TRANSITION_REL = "ProgramTransition";
    final static String DEVICE_ACTIONS_REL = "DeviceActionsAttached";
    final static String PROGRAM_STATE = "ProgramState";
    final static String VIBRATE_DEVICE_ACTION = "VibrateDeviceAction";
    final static String INCOMING_CALL_ACTION = "IncomingCallAction";
    final static String TTS_DEVICE_ACTION = "TextToSpeachAction";
    final static String SWITCH_LIGHT_ACTION = "SwitchLightAction";
    final static String DEVICE_BEHAVIOR_DIAGRAM = "DeviceBehavoirDiagram";

    final static String DEVICE_ID_FIELD = "DeviceIdField";
    final static String MILLIS_FIELD = "MillisDurationField";
    final static String PHONE_NUMBER_FIELD = "PhoneNumberField";
    final static String ON_OFF_FIELD = "ONOFFStateField";
    final static String ROOM_FIELD = "RoomField";
    final static String MESSAGE_FIELD = "MessageField";

    Browser browser;

    public ProgramBehaviorGenerator(Browser browser) {
        this.browser = browser;
    }

    public void generateFSMProgramBehaviorClasses(Sequences seq) {
        System.out.println("\n\ngenerateFSMProgramBehaviorClasses...");
        for (Graph diagram : Utils.getGraphsByType(DEVICE_BEHAVIOR_DIAGRAM, browser)) {
            System.out.println("Diagram: " + diagram.getID());
            logger.log(Level.INFO, "Processing diagram {0}...", new Object[]{diagram.getID()});
            Repeat repProgBehav = new Repeat("programBehavior");
            repProgBehav.add(new Var("seName", Utils.replaceBadChars(diagram.getID())));
            seq.addRepeat(repProgBehav);
            try {
                GraphEntity[] diagramEntities = diagram.getEntities();

                // Generate FSMSymptomEvolution classes
                for (GraphEntity progState : diagramEntities) {
                    System.out.println("\tState: " + progState.getID());
                    if (progState.getType().equals(PROGRAM_STATE)) {
                        logger.log(Level.INFO, "Building state {0} with type {1}...",
                                new Object[]{progState.getID(), progState.getType()});
                        Repeat appStateRep = createNewProgState(progState);
                        repProgBehav.add(appStateRep);

                        for (GraphEntity action : Utils.getTargetsEntity(progState, DEVICE_ACTIONS_REL)) {
                            System.out.println("\t\tAction: " + action.getID());
                            processDeviceAction(action, appStateRep);
                        }
                    }
                }

                // Generate Transitions
                for (GraphRelationship gr : diagram.getRelationships()) {
                    if (gr.getType().equals(PROGRAM_TRANSITION_REL)) {
                        processProgTransition(gr, repProgBehav);
                    }
                }
                registerFirstProgState(diagram, repProgBehav);
            } catch (NullEntity ex) {
                logger.log(Level.SEVERE, "Diagram {0} is empty!", new Object[]{diagram.getID()});
                System.exit(-1);
            }
        }
        System.out.println("...generateFSMProgramBehaviorClasses\n\n");
    }
    
    private void registerFirstProgState(Graph deviceBehavDiag, Repeat repFather) {
        GraphEntity ge = Utils.getFirstEntity(deviceBehavDiag, PROGRAM_STATE);
        if (ge == null) {
            logger.log(Level.SEVERE, "The diagram {0} is empty or doesn't know "
                    + "which entity is the first one!",
                    new Object[]{deviceBehavDiag.getID()});
            System.exit(0);
        }

        Repeat repFirst = new Repeat("firstProgState");
        repFather.add(repFirst);
        repFirst.add(new Var("psID", Utils.replaceBadChars(ge.getID())));
    }

    private void processProgTransition(GraphRelationship progTrans, Repeat rep) throws NullEntity {
        Collection<GraphEntity> conds = new ArrayList<>();
        GraphEntity source = null;
        GraphEntity target = null;
        System.out.println("\trel=" + progTrans.getID() + ":" + progTrans.getType());
        for (GraphRole gRole : progTrans.getRoles()) {
            System.out.println("\t\trole="
                    + gRole.getID() + ":"
                    + gRole.getName() + ":"
                    + gRole.getPlayer().getID() + ":"
                    + gRole.getPlayer().getType());
            if (gRole.getName().startsWith("PreCondProgTransition")) {
                if (gRole.getPlayer().getType().endsWith("ProgramState")) {
                    source = gRole.getPlayer();
                } else {
                    conds.add(gRole.getPlayer());
                }
            } else if (gRole.getName().startsWith("PostCondProgTransition")) {
                target = gRole.getPlayer();
            }
        }
        if (target != null && source != null) {
            System.out.println(source.getID() + " -> " + target.getID());
            
            String condSentence = ConditionGenerator.generateAndCondition(conds);
            Repeat sympStateRep = new Repeat("progStatesTrans");
            sympStateRep.add(new Var("stateSource", source.getID()));
            sympStateRep.add(new Var("stateTarget", target.getID()));
            sympStateRep.add(new Var("condInst", condSentence));
            rep.add(sympStateRep);
        }
    }

    private void processDeviceAction(GraphEntity action, Repeat rep) {
        String deviceId = Utils.getAttributeByName(action, DEVICE_ID_FIELD);

        if (action.getType().equals(VIBRATE_DEVICE_ACTION)) {
            String millis = Utils.getAttributeByName(action, MILLIS_FIELD, "1000");

            Repeat repVibrateAction = new Repeat("VibrateAction");
            rep.add(repVibrateAction);
            repVibrateAction.add(new Var("aID", action.getID()));
            repVibrateAction.add(new Var("aType", action.getType()));
            repVibrateAction.add(new Var("aDesc", Utils.getAttributeByName(action, "Description")));
            repVibrateAction.add(new Var("deviceId", deviceId));
            repVibrateAction.add(new Var("millis", millis));
        } else if (action.getType().equals(INCOMING_CALL_ACTION)) {
            String phoneNumber = Utils.getAttributeByName(action, PHONE_NUMBER_FIELD, "000-000-000");

            Repeat repIncomingCallAction = new Repeat("IncomingCallAction");
            repIncomingCallAction.add(new Var("aID", action.getID()));
            repIncomingCallAction.add(new Var("aType", action.getType()));
            repIncomingCallAction.add(new Var("aDesc", Utils.getAttributeByName(action, "Description")));
            repIncomingCallAction.add(new Var("deviceId", deviceId));
            repIncomingCallAction.add(new Var("phoneNumber", phoneNumber));
            rep.add(repIncomingCallAction);            
        } else if (action.getType().equals(TTS_DEVICE_ACTION)) {
            String message = Utils.getAttributeByName(action, MESSAGE_FIELD, "No message specified");

            Repeat repTTSAction = new Repeat("TTSAction");
            repTTSAction.add(new Var("aID", action.getID()));
            repTTSAction.add(new Var("aType", action.getType()));
            repTTSAction.add(new Var("aDesc", Utils.getAttributeByName(action, "Description")));
            repTTSAction.add(new Var("deviceId", deviceId));
            repTTSAction.add(new Var("message", message));
            rep.add(repTTSAction);
        } else if (action.getType().equals(SWITCH_LIGHT_ACTION)) {
            String onOff = Utils.getAttributeByName(action, ON_OFF_FIELD, "ON");
            String roomId = Utils.replaceBadChars(Utils.getAttributeByName(action, ROOM_FIELD));
            if(roomId == null || roomId.equals("")) {
                logger.log(Level.SEVERE, "The {0} of entity {0} is empty!!", new Object[]{ROOM_FIELD, action.getID()});
                System.exit(-1);
            }
            Repeat repTTSAction = new Repeat("SwitchLightAction");
            repTTSAction.add(new Var("aID", action.getID()));
            repTTSAction.add(new Var("aType", action.getType()));
            repTTSAction.add(new Var("aDesc", Utils.getAttributeByName(action, "Description")));
            repTTSAction.add(new Var("roomId", roomId));
            repTTSAction.add(new Var("onOff", onOff));
            rep.add(repTTSAction);
        }
    }

    private Repeat createNewProgState(GraphEntity progState) {
        Repeat progStateRep = new Repeat("progStates");
        System.out.println("\t\tProgState: " + progState.getID() + ":" + progState.getType());
        progStateRep.add(new Var("psID", Utils.replaceBadChars(progState.getID())));
        progStateRep.add(new Var("psType", Utils.replaceBadChars(progState.getType())));
        progStateRep.add(new Var("psDesc", Utils.getAttributeByName(progState, "Description")));
        return progStateRep;
    }
}
