/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.body.commands;

import com.jme3.app.Application;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.PhysicsControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import java.util.logging.Level;

import phat.body.BodiesAppState;
import phat.body.control.physics.PHATCharacterControl;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.structures.houses.HouseAppState;
import phat.util.PhysicsUtils;

/**
 *
 * @author pablo
 */
public class SetBodyInCoordenatesCommand extends PHATCommand {

    private String bodyId;
    private Vector3f location;

    public SetBodyInCoordenatesCommand(String bodyId, Vector3f location) {
        this(bodyId, location, null);
    }

    public SetBodyInCoordenatesCommand(String bodyId, Vector3f location, PHATCommandListener listener) {
        super(listener);
        this.bodyId = bodyId;
        this.location = location;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }

    @Override
    public void runCommand(Application app) {
        BodiesAppState bodiesAppState = app.getStateManager().getState(BodiesAppState.class);
        HouseAppState houseAppState = app.getStateManager().getState(HouseAppState.class);
        BulletAppState bulletAppState = app.getStateManager().getState(BulletAppState.class);

        Node body = bodiesAppState.getAvailableBodies().get(bodyId);

        if (body != null && body.getParent() == null) {
            PHATCharacterControl cc = body.getControl(PHATCharacterControl.class);
            bodiesAppState.getBodiesNode().attachChild(body);

            PhysicsUtils.addAllPhysicsControls(body, bulletAppState);
            //bulletAppState.getPhysicsSpace().addAll(body);

            if(cc != null)
                cc.warp(location);
            else
                body.setLocalTranslation(location);
            setState(State.Success);
            return;
        }
        setState(State.Fail);
    }

    @Override
    public void interruptCommand(Application app) {
        setState(State.Fail);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + bodyId + ", " + location + ")";
    }
}
