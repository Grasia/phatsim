/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.body;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import phat.environment.*;

import java.util.logging.Logger;

import phat.app.PHATApplication;
import phat.app.PHATInitAppListener;
import phat.body.BodiesAppState.BodyType;
import phat.body.commands.BodyLabelCommand;
import phat.body.commands.GoCloseToBodyCommand;
import phat.body.commands.GoToSpaceCommand;
import phat.body.commands.PlayBodyAnimationCommand;
import phat.body.commands.SayASentenceBodyCommand;
import phat.body.commands.SetPCListenerToBodyCommand;
import phat.body.commands.TremblingHandCommand;
import phat.body.commands.TremblingHeadCommand;
import phat.body.control.animation.BasicCharacterAnimControl;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.structures.houses.HouseFactory;
import phat.structures.houses.TestHouse;
import phat.structures.houses.HouseFactory.HouseType;
import phat.structures.houses.commands.CreateHouseCommand;

/**
 *
 * @author pablo
 */
public class TestBodiesAppState implements PHATInitAppListener {

    private static final Logger logger = Logger.getLogger(TestHouse.class.getName());
    
    BodiesAppState bodiesAppState;
    
    public static void main(String[] args) {
    	TestBodiesAppState test = new TestBodiesAppState();
    	PHATApplication phat = new PHATApplication(test);
		phat.start();
        //app.setMultiAudioRenderer(false);
        //app.setDisplayFps(false);
        //app.setShowSettings(false);
        //app.setPauseOnLostFocus(false);
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
        bodiesAppState.runCommand(new GoToSpaceCommand("Patient", "LivingRoom", new PHATCommandListener() {
            @Override
            public void commandStateChanged(PHATCommand command) {
                if(command.getState() == PHATCommand.State.Success) {
                    bodiesAppState.runCommand(new GoToSpaceCommand("Patient", "Kitchen"));
                }
            }
        }));
        bodiesAppState.runCommand(new TremblingHeadCommand("Patient", true));
        bodiesAppState.runCommand(new TremblingHandCommand("Patient", true, true));
        bodiesAppState.runCommand(new TremblingHandCommand("Patient", true, false));
        //bodiesAppState.runCommand(new SetCameraToBodyCommand("Patient"));
        bodiesAppState.runCommand(new SetPCListenerToBodyCommand("Patient"));
        //bodiesAppState.runCommand(new BodyLabelCommand("Patient", true));
        bodiesAppState.runCommand(
                new PlayBodyAnimationCommand("Patient", 
                BasicCharacterAnimControl.AnimName.DrinkStanding.name()));
        bodiesAppState.runCommand(
                new SayASentenceBodyCommand("Patient", "Hello, my name is Paul. Hello, my name is Paul."));
        
        bodiesAppState.createBody(BodiesAppState.BodyType.Elder, "Relative");
        bodiesAppState.setInSpace("Relative", "House1", "BedRoom2");
        bodiesAppState.runCommand(new GoCloseToBodyCommand("Relative", "Patient"));
        bodiesAppState.runCommand(new BodyLabelCommand("Relative", true));
        
        app.getCamera().setLocation(new Vector3f(9.692924f, 11.128746f, 4.5429335f));
        app.getCamera().setRotation(new Quaternion(0.37133554f, -0.6016627f, 0.37115145f, 0.60196227f));
	}
}