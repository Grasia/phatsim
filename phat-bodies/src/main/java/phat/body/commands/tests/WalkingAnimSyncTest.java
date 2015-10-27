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
import com.jme3.effect.ParticleEmitter;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import phat.environment.*;

import java.util.logging.Logger;

import phat.app.PHATApplication;
import phat.app.PHATInitAppListener;
import phat.audio.MultiAudioAppState;
import phat.audio.AudioFactory;
import phat.body.BodiesAppState;
import phat.body.commands.AlignWithCommand;
import phat.body.commands.CloseObjectCommand;
import phat.body.commands.FallDownCommand;
import phat.body.commands.GoCloseToObjectCommand;
import phat.body.commands.GoIntoBedCommand;
import phat.body.commands.GoToCommand;
import phat.body.commands.GoToSpaceCommand;
import phat.body.commands.OpenObjectCommand;
import phat.body.commands.PlayBodyAnimationCommand;
import phat.body.commands.RandomWalkingCommand;
import phat.body.commands.RotateTowardCommand;
import phat.body.commands.SetBodyInCoordenatesCommand;
import phat.body.commands.SetCameraToBodyCommand;
import phat.body.commands.SetPCListenerToBodyCommand;
import phat.body.commands.SetSpeedDisplacemenetCommand;
import phat.body.commands.SitDownCommand;
import phat.body.commands.StandUpCommand;
import phat.body.commands.TremblingHandCommand;
import phat.body.commands.TremblingHeadCommand;
import phat.body.commands.TripOverCommand;
import phat.body.control.animation.BasicCharacterAnimControl;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.structures.houses.HouseFactory;
import phat.structures.houses.TestHouse;
import phat.structures.houses.commands.CreateHouseCommand;
import phat.structures.houses.commands.DebugShowHouseNavMeshCommand;
import phat.util.Debug;
import phat.world.WorldAppState;

/**
 *
 * @author pablo
 */
public class WalkingAnimSyncTest implements PHATInitAppListener {

    private static final Logger logger = Logger.getLogger(TestHouse.class.getName());
    BodiesAppState bodiesAppState;
    SpatialEnvironmentAPI seAPI;

    public static void main(String[] args) {
        WalkingAnimSyncTest test = new WalkingAnimSyncTest();
        PHATApplication phat = new PHATApplication(test);
        phat.setDisplayFps(true);
        phat.setDisplayStatView(false);
        phat.start();
    }

    @Override
    public void init(SimpleApplication app) {
        AppStateManager stateManager = app.getStateManager();

        app.getFlyByCamera().setMoveSpeed(10f);

        app.getCamera().setLocation(new Vector3f(4.497525f, 6.3693237f, 4.173162f));
        app.getCamera().setRotation(new Quaternion(0.5199084f, 0.42191547f, -0.32954147f, 0.6656463f));

        BulletAppState bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        //bulletAppState.setDebugEnabled(true);
        //bulletAppState.getPhysicsSpace().setAccuracy(1f/200f);

        MultiAudioAppState audio = new MultiAudioAppState();
        stateManager.attach(audio);
        
        seAPI = SpatialEnvironmentAPI.createSpatialEnvironmentAPI(app);

        seAPI.getWorldAppState().setCalendar(2013, 1, 1, 12, 0, 0);
        seAPI.getWorldAppState().setLandType(WorldAppState.LandType.Grass);
        //seAPI.getHouseAppState().runCommand(new CreateHouseCommand("House1", HouseFactory.HouseType.House3room2bath));
        //seAPI.getHouseAppState().runCommand(new DebugShowHouseNavMeshCommand(true));
        
        bodiesAppState = new BodiesAppState();
        stateManager.attach(bodiesAppState);

        bodiesAppState.createBody(BodiesAppState.BodyType.ElderLP, "Patient");
        bodiesAppState.runCommand(new SetBodyInCoordenatesCommand("Patient", Vector3f.ZERO));
        
        bodiesAppState.runCommand(new RandomWalkingCommand("Patient", true));
        bodiesAppState.runCommand(new SetCameraToBodyCommand("Patient"));
        bodiesAppState.runCommand(new SetPCListenerToBodyCommand("Patient"));
        //bodiesAppState.runCommand(new BodyLabelCommand("Patient", true));


        Debug.enableDebugGrid(20f, app.getAssetManager(), app.getRootNode());
        
        stateManager.attach(new AbstractAppState() {
            PHATApplication app;

            @Override
            public void initialize(AppStateManager asm, Application aplctn) {
                app = (PHATApplication) aplctn;
            }
            boolean standUp = false;
            boolean washingHands = false;
            boolean havingShower = false;

            boolean init = false;
            float sign = 1f;
            float maxSpeed = 4f;
            float incSpeed = 0.5f;
            float speed = 4.1f;
            float time = 0f;
            @Override
            public void update(float f) {
                time += f;
                if(!init) {
                    //bodiesAppState.getAvailableBodies().get("Patient").setUserData("Speed", 1f);
                    init = true;
                }
                if(time > 2f) {
                    if(speed >= maxSpeed) {
                        sign *= -1f;
                    } else if(speed <= 0f) {
                        sign *= -1f;
                    }
                    speed = speed + sign*incSpeed;
                    bodiesAppState.runCommand(new SetSpeedDisplacemenetCommand("Patient", speed));
                    //bodiesAppState.getAvailableBodies().get("Patient").setUserData("Speed", speed);
                    time = 0f;
                }
            }
        });
    }

    private void sitDown(final String placeToSit) {
        /*GoCloseToObjectCommand gtc = new GoCloseToObjectCommand("Patient", placeToSit, new PHATCommandListener() {
            @Override
            public void commandStateChanged(PHATCommand command) {
                if (command.getState() == PHATCommand.State.Success) {
                    bodiesAppState.runCommand(new SitDownCommand("Patient", placeToSit));
                }
            }
        });
        gtc.setMinDistance(0.1f);
        bodiesAppState.runCommand(gtc);*/
        bodiesAppState.runCommand(new SitDownCommand("Patient", placeToSit));
    }
    
    private void goToUse(final String obj) {
        GoCloseToObjectCommand gtc = new GoCloseToObjectCommand("Patient", obj, new PHATCommandListener() {
            @Override
            public void commandStateChanged(PHATCommand command) {
                if (command.getState() == PHATCommand.State.Success) {
                    bodiesAppState.runCommand(new AlignWithCommand("Patient", obj));
                    bodiesAppState.runCommand(new OpenObjectCommand("Patient", obj));
                }
            }
        });
        gtc.setMinDistance(0.05f);
        bodiesAppState.runCommand(gtc);
    }
}