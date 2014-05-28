package phat.body.commands;

import com.jme3.app.Application;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.AbstractPhysicsControl;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;

import java.util.logging.Level;

import phat.body.BodiesAppState;
import phat.body.control.navigation.StraightMovementControl;
import phat.body.control.physics.ragdoll.SimulateTripOver;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.commands.PHATCommand.State;

/**
 *
 * @author pablo
 */
public class TripOverCommand extends PHATCommand {

    private String bodyId;

    public TripOverCommand(String bodyId, PHATCommandListener listener) {
        super(listener);
        this.bodyId = bodyId;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }

    public TripOverCommand(String bodyId) {
        this(bodyId, null);
    }

    @Override
    public void runCommand(Application app) {
        BodiesAppState bodiesAppState = app.getStateManager().getState(BodiesAppState.class);
        BulletAppState bulletAppState = app.getStateManager().getState(BulletAppState.class);

        Node body = bodiesAppState.getAvailableBodies().get(bodyId);

        if (body != null && body.getParent() != null) {
            printControls(body);
            SimulateTripOver sto = body.getControl(SimulateTripOver.class);
            if(sto == null) {
                System.out.println("SimulateTripOver");
                sto = new SimulateTripOver();          
                body.addControl(sto);
            }
            
            StraightMovementControl smc = body.getControl(StraightMovementControl.class);
            if (smc != null) {
                System.out.println("StraightMovementControl stopped!!");
                body.removeControl(smc);
            }
            
            sto.activate();

            printControls(body);

            //SimulateTripOver sto = new SimulateTripOver(body);
            //sto.activate();
            //krc.setEnabled(true);
            //PhysicsUtils.setHighPhysicsPrecision(app.getStateManager().getState(HouseAppState.class).getHouse().getRootNode());
            setState(State.Success);
            return;
        }
        setState(State.Fail);
    }

    public void printControls(Spatial spatial) {
        for (int i = 0; i < spatial.getNumControls(); i++) {
            Control c = spatial.getControl(i);
            System.out.print("\t-" + c.getClass().getSimpleName());
            if (c instanceof AbstractControl) {
                System.out.println(" " + ((AbstractControl) c).isEnabled());
            } else if (c instanceof AbstractPhysicsControl) {
                System.out.println(" " + ((AbstractPhysicsControl) c).isEnabled());
            } else {
                System.out.println("");
            }
        }
        System.out.println("");
    }

    @Override
    public void interruptCommand(Application app) {
        setState(State.Fail);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + bodyId + ")";
    }
}
