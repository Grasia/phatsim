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

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import phat.environment.*;

import java.util.logging.Logger;

import phat.app.PHATApplication;
import phat.app.PHATInitAppListener;
import phat.body.BodiesAppState;
import phat.body.TestBodiesAppState;
import phat.body.commands.BodyLabelCommand;
import phat.body.commands.GoCloseToBodyCommand;
import phat.body.commands.GoCloseToObjectCommand;
import phat.body.commands.GoToSpaceCommand;
import phat.body.commands.LookAtCommand;
import phat.body.commands.PickUpCommand;
import phat.body.commands.PlayBodyAnimationCommand;
import phat.body.commands.RotateTowardCommand;
import phat.body.commands.SayASentenceBodyCommand;
import phat.body.commands.SetCameraToBodyCommand;
import phat.body.commands.SetPCListenerToBodyCommand;
import phat.body.commands.SitDownCommand;
import phat.body.commands.TremblingHandCommand;
import phat.body.commands.TremblingHeadCommand;
import phat.body.control.animation.BasicCharacterAnimControl;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.structures.houses.HouseFactory;
import phat.structures.houses.TestHouse;
import phat.structures.houses.commands.CreateHouseCommand;
import phat.world.WorldAppState;

/**
 *
 * @author pablo
 */
public class PickUpCommandTest implements PHATInitAppListener {

    private static final Logger logger = Logger.getLogger(TestHouse.class.getName());
    BodiesAppState bodiesAppState;
    SpatialEnvironmentAPI seAPI;

    public static void main(String[] args) {
        PickUpCommandTest test = new PickUpCommandTest();
        PHATApplication phat = new PHATApplication(test);
        phat.setDisplayFps(true);
        phat.setDisplayStatView(false);
        phat.start();
    }

    @Override
    public void init(SimpleApplication app) {
        AppStateManager stateManager = app.getStateManager();

        app.getFlyByCamera().setMoveSpeed(10f);
        app.getCamera().setFrustumPerspective(45f, 
                (float) app.getCamera().getWidth() / app.getCamera().getHeight(), 
                0.1f, 1000f);
        
        app.getCamera().setLocation(new Vector3f(4.497525f, 6.3693237f, 4.173162f));
        app.getCamera().setRotation(new Quaternion(0.5199084f, 0.42191547f, -0.32954147f, 0.6656463f));

        seAPI = SpatialEnvironmentAPI.createSpatialEnvironmentAPI(app);

        seAPI.getWorldAppState().setCalendar(2013, 1, 1, 12, 0, 0);
        seAPI.getWorldAppState().setLandType(WorldAppState.LandType.Basic);
        seAPI.getHouseAppState().runCommand(new CreateHouseCommand("House1", HouseFactory.HouseType.House3room2bath));
        //seAPI.getHouseAppState().runCommand(new DebugShowHouseNavMeshCommand(true));

        bodiesAppState = new BodiesAppState();
        stateManager.attach(bodiesAppState);

        bodiesAppState.createBody(BodiesAppState.BodyType.Elder, "Patient");
        bodiesAppState.setInSpace("Patient", "House1", "LivingRoom");
        //bodiesAppState.runCommand(new GoToSpaceCommand("Patient", "Kitchen"));
        bodiesAppState.runCommand(new LookAtCommand("Patient", "TVRemote1"));
        bodiesAppState.runCommand(new RotateTowardCommand("Patient", "TVRemote1"));
        bodiesAppState.runCommand(new GoCloseToObjectCommand("Patient", "TVRemote1", new PHATCommandListener() {
            @Override
            public void commandStateChanged(PHATCommand command) {
                if (command.getState() == PHATCommand.State.Success) {
                    bodiesAppState.runCommand(new PickUpCommand("Patient", "TVRemote1", PickUpCommand.Hand.Left));
                    bodiesAppState.runCommand(new LookAtCommand("Patient", "Screen1"));
                    bodiesAppState.runCommand(new RotateTowardCommand("Patient", "LcdTV1", new PHATCommandListener() {
                        @Override
                        public void commandStateChanged(PHATCommand command) {
                            if (command.getState() == PHATCommand.State.Success) {
                                bodiesAppState.runCommand(new SitDownCommand("Patient", "Sofa3Seats"));
                            }
                        }
                    }));
                }
            }
        }));
        bodiesAppState.runCommand(new TremblingHeadCommand("Patient", true));
        bodiesAppState.runCommand(new TremblingHandCommand("Patient", true, true));
        bodiesAppState.runCommand(new TremblingHandCommand("Patient", true, false));
        //bodiesAppState.runCommand(new SetCameraToBodyCommand("Patient"));
        //bodiesAppState.runCommand(new SetPCListenerToBodyCommand("Patient"));
        //bodiesAppState.runCommand(new BodyLabelCommand("Patient", true));
    }
}