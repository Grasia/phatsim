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
import phat.body.commands.AlignWithCommand;
import phat.body.commands.GoCloseToObjectCommand;
import phat.body.commands.GoToSpaceCommand;
import phat.body.commands.OpenObjectCommand;
import phat.body.commands.SetPCListenerToBodyCommand;
import phat.body.commands.SitDownCommand;
import phat.body.commands.StandUpCommand;
import phat.body.commands.TremblingHandCommand;
import phat.body.commands.TremblingHeadCommand;
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
public class UseShowerTest implements PHATInitAppListener {

    private static final Logger logger = Logger.getLogger(TestHouse.class.getName());
    BodiesAppState bodiesAppState;
    SpatialEnvironmentAPI seAPI;

    public static void main(String[] args) {
        UseShowerTest test = new UseShowerTest();
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
        seAPI.getWorldAppState().setLandType(WorldAppState.LandType.TwoHouses);
        seAPI.getHouseAppState().runCommand(new CreateHouseCommand("House1", HouseFactory.HouseType.House3room2bath));
        

        bodiesAppState = new BodiesAppState();
        stateManager.attach(bodiesAppState);

        bodiesAppState.createBody(BodiesAppState.BodyType.Elder, "Patient");
        bodiesAppState.setInSpace("Patient", "House1", "BedRoom1RightSide");
        //haveAShower();
        //sitDown("WC1");
        //goToUse("Sink");
        bodiesAppState.runCommand(new GoToSpaceCommand("Patient", "Hall"));
        bodiesAppState.runCommand(new TremblingHeadCommand("Patient", true));
        bodiesAppState.runCommand(new TremblingHandCommand("Patient", true, true));
        bodiesAppState.runCommand(new TremblingHandCommand("Patient", true, false));
        //bodiesAppState.runCommand(new SetCameraToBodyCommand("Patient"));
        bodiesAppState.runCommand(new SetPCListenerToBodyCommand("Patient"));
        //bodiesAppState.runCommand(new BodyLabelCommand("Patient", true));
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
    private void finishUseWC() {
        bodiesAppState.runCommand(new StandUpCommand("Patient"));
        bodiesAppState.runCommand(new OpenObjectCommand("Patient", "WC1"));        
    }

    /*private void haveAShower() {
        GoToCommand gtc = new GoToCommand("Patient", new Lazy<Vector3f>() {
            @Override
            public Vector3f getLazy() {
                Spatial targetSpatial = SpatialUtils.getSpatialById(
                        SpatialFactory.getRootNode(), "Shower1");
                return targetSpatial.getWorldTranslation();
            }
        }, new PHATCommandListener() {
            @Override
            public void commandStateChanged(PHATCommand command) {
                if (command.getState() == PHATCommand.State.Success) {
                    bodiesAppState.runCommand(new OpenObjectCommand("Patient", "Shower1"));
                    bodiesAppState.runCommand(new PlayBodyAnimationCommand("Patient", 
                            BasicCharacterAnimControl.AnimName.ScratchArm.name()));
                }
            }
        });
        //bodiesAppState.runCommand(new CloseObjectCommand("Patient", "Basin1"));
        gtc.setMinDistance(0.05f);
        bodiesAppState.runCommand(gtc);
    }*/
}