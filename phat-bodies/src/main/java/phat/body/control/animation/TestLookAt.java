/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.body.control.animation;

import phat.body.*;
import phat.body.commands.RotateTowardCommand;

import com.jme3.ai.steering.TestSteering;
import com.jme3.ai.steering.behaviour.Seek;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;

import phat.environment.*;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import java.util.logging.Logger;

import phat.app.PHATApplication;
import phat.app.PHATInitAppListener;
import phat.structures.houses.HouseFactory;
import phat.structures.houses.TestHouse;
import phat.structures.houses.commands.CreateHouseCommand;
import phat.util.SpatialFactory;
import phat.world.WorldAppState;

/**
 *
 * @author pablo
 */
public class TestLookAt implements PHATInitAppListener {

    private static final Logger logger = Logger.getLogger(TestHouse.class.getName());
    
    BodiesAppState bodiesAppState;
    
    public static void main(String[] args) {
    	TestLookAt test = new TestLookAt();
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
        
        bodiesAppState = new BodiesAppState();
        stateManager.attach(bodiesAppState);

        startTest(app);
        bodiesAppState.createBody(BodiesAppState.BodyType.Elder, "Relative");
        bodiesAppState.setInSpace("Relative", "House1", "BedRoom2");
        bodiesAppState.runCommand(new RotateTowardCommand("Relative", "o"));
	}
    
    private void startTest(SimpleApplication app) {
        Spatial s = SpatialFactory.createCube(Vector3f.UNIT_XYZ.mult(0.1f), ColorRGBA.Blue);
        s.setLocalTranslation(new Vector3f(1f, 2.0f, 1f));        
        s.setUserData("ID", "o");
        app.getRootNode().attachChild(s);
    }    
}