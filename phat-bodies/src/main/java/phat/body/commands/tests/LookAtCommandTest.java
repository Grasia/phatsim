/*
 * Copyright (C) 2014 pablo <pabcampi@ucm.es>
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
import java.util.logging.Logger;
import phat.app.PHATApplication;
import phat.app.PHATInitAppListener;
import phat.body.BodiesAppState;
import phat.body.commands.LookAtCommand;
import phat.body.commands.SetBodyHeightCommand;
import phat.body.commands.ShowLabelsOfVisibleObjectsCommand;
import phat.body.commands.SitDownCommand;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.environment.SpatialEnvironmentAPI;
import phat.structures.houses.HouseFactory;
import phat.structures.houses.TestHouse;
import phat.structures.houses.commands.CreateHouseCommand;
import phat.world.WorldAppState;

/**
 *
 * @author pablo <pabcampi@ucm.es>
 */
public class LookAtCommandTest implements PHATInitAppListener {

    private static final Logger logger = Logger.getLogger(TestHouse.class.getName());
    BodiesAppState bodiesAppState;
    SpatialEnvironmentAPI seAPI;

    public static void main(String[] args) {
        LookAtCommandTest test = new LookAtCommandTest();
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
        seAPI.getWorldAppState().setLandType(WorldAppState.LandType.Basic);
        seAPI.getHouseAppState().runCommand(new CreateHouseCommand("House1", HouseFactory.HouseType.House3room2bath));

        bodiesAppState = new BodiesAppState();
        stateManager.attach(bodiesAppState);

        String bodyId = "Young";
        bodiesAppState.createBody(BodiesAppState.BodyType.Young, bodyId);
        bodiesAppState.runCommand(new SetBodyHeightCommand(bodyId, 1.7f));
        bodiesAppState.setInSpace(bodyId, "House1", "Hall");
        
        
        bodiesAppState.createBody(BodiesAppState.BodyType.ElderLP, "Patient");
        bodiesAppState.runCommand(new SetBodyHeightCommand("Patient", 1.7f));
        bodiesAppState.setInSpace("Patient", "House1", "LivingRoom");
        //bodiesAppState.runCommand(new RotateTowardCommand("Patient", "ArmChair1"));
        bodiesAppState.runCommand(new LookAtCommand("Patient", "Young"));
        bodiesAppState.runCommand(new ShowLabelsOfVisibleObjectsCommand("Patient", true));
        bodiesAppState.runCommand(new SitDownCommand("Patient", "Chair1", new PHATCommandListener() {
            @Override
            public void commandStateChanged(PHATCommand command) {
                if (command.getState().equals(PHATCommand.State.Success)) {
                    bodiesAppState.runCommand(new LookAtCommand("Patient", "Chair2"));
                    bodiesAppState.runCommand(new ShowLabelsOfVisibleObjectsCommand("Patient", false));
                }
            }
        }));
        
    }
}