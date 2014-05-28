/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.body.commands;

import com.jme3.app.Application;
import com.jme3.bullet.BulletAppState;
import com.jme3.scene.Node;

import java.util.logging.Level;

import phat.body.BodiesAppState;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.structures.houses.HouseAppState;

/**
 *
 * @author pablo
 */
public class SetSpeedDisplacemenetCommand extends PHATCommand {

    private String bodyId;
    private float speed;

    public SetSpeedDisplacemenetCommand(String bodyId, float speed) {
        this(bodyId, speed, null);
    }

    public SetSpeedDisplacemenetCommand(String bodyId, float speed, PHATCommandListener listener) {
        super(listener);
        this.bodyId = bodyId;
        this.speed = speed;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }

    @Override
    public void runCommand(Application app) {
        BodiesAppState bodiesAppState = app.getStateManager().getState(BodiesAppState.class);

        Node body = bodiesAppState.getAvailableBodies().get(bodyId);

        if (body != null && body.getParent() != null) {
            body.setUserData("Speed", speed);
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
        return getClass().getSimpleName() + "(" + bodyId + ", speed=" + speed + ")";
    }
}
