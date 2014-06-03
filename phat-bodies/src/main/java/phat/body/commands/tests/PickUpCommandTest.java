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
import phat.body.commands.PickUpCommand;
import phat.body.commands.PlayBodyAnimationCommand;
import phat.body.commands.SayASentenceBodyCommand;
import phat.body.commands.SetCameraToBodyCommand;
import phat.body.commands.SetPCListenerToBodyCommand;
import phat.body.commands.TremblingHandCommand;
import phat.body.commands.TremblingHeadCommand;
import phat.body.control.animation.BasicCharacterAnimControl;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.structures.houses.HouseFactory;
import phat.structures.houses.TestHouse;
import phat.structures.houses.commands.CreateHouseCommand;

/**
 *
 * @author pablo
 */
public class PickUpCommandTest implements PHATInitAppListener {

    private static final Logger logger = Logger.getLogger(TestHouse.class.getName());
    
    BodiesAppState bodiesAppState;
    
    public static void main(String[] args) {
    	TestBodiesAppState test = new TestBodiesAppState();
    	PHATApplication phat = new PHATApplication(test);
		phat.start();
    }

	@Override
	public void init(SimpleApplication app) {
		AppStateManager stateManager = app.getStateManager();
		
		app.getFlyByCamera().setMoveSpeed(10f);
        
		BulletAppState bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        bulletAppState.setDebugEnabled(true);
        
        SpatialEnvironmentAPI seAPI = SpatialEnvironmentAPI.createSpatialEnvironmentAPI(app);
        
        seAPI.getWorldAppState().setCalendar(2013, 1, 1, 12, 0, 0);
        seAPI.getHouseAppState().runCommand(new CreateHouseCommand("House1", HouseFactory.HouseType.House3room2bath));
                
        bodiesAppState = new BodiesAppState();
        stateManager.attach(bodiesAppState);
        
        bodiesAppState.createBody(BodiesAppState.BodyType.Elder, "Patient");
        bodiesAppState.setInSpace("Patient", "House1", "BedRoom1");
        //bodiesAppState.runCommand(new GoToSpaceCommand("Patient", "Kitchen"));
        bodiesAppState.runCommand(new GoCloseToObjectCommand("Patient", "Bottle1", new PHATCommandListener() {
            @Override
            public void commandStateChanged(PHATCommand command) {
                if(command.getState() == PHATCommand.State.Success) {
                    bodiesAppState.runCommand(new PickUpCommand("Patient", "Bottle1", PickUpCommand.Hand.Right));
                   /* bodiesAppState.runCommand(
                new SayASentenceBodyCommand("Patient", "Good! Water! I am thirsty!"));
                    bodiesAppState.runCommand(
                new PlayBodyAnimationCommand("Patient", 
                BasicCharacterAnimControl.AnimName.DrinkStanding.name()));*/
                }
            }
        }));
        bodiesAppState.runCommand(new TremblingHeadCommand("Patient", true));
        bodiesAppState.runCommand(new TremblingHandCommand("Patient", true, true));
        bodiesAppState.runCommand(new TremblingHandCommand("Patient", true, false));
        bodiesAppState.runCommand(new SetCameraToBodyCommand("Patient"));
        bodiesAppState.runCommand(new SetPCListenerToBodyCommand("Patient"));
        //bodiesAppState.runCommand(new BodyLabelCommand("Patient", true));
	}
}