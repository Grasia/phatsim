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

import java.util.List;

public class SimulationGenerator {

    static final String HUMAN_PROFILE_SPEC_DIAGRAM = "HumanProfileSpecDiagram";
    static final String ADLProfile_SPEC_DIAGRAM = "ADLProfile";
    static final String SIMULATION_DIAGRAM = "SimulationDiagram";
    static final String HUMAN_INITIALIZATION_OBJ = "HumanInitialization";
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

            generateWorldInitialization(simId, simDiag, simInitRep);
            generatePeopleInitialization(simDiag, simInitRep);
            generateCameraPositionToBody(simDiag, simInitRep);
            generateSmartphones(simId, simDiag, simInitRep);
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
                        GraphAttribute ga = human.getAttributeByName("ShowName");
                        if (ga != null) {
                            String valueName = ga.getSimpleValue();
                            if (valueName != null && valueName.equals("Yes")) {
                                showName = "true";
                            }
                        }
                    } catch (NotFound nf) {
                    }
                    bodyRep.add(new Var("showName", showName));
                    String initialLoc = getInitialLocation(humanId, simDiag);
                    System.out.println("humanId=" + humanId + ",il="
                            + initialLoc);
                    if (initialLoc != null) {
                        bodyRep.add(new Var("iniLoc", initialLoc));
                    }

                    if (TimeIntervalsGenerator.getADL(humanId, browser) != null) {
                        Repeat bodyRep1 = new Repeat("agent");
                        simInitRep.add(bodyRep1);
                        bodyRep1.add(new Var("agentname", humanId));
                    }
                }


            }
        }
    }

    private void generateCameraPositionToBody(Graph simDiag, Repeat simInitRep)
            throws NullEntity, NotFound {
        for (GraphEntity hi : Utils.getEntities(simDiag, "CameraInit")) {
            GraphEntity human = Utils.getTargetEntity(hi, "CameraFaceToHuman", simDiag.getRelationships());
            if (human != null) {
                String humanId = human.getID();

                Repeat camRep = new Repeat("CameraToBodyInit");
                simInitRep.add(camRep);
                camRep.add(new Var("actorname", Utils.replaceBadChars(humanId)));
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
    }

    private void generateSmartphones(String simId, Graph simDiags,
            Repeat rep) throws NullEntity, NotFound {
        for (GraphEntity smartphone : Utils.getEntities(simDiags, "ESmartPhone")) {
            Repeat createSPRep = new Repeat("createSP");
            rep.add(createSPRep);
            createSPRep.add(new Var("SPname", smartphone.getID()));

            GraphEntity loc = Utils.getTargetEntity(smartphone, "InitialDeviceLocation", simDiags.getRelationships());
            if (loc != null && !loc.equals("")) {
                Repeat setLocRep = new Repeat("setLoc");
                createSPRep.add(setLocRep);
                String humanId = loc.getAttributeByName("BelongsTo").getSimpleValue();
                String partOfBody = loc.getAttributeByName("PartOfBodyName").getSimpleValue();
                setLocRep.add(new Var("humanId", humanId));
                setLocRep.add(new Var("partOfBody", partOfBody));
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
