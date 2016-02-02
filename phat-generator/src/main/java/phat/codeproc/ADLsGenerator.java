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
import ingenias.generator.browser.GraphRelationship;
import ingenias.generator.datatemplate.Repeat;
import ingenias.generator.datatemplate.Sequences;
import ingenias.generator.datatemplate.Var;
import java.util.Collection;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import static phat.codeproc.ActivityGenerator.ACTIVITY_TYPE;

public class ADLsGenerator {

    final static String TIME_INTERVAL_TYPE = "TimeInterval";
    final static String INTERVAL_CLOCK_REL = "TIStartTime";
    final static String ADLProfile_SPEC_DIAGRAM = "ADLProfile";
    final static String HOURS_FIELD = "HoursField";
    final static String MINS_FIELD = "MinutesField";
    final static String SECS_FIELD = "SecondsField";
    Browser browser;

    public ADLsGenerator(Browser browser) {
        this.browser = browser;
    }

    public void generateADLClasses(Sequences sequence) throws NotFound {
        for (Graph adlSpec : Utils.getGraphsByType("ADLSpecDiagram", browser)) {
            try {
                if (adlSpec.getEntities().length > 0) {
                    Repeat adlsRep = new Repeat("adls");
                    adlsRep.add(new Var("adlID", Utils.replaceBadChars(adlSpec.getID())));
                    adlsRep.add(new Var("adlType", Utils.replaceBadChars(adlSpec.getType())));
                    adlsRep.add(new Var("adlDesc", Utils.replaceBadChars(adlSpec.getType())));
                    sequence.addRepeat(adlsRep);

                    System.out.println("ADL no empty!");

                    for (GraphEntity timeInterval : adlSpec.getEntities()) {
                        if (timeInterval.getType().equals(TIME_INTERVAL_TYPE)) {
                            String timeIntervalName = timeInterval.getID();

                            Repeat instRep = new Repeat("tiInst");
                            adlsRep.add(instRep);
                            instRep.add(new Var("tiID", Utils.replaceBadChars(timeIntervalName)));

                            GraphEntity geClock = Utils.getTargetEntity(timeInterval,
                                    INTERVAL_CLOCK_REL);
                            System.out.println(timeInterval.getID());
                            System.out.println(INTERVAL_CLOCK_REL + " = " + geClock);
                            if (geClock != null) {
                                GraphAttribute gaHours = geClock
                                        .getAttributeByName(HOURS_FIELD);
                                int hours = Integer.parseInt(gaHours.getSimpleValue());
                                GraphAttribute gaMins = geClock
                                        .getAttributeByName("MinutesField");
                                int mins = Integer.parseInt(gaMins.getSimpleValue());
                                GraphAttribute gaSecs = geClock
                                        .getAttributeByName(SECS_FIELD);
                                int secs = Integer.parseInt(gaSecs.getSimpleValue());
                                System.out.println(geClock.getID() + ": " + hours + ":"
                                        + mins + ":" + secs);
                                Repeat timeRep = new Repeat("tiTime");
                                instRep.add(timeRep);
                                timeRep.add(new Var("h", String.valueOf(hours)));
                                timeRep.add(new Var("m", String.valueOf(mins)));
                                timeRep.add(new Var("s", String.valueOf(secs)));
                            }

                            Collection<GraphEntity> nextEntities = Utils.getTargetsEntity(timeInterval,
                                    "NextTI");
                            if (nextEntities.isEmpty()) {
                                // It is the last time interval
                                System.out.println("REGISTER FINAL STATE!!!!! -> " + Utils.replaceBadChars(timeInterval.getID()));
                                Repeat lastRep = new Repeat("tiLast");
                                adlsRep.add(lastRep);
                                lastRep.add(new Var("tiID", Utils.replaceBadChars(timeInterval.getID())));
                            } else {
                                for (GraphEntity timeIntervalNext : nextEntities) {
                                    Repeat transRep = new Repeat("tiTrans");
                                    adlsRep.add(transRep);
                                    transRep.add(new Var("tiIDS", Utils.replaceBadChars(timeInterval.getID())));
                                    transRep.add(new Var("tiIDT", Utils.replaceBadChars(timeIntervalNext.getID())));
                                }
                            }
                        }
                    }
                    GraphEntity ge = Utils.getFirstEntity(adlSpec);
                    Repeat repFirst = new Repeat("tiFirst");
                    adlsRep.add(repFirst);
                    repFirst.add(new Var("tiID", Utils.replaceBadChars(ge.getID())));
                }
            } catch (NullEntity ex) {
                Logger.getLogger(ADLsGenerator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println(".............................generateAllSeqTasks");
    }

    public void generateADLBack(String humanId, Repeat repFather) throws NotFound,
            NullEntity {

        GraphEntity adl = getADL(humanId, browser);
        if (adl == null) {
            return;
        }
        GraphAttribute ga = adl.getAttributeByName("ADLSpecField");
        if (ga == null || ga.getSimpleValue().equals("")) {
            return;
        }

        String adlDiagName = ga.getSimpleValue();
        System.out.println("GENERATE ADL: " + humanId);
        System.out.println("ADL = " + adlDiagName);
        Graph adlSpec = browser.getGraph(adlDiagName);
        if (adlSpec != null && adlSpec.getEntities().length > 0) {
            System.out.println("ADL no empty!");
            GraphEntity ge = Utils.getFirstEntity(adlSpec);
            Repeat repFirst = new Repeat("firstTimeInterval");
            repFather.add(repFirst);
            repFirst.add(new Var("tiname", Utils.replaceBadChars(ge.getID())));

            System.out.println("----------> First: " + Utils.replaceBadChars(ge.getID()));
            while (ge != null) {
                System.out.println(ge.getType() + ": " + ge.getID());
                if (ge.getType().equals(TIME_INTERVAL_TYPE)) {
                    String timeIntervalName = ge.getID();
                    Repeat rep = new Repeat("timeInstance");
                    repFather.add(rep);
                    rep.add(new Var("tiname", Utils.replaceBadChars(timeIntervalName)));
                    GraphEntity geClock = Utils.getTargetEntity(ge,
                            INTERVAL_CLOCK_REL);
                    if (geClock != null) {
                        GraphAttribute gaHours = geClock
                                .getAttributeByName(HOURS_FIELD);
                        int hours = Integer.parseInt(gaHours.getSimpleValue());
                        GraphAttribute gaMins = geClock
                                .getAttributeByName("MinutesField");
                        int mins = Integer.parseInt(gaMins.getSimpleValue());
                        GraphAttribute gaSecs = geClock
                                .getAttributeByName(SECS_FIELD);
                        int secs = Integer.parseInt(gaSecs.getSimpleValue());
                        System.out.println(geClock.getID() + ": " + hours + ":"
                                + mins + ":" + secs);
                        Repeat rep2 = new Repeat("timeTransition");
                        rep.add(rep2);
                        rep.add(new Var("hours", String.valueOf(hours)));
                        rep.add(new Var("minutes", String.valueOf(mins)));
                        rep.add(new Var("seconds", String.valueOf(secs)));
                    }
                    System.out.println("NextTI of " + ge.getID());
                    GraphEntity geNext = Utils.getTargetEntity(ge,
                            "NextTI");
                    if (geNext != null) {
                        Repeat rep3 = new Repeat("regTrans");
                        repFather.add(rep3);
                        rep3.add(new Var("tinameS", Utils.replaceBadChars(ge.getID())));
                        rep3.add(new Var("tinameT", Utils.replaceBadChars(geNext.getID())));
                    } else {
                        // It is the last time interval
                        System.out.println("REGISTER FINAL STATE!!!!! -> " + Utils.replaceBadChars(ge.getID()));
                        Repeat rep3 = new Repeat("regLastActivityRep");
                        repFather.add(rep3);
                        rep3.add(new Var("finalActivity", Utils.replaceBadChars(ge.getID())));
                    }
                    ge = geNext;
                } else {
                    System.out.println(ge.getType() + ": " + ge.getID());
                    break;
                }
            }
        }
    }

    public static String getADLName(String humanId, Browser browser) throws NotFound {
        GraphEntity ge = getADL(humanId, browser);
        if (ge != null) {
            GraphAttribute ga = ge.getAttributeByName("ADLSpecField");
            if(ga != null && !ga.getSimpleValue().equals("")) {
                return Utils.replaceBadChars(ga.getSimpleValue());
            }
        }
        return null;
    }

    public static GraphEntity getADL(String humanId, Browser browser) {
        GraphEntity result = null;
        try {
            GraphEntity[] entities = browser.getAllEntities();
            for (GraphEntity adl : entities) {
                if (adl.getType().equalsIgnoreCase(ADLProfile_SPEC_DIAGRAM)) {
                    Vector<GraphRelationship> rels = adl.getAllRelationships("ProfileOf");
                    for (GraphRelationship rel : rels) {
                        GraphEntity connectedHuman = Utils.getTargetEntity(adl, rel);
                        if (connectedHuman != null && connectedHuman.getID().equalsIgnoreCase(humanId)) {
                            return adl;
                        }
                    }
                }
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        return result;
    }
}
