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
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import phat.environment.*;

import java.util.logging.Logger;

import phat.app.PHATApplication;
import phat.app.PHATInitAppListener;
import phat.audio.SingleAudioAppState;
import phat.body.BodiesAppState;
import phat.body.commands.SayASentenceBodyCommand;
import phat.body.commands.SetBodyHeightCommand;
import phat.structures.houses.HouseFactory;
import phat.structures.houses.TestHouse;
import phat.structures.houses.commands.CreateHouseCommand;
import phat.world.WorldAppState;

/**
 *
 * @author pablo
 */
public class TextBalloonTest implements PHATInitAppListener {

    private static final Logger logger = Logger.getLogger(TestHouse.class.getName());
    BodiesAppState bodiesAppState;
    SpatialEnvironmentAPI seAPI;
    
    final String bodyId = "Patient";

    public static void main(String[] args) {
        TextBalloonTest test = new TextBalloonTest();
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
        app.getFlyByCamera().setDragToRotate(true);

        BulletAppState bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        //bulletAppState.setDebugEnabled(true);

        seAPI = SpatialEnvironmentAPI.createSpatialEnvironmentAPI(app);

        seAPI.getWorldAppState().setCalendar(2013, 1, 1, 12, 0, 0);
        seAPI.getWorldAppState().setLandType(WorldAppState.LandType.Grass);
        seAPI.getHouseAppState().runCommand(new CreateHouseCommand("House1", HouseFactory.HouseType.House3room2bath));
        
        SingleAudioAppState singleAudioAppState = new SingleAudioAppState();
        stateManager.attach(singleAudioAppState);

        bodiesAppState = new BodiesAppState();
        stateManager.attach(bodiesAppState);

        bodiesAppState.createBody(BodiesAppState.BodyType.Elder, bodyId);
        bodiesAppState.runCommand(new SetBodyHeightCommand(bodyId, 1.7f));
        bodiesAppState.setInSpace(bodyId, "House1", "BathRoom1");
        
        app.getStateManager().attach(new AbstractAppState() {
            String [] sentences = {"It's to early.", "You should return to the bed"};
            int index = 0;
            float timer = 5f;
            Application app;
            
            @Override
            public void initialize(AppStateManager stateManager, Application app) {
                this.index = 0;
                this.timer = 5f;
                this.app = app;
            }
            
            @Override
            public void update(float tpf) {
                timer -= tpf;
                if(timer <= 0 && index < sentences.length) {
                    timer = 5f;
                    app.getStateManager().getState(BodiesAppState.class).runCommand(new SayASentenceBodyCommand(bodyId, sentences[index]));
                    index++;
                }
            }
        });
    }
}