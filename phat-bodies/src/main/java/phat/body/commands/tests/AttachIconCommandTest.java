/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
import phat.body.commands.AttachIconCommand;
import phat.body.commands.RandomWalkingCommand;
import phat.body.commands.SetBodyInCoordenatesCommand;
import phat.structures.houses.TestHouse;
import phat.util.SpatialFactory;
import phat.world.WorldAppState;

/**
 *
 * @author pablo
 */
public class AttachIconCommandTest implements PHATInitAppListener {

    private static final Logger logger = Logger.getLogger(TestHouse.class.getName());
    BodiesAppState bodiesAppState;
    WorldAppState worldAppState;

    public static void main(String[] args) {
        AttachIconCommandTest test = new AttachIconCommandTest();
        PHATApplication phat = new PHATApplication(test);
        phat.setDisplayFps(true);
        phat.setDisplayStatView(false);
        phat.start();
    }

    @Override
    public void init(SimpleApplication app) {
        SpatialFactory.init(app.getAssetManager(), app.getRootNode());

        AppStateManager stateManager = app.getStateManager();

        app.getFlyByCamera().setMoveSpeed(10f);

        app.getCamera().setLocation(new Vector3f(4.034334f, 3.8802402f, 6.621415f));
        app.getCamera().setRotation(new Quaternion(-7.4161455E-4f, 0.97616464f, -0.21700443f, -0.0033340578f));

        app.getFlyByCamera().setDragToRotate(true);

        BulletAppState bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        //bulletAppState.setDebugEnabled(true);

        worldAppState = new WorldAppState();
        app.getStateManager().attach(worldAppState);
        worldAppState.setCalendar(2013, 1, 1, 12, 0, 0);
        worldAppState.setLandType(WorldAppState.LandType.Basic);

        bodiesAppState = new BodiesAppState();
        stateManager.attach(bodiesAppState);

        String bodyId = "P";
        bodiesAppState.createBody(BodiesAppState.BodyType.ElderLP, bodyId);
        bodiesAppState.runCommand(new SetBodyInCoordenatesCommand(bodyId, Vector3f.ZERO.add(0f, 0f, 0f)));
        bodiesAppState.runCommand(new RandomWalkingCommand(bodyId, true));
        bodiesAppState.runCommand(new AttachIconCommand(bodyId, "Textures/SociaalmlImages/behaviour/tasks/PutOn.png", true));
    }
}