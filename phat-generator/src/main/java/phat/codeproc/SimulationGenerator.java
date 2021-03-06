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
import ingenias.generator.browser.GraphCollection;
import ingenias.generator.browser.GraphEntity;
import ingenias.generator.datatemplate.Repeat;
import ingenias.generator.datatemplate.Sequences;
import ingenias.generator.datatemplate.Var;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SimulationGenerator {

    static final String HUMAN_PROFILE_SPEC_DIAGRAM = "HumanProfileSpecDiagram";
    static final String ADLProfile_SPEC_DIAGRAM = "ADLProfile";
    static final String SIMULATION_DIAGRAM = "SimulationDiagram";
    static final String HUMAN_INITIALIZATION_OBJ = "HumanInitialization";
    static final String INIT_PROGRAM_POOL = "InitProgramPool";
    Browser browser;

    public SimulationGenerator(Browser browser) {
        this.browser = browser;
    }

    public void generateSimulations(Sequences seq) throws NullEntity, NotFound {
        for (Graph simDiag : Utils
                .getGraphsByType(SIMULATION_DIAGRAM, browser)) {
            String simId = simDiag.getID();
            Repeat simInitRep = new Repeat("simInitialization");
            seq.addRepeat(simInitRep);
            simInitRep.add(new Var("simName", Utils.replaceBadChars(simId)));
            GraphAttribute description = simDiag.getAttributeByName("Description");
            simInitRep.add(new Var("simDescription", description.getSimpleValue()));

            generateWorldInitialization(simId, simDiag, simInitRep);
            generatePeopleInitialization(simDiag, simInitRep);
            generateCameraPositionToBody(simDiag, simInitRep);
            generateSmartphones(simId, simDiag, simInitRep);
            generateDeviceAgentsInitialization(simDiag, simInitRep);
        }

    }

    private void generateDeviceAgentsInitialization(Graph simDiag, Repeat simInitRep)
            throws NullEntity, NotFound {
        for (GraphEntity progPool : Utils.getEntities(simDiag, INIT_PROGRAM_POOL)) {
            for (GraphEntity deviceEntity : Utils.getTargetsEntity(progPool, "device")) {
                Repeat importADLRep = new Repeat("importDevices");
                simInitRep.add(importADLRep);

                String deviceId = deviceEntity.getID();
                Repeat agentRep = new Repeat("deviceAgentRep");
                simInitRep.add(agentRep);
                simInitRep.add(new Var("daID", deviceId));

                GraphCollection gc = progPool.getAttributeByName("ProgramPoolField").getCollectionValue();
                for (int i = 0; i < gc.size(); i++) {
                    String progId = gc.getElementAt(i).getAttributeByName("modelID").getSimpleValue();

                    Repeat progRep = new Repeat("progsRep");
                    progRep.add(new Var("progId", progId));
                    agentRep.add(progRep);
                }
            }

        }
    }

    private void generatePeopleInitialization(Graph simDiag, Repeat simInitRep)
            throws NullEntity, NotFound {
        for (GraphEntity hi : Utils.getEntities(simDiag, HUMAN_INITIALIZATION_OBJ)) {
            GraphEntity human = Utils.getTargetEntity(hi, "RelatedHuman", simDiag.getRelationships());
            if (human != null) {
                String humanId = human.getID();

                String bodyType = getBodyType(humanId);
                if (bodyType != null) {
                    Repeat bodyRep = new Repeat("bodies");
                    simInitRep.add(bodyRep);
                    bodyRep.add(new Var("actorname", Utils.replaceBadChars(humanId)));
                    bodyRep.add(new Var("bodyType", bodyType));
                    String showName = "false";
                    try {
                        GraphAttribute ga = hi.getAttributeByName("ShowName");
                        if (ga != null) {
                            String valueName = ga.getSimpleValue();
                            if (valueName != null && valueName.equals("Yes")) {
                                showName = "true";
                            }
                        }
                    } catch (NotFound nf) {
                        System.out.println("NOT FOUND ShowName" + nf.getMessage());
                    }
                    bodyRep.add(new Var("showName", showName));
                    String initialLoc = getInitialLocation(humanId, simDiag);
                    System.out.println("humanId=" + humanId + ",il="
                            + initialLoc);
                    if (initialLoc != null) {
                        Repeat initLocRep = new Repeat("initLocRep");
                        bodyRep.add(initLocRep);
                        initLocRep.add(new Var("iniLoc", initialLoc));
                    }

                    Repeat agentRep = new Repeat("agent");
                    simInitRep.add(agentRep);
                    agentRep.add(new Var("agentname", humanId));
                    GraphEntity activity = Utils.getTargetEntity(hi, "InitialActivity", simDiag.getRelationships());
                    if (activity != null) {
                        Repeat adlRep = new Repeat("setActivity");
                        agentRep.add(adlRep);
                        adlRep.add(new Var("actName", Utils.replaceBadChars(activity.getID())));
                        ActivityGenerator.addPararms(activity, agentRep);
                    } else {
                        String adlName = ADLsGenerator.getADLName(humanId, browser);
                        if (adlName != null) {

                            Repeat importADLRep = new Repeat("importADL");
                            simInitRep.add(importADLRep);

                            Repeat adlRep = new Repeat("ADL");
                            agentRep.add(adlRep);
                            adlRep.add(new Var("adlName", adlName));
                        } else {
                            // The agent does not have a behaviour defined
                        }
                    }
                    List<List<String>> sentencesWordByWord = getSentencesWordByWord(humanId);
                    if (!sentencesWordByWord.isEmpty()) {
                        Repeat hearingRep = new Repeat("ActivateHearingSense");
                        agentRep.add(hearingRep);

                        for (List<String> sentence : sentencesWordByWord) {
                            String joinedSentence = "";
                            for (String w : sentence) {
                                Repeat wordRep = new Repeat("wordsToBeListened");
                                hearingRep.add(wordRep);
                                wordRep.add(new Var("word", w));
                                joinedSentence += w + " ";
                            }
                            Repeat sentenceRep = new Repeat("sentenceRep");
                            hearingRep.add(sentenceRep);
                            sentenceRep.add(new Var("sentence", joinedSentence));
                        }
                    }
                }
            }
        }

        if (Utils.hasAnyEntity(browser, "CallStateEvent")) {
            Repeat activateCallStatesRep = new Repeat("activateCallStates");
            simInitRep.add(activateCallStatesRep);
        }

        if (Utils.hasAnyEntity(browser, "MessageListenedEvent")) {
            Repeat activateCallStatesRep = new Repeat("activateCallStates");
            simInitRep.add(activateCallStatesRep);
        }
    }

    private List<List<String>> getSentencesWordByWord(String actorId) {
        List<List<String>> result = new ArrayList<>();
        System.out.println("\n\n\ncontainsWordHeardEvent..." + actorId);
        Vector<GraphEntity> interactionProfiles = Utils.getProfilesTypeOf(actorId, "InteractionProfile", browser);
        System.out.println("interactionDiagrams=" + interactionProfiles.size());
        for (GraphEntity ge : interactionProfiles) {
            System.out.println("-" + ge.getID());
            GraphAttribute ga = null;
            try {
                ga = ge.getAttributeByName("InteractionSpecDiagField");
                if (ga != null && !ga.getSimpleValue().equals("")) {
                    System.out.println("\t-" + ga.getSimpleValue());
                    Graph interactionGraph = Utils.getGraphByName(ga.getSimpleValue(), browser);
                    if (interactionGraph != null) {
                        System.out.println("\t-" + interactionGraph.getName());
                        for (GraphEntity mle : Utils.getEntities(interactionGraph, "MessageListenedEvent")) {
                            GraphAttribute message = mle.getAttributeByName("Message");
                            if (!"".equals(message.getSimpleValue())) {
                                List<String> words = new ArrayList<>();
                                words.addAll(Arrays.asList(message.getSimpleValue().split("[\\s,?!]")));
                                result.add(words);
                            }
                        }
                    }
                }
            } catch (NotFound | NullEntity ex) {
                Logger.getLogger(SimulationGenerator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return result;
    }

    private void generateCameraPositionToBody(Graph simDiag, Repeat simInitRep)
            throws NullEntity, NotFound {
        for (GraphEntity hi : Utils.getEntities(simDiag, "CameraInit")) {
            GraphEntity human = Utils.getTargetEntity(hi, "CameraFaceToHuman", simDiag.getRelationships());
            if (human != null) {
                String humanId = human.getID();

                Repeat camRep = new Repeat("CameraToBodyInit");
                simInitRep.add(camRep);
                camRep.add(new Var("actorname", humanId));
                String distance = "2";
                String elevation = "15";
                String front = "true";

                GraphAttribute distanceGA = hi.getAttributeByName("DistanceToTarget");
                if (distanceGA != null && !distanceGA.getSimpleValue().equals("")) {
                    distance = distanceGA.getSimpleValue();
                }
                camRep.add(new Var("distance", distance));

                GraphAttribute elevationGA = hi.getAttributeByName("Elevation");
                if (elevationGA != null && !elevationGA.getSimpleValue().equals("")) {
                    elevation = elevationGA.getSimpleValue();
                }
                camRep.add(new Var("elevation", elevation));

                GraphAttribute frontGA = hi.getAttributeByName("IsInFrontOfHuman");
                if (frontGA != null && !frontGA.getSimpleValue().equals("")) {
                    if (frontGA.getSimpleValue().equals("No")) {
                        front = "false";
                    }
                }
                camRep.add(new Var("isinfrontofhuman", front));
            }
        }
    }

    public void generateWorldInitialization(String simId, Graph simDiags,
            Repeat rep) throws NullEntity, NotFound {

        GraphEntity ge = getEntity(simDiags, "WorldInitialization");
        GraphAttribute seedworld = ge.getAttributeByName("SimulationSeedField");

        if (seedworld != null && !seedworld.getSimpleValue().equals("")) {
            System.out.println("\n\n\nSEED = " + seedworld.getSimpleValue() + "\n\n\n");
            Repeat setSeed = new Repeat("setSeed");
            rep.add(setSeed);
            setSeed.add(new Var("seedValue", seedworld.getSimpleValue()));
        }

        GraphAttribute houseType = ge.getAttributeByName("HouseTypeField");
        String houseTypeString = "House3room2bath";
        if (houseType != null && !houseType.getSimpleValue().equals("")) {
            houseTypeString = houseType.getSimpleValue();
        }
        rep.add(new Var("houseType", houseTypeString));

        GraphEntity iniDate = Utils.getTargetEntity(ge, "InitialDate");
        if (iniDate == null) {
            System.out.println("Hola");
        }
        GraphAttribute year = iniDate.getAttributeByName("YearField");
        GraphAttribute month = iniDate.getAttributeByName("MonthField");
        GraphAttribute day = iniDate.getAttributeByName("DayField");
        GraphAttribute hour = iniDate.getAttributeByName("HourField");
        GraphAttribute min = iniDate.getAttributeByName("MinuteField");
        GraphAttribute sec = iniDate.getAttributeByName("SecondField");

        rep.add(new Var("year", year.getSimpleValue()));
        rep.add(new Var("month", month.getSimpleValue()));
        rep.add(new Var("day", day.getSimpleValue()));
        rep.add(new Var("hour", hour.getSimpleValue()));
        rep.add(new Var("min", min.getSimpleValue()));
        rep.add(new Var("sec", sec.getSimpleValue()));

        ge = getEntity(simDiags, "FlyCamInit");
        if (ge != null) {
            GraphAttribute width = ge.getAttributeByName("CamWidth");
            GraphAttribute height = ge.getAttributeByName("CamHeight");

            if (width != null && !width.getSimpleValue().equals("")
                    && height != null && !height.getSimpleValue().equals("")) {
                System.out.println("\n\n\nSEED = " + seedworld.getSimpleValue() + "\n\n\n");
                Repeat setResolution = new Repeat("setResolution");
                rep.add(setResolution);
                setResolution.add(new Var("displayHeight", height.getSimpleValue()));
                setResolution.add(new Var("displayWidth", width.getSimpleValue()));
            }
        }
    }

    private void generateSmartphones(String simId, Graph simDiags,
            Repeat rep) throws NullEntity, NotFound {
        for (GraphEntity smartphone : Utils.getEntities(simDiags, "ESmartPhone")) {
            Repeat createSPRep = new Repeat("createSP");
            rep.add(createSPRep);
            createSPRep.add(new Var("SPname", smartphone.getID()));

            String width = smartphone.getAttributeByName("WidthField").getSimpleValue();
            String height = smartphone.getAttributeByName("HeightField").getSimpleValue();
            String depth = smartphone.getAttributeByName("DepthField").getSimpleValue();

            if (!width.equals("") && !height.equals("") && !depth.equals("")) {
                Repeat setDims = new Repeat("createSPSetDim");
                createSPRep.add(setDims);
                setDims.add(new Var("width", width));
                setDims.add(new Var("height", height));
                setDims.add(new Var("depth", depth));
            }
            GraphEntity loc = Utils.getTargetEntity(smartphone, "InitialDeviceLocation", simDiags.getRelationships());
            if (loc != null) {
                if (loc.getType().equals("PartOfBody")) {
                    Repeat setLocRep = new Repeat("setLocPartOfBody");
                    createSPRep.add(setLocRep);
                    String humanId = loc.getAttributeByName("BelongsTo").getSimpleValue();
                    String partOfBody = loc.getAttributeByName("PartOfBodyName").getSimpleValue();
                    setLocRep.add(new Var("humanId", humanId));
                    setLocRep.add(new Var("partOfBody", partOfBody));
                } else if (loc.getType().equals("FTable")) {
                    Repeat setLocRep = new Repeat("setLocFurniture");
                    createSPRep.add(setLocRep);
                    setLocRep.add(new Var("furId", loc.getID()));
                } else if (loc.getType().equals("InitialPreDefPos")) {
                    Repeat setLocRep = new Repeat("setPreDefPos");
                    createSPRep.add(setLocRep);
                    String elementId = loc.getAttributeByName("ElementIdField").getSimpleValue();
                    String preDefPos = loc.getAttributeByName("PreDefPosNameField").getSimpleValue();
                    setLocRep.add(new Var("elementId", elementId));
                    setLocRep.add(new Var("preDefPos", preDefPos));
                }
            }
        }

        for (GraphEntity smartphone : Utils.getEntities(simDiags, "ESmartPhone")) {

            GraphEntity emu = Utils.getTargetEntity(smartphone, "EmulatorPeer", simDiags.getRelationships());
            if (emu != null) {
                GraphAttribute avdName = emu.getAttributeByName("AvdName");
                GraphAttribute avdSerialNumName = emu.getAttributeByName("AvdSerialNumber");
                if (avdName != null && !avdName.getSimpleValue().equals("")
                        && avdSerialNumName != null && !avdSerialNumName.getSimpleValue().equals("")) {
                    Repeat emulator = new Repeat("emulator");
                    rep.add(emulator);
                    emulator.add(new Var("SPname", smartphone.getID()));
                    emulator.add(new Var("AvdName", avdName.getSimpleValue()));
                    emulator.add(new Var("AvdSerialNum", avdSerialNumName.getSimpleValue()));

                    GraphAttribute apkFileField = emu.getAttributeByName("ApkFile");
                    System.out.println("ApkFile = " + apkFileField.getSimpleValue());
                    if (apkFileField != null && !apkFileField.getSimpleValue().equals("")) {
                        Repeat installApp = new Repeat("installApp");
                        emulator.add(installApp);
                        installApp.add(new Var("SPname", smartphone.getID()));
                        installApp.add(new Var("apkFile", apkFileField.getSimpleValue()));
                    }

                    GraphAttribute avdScreenFeed = emu.getAttributeByName("AvdScreenFeed");
                    if (avdScreenFeed != null && avdScreenFeed.getSimpleValue().equals("Yes")) {
                        Repeat avdScreen = new Repeat("avdScreen");
                        rep.add(avdScreen);
                        avdScreen.add(new Var("SPname", smartphone.getID()));
                        avdScreen.add(new Var("AvdName", avdName.getSimpleValue()));
                    }

                    GraphEntity runApp = Utils.getTargetEntity(emu, "RunAndroidApp", simDiags.getRelationships());
                    if (runApp != null) {
                        GraphAttribute packageName = runApp.getAttributeByName("PackageField");
                        GraphAttribute activityName = runApp.getAttributeByName("ActivityField");
                        if (packageName != null && !packageName.getSimpleValue().equals("")
                                && activityName != null && !activityName.getSimpleValue().equals("")) {
                            Repeat androidApp = new Repeat("AndroidApp");
                            emulator.add(androidApp);
                            androidApp.add(new Var("packageName", packageName.getSimpleValue()));
                            androidApp.add(new Var("activityName", activityName.getSimpleValue()));
                        }
                    }
                }
            }
        }
    }

    private GraphEntity getEntity(Graph diagram, String type) {
        try {
            for (GraphEntity ge : Utils.getEntities(diagram, type)) {
                return ge;
            }
        } catch (NullEntity e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getBodyType(String humanId) {
        for (Graph humanProfileDiagram : Utils.getGraphsByType(
                "HumanProfileSpecDiagram", browser)) {
            List<GraphEntity> humans;

            try {
                humans = Utils.getEntities(humanProfileDiagram, "Human");
                for (GraphEntity human : humans) {
                    if (human.getID().equals(humanId)) {
                        GraphEntity sp = getEntity(humanProfileDiagram, "SocialProfile");
                        if (sp != null) {
                            try {
                                GraphAttribute sd = sp
                                        .getAttributeByName("SocialSpecDiagField");
                                if (sd.getSimpleValue() != null) {
                                    System.out.println("Diagram name = "
                                            + sd.getSimpleValue());
                                    Graph socialProfile = Utils.getGraphByName(
                                            sd.getSimpleValue(), browser);
                                    GraphEntity pi = getEntity(socialProfile,
                                            "PersonalInfo");
                                    if (pi != null) {
                                        GraphAttribute ageAt = pi
                                                .getAttributeByName("AgeField");
                                        if (ageAt != null
                                                && !ageAt.getSimpleValue().equals("")) {
                                            int age = Integer.parseInt(ageAt
                                                    .getSimpleValue());
                                            if (age > 60) {
                                                return "ElderLP";
                                            } else {
                                                return "Young";
                                            }
                                        }
                                    }
                                }
                            } catch (NotFound e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }

                    }
                }

            } catch (NullEntity e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        return null;
    }

    public String getInitialLocation(String humanId, Graph simDiagram) {
        try {
            //  Graph[] diagrams = browser.getGraphs();
            // for (Graph simDiagram : diagrams) {
            //   if (simDiagram.getType().equalsIgnoreCase(SIMULATION_DIAGRAM)) {
            System.out.println("Diagram = " + simDiagram.getID());
            for (GraphEntity hi : simDiagram.getEntities()) {
                if (hi.getType().equalsIgnoreCase(
                        HUMAN_INITIALIZATION_OBJ)) {
                    GraphEntity human = Utils.getTargetEntity(hi,
                            "RelatedHuman");
                    if (human.getID().equals(humanId)) {
                        GraphEntity initialPos = Utils.getTargetEntity(
                                hi, "InitialLocation");
                        if (initialPos != null) {
                            return initialPos.getID();
                        }
                    }
                }
            }
            //  }
            // }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
