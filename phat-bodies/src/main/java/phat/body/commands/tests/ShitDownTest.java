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
package phat.body.commands.tests;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import phat.environment.*;

import java.util.logging.Logger;

import phat.app.PHATApplication;
import phat.app.PHATInitAppListener;
import phat.body.BodiesAppState;
import phat.body.commands.GoCloseToObjectCommand;
import phat.body.commands.GoToSpaceCommand;
import phat.body.commands.RotateTowardCommand;
import phat.body.commands.SitDownCommand;
import phat.body.commands.StandUpCommand;
import phat.body.commands.TremblingHandCommand;
import phat.body.commands.TremblingHeadCommand;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.structures.houses.HouseFactory;
import phat.structures.houses.TestHouse;
import phat.structures.houses.commands.CreateHouseCommand;
import phat.util.SpatialUtils;
import phat.world.WorldAppState;

/**
 *
 * @author pablo
 */
public class ShitDownTest implements PHATInitAppListener {

    private static final Logger logger = Logger.getLogger(TestHouse.class.getName());
    BodiesAppState bodiesAppState;
    SpatialEnvironmentAPI seAPI;

    public static void main(String[] args) {
        ShitDownTest test = new ShitDownTest();
        PHATApplication phat = new PHATApplication(test);
        phat.start();
    }

    @Override
    public void init(SimpleApplication app) {
        AppStateManager stateManager = app.getStateManager();

        app.getFlyByCamera().setMoveSpeed(10f);

        BulletAppState bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        //bulletAppState.setDebugEnabled(true);

        seAPI = SpatialEnvironmentAPI.createSpatialEnvironmentAPI(app);

        seAPI.getWorldAppState().setCalendar(2013, 1, 1, 12, 0, 0);
        seAPI.getHouseAppState().runCommand(new CreateHouseCommand("House1", HouseFactory.HouseType.House3room2bath));

        bodiesAppState = new BodiesAppState();
        stateManager.attach(bodiesAppState);

        final String seatId = "Chair3";//"Sofa3Seats";"ArmChair1";
        bodiesAppState.createBody(BodiesAppState.BodyType.Elder, "Patient");
        bodiesAppState.setInSpace("Patient", "House1", "LivingRoom");
        //bodiesAppState.runCommand(new GoToSpaceCommand("Patient", "Kitchen"));
        bodiesAppState.runCommand(new GoCloseToObjectCommand("Patient", seatId, new PHATCommandListener() {
            @Override
            public void commandStateChanged(PHATCommand command) {
                //System.out.println("Notify: (" + command.getState().name() + ") " + command);
                if (command.getState() == PHATCommand.State.Success) {
                    bodiesAppState.runCommand(new SitDownCommand("Patient", seatId));
                    /* bodiesAppState.runCommand(
                     new SayAdSentenceBodyCommand("Patient", "Good! Water! I am thirsty!"));
                     bodiesAppState.runCommand(
                     new PlayBodyAnimationCommand("Patient", 
                     BasicCharacterAnimControl.AnimName.DrinkStanding.name()));*/
                }
            }
        }));
        bodiesAppState.runCommand(new TremblingHeadCommand("Patient", true));
        bodiesAppState.runCommand(new TremblingHandCommand("Patient", true, true));
        bodiesAppState.runCommand(new TremblingHandCommand("Patient", true, false));
        //bodiesAppState.runCommand(new SetCameraToBodyCommand("Patient"));
        //bodiesAppState.runCommand(new SetPCListenerToBodyCommand("Patient"));
        //bodiesAppState.runCommand(new BodyLabelCommand("Patient", true));


        stateManager.attach(new AbstractAppState() {
            PHATApplication app;

            @Override
            public void initialize(AppStateManager asm, Application aplctn) {
                app = (PHATApplication) aplctn;

            }
            boolean standUp = false;
            boolean standUp2 = false;
            
            @Override
            public void update(float f) {
                if(!standUp2 && seAPI.getWorldAppState().getCalendar().pastTime(12, 0, 55, 10*60)) {
                    bodiesAppState.runCommand(new StandUpCommand("Patient"));
                    standUp2 = true;
                }
                if (!standUp && seAPI.getWorldAppState().getCalendar().pastTime(12, 0, 20, 10*60)) {
                    final GoCloseToObjectCommand gctoc = new GoCloseToObjectCommand("Patient", "Chair1", new PHATCommandListener() {

                        @Override
                        public void commandStateChanged(PHATCommand command) {
                            if (command.getState() == PHATCommand.State.Success) {                                
                                bodiesAppState.runCommand(new SitDownCommand("Patient", "Chair1"));
                            }
                        }
                    });
                    bodiesAppState.runCommand(new StandUpCommand("Patient", new PHATCommandListener() {
                        @Override
                        public void commandStateChanged(PHATCommand command) {
                            if (command.getState() == PHATCommand.State.Success) {                                
                                bodiesAppState.runCommand(gctoc);
                            }
                        }
                    }));
                    
                    standUp = true;
                }
            }
        });
    }
}