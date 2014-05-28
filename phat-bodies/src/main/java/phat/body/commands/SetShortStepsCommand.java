/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.body.commands;

import com.jme3.app.Application;
import com.jme3.scene.Node;
import java.util.logging.Level;
import phat.body.BodiesAppState;
import phat.body.control.parkinson.ShortStepsControl;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;

/**
 *
 * @author pablo
 */
public class SetShortStepsCommand  extends PHATCommand {

    private String bodyId;
    private Boolean on;

    public SetShortStepsCommand(String bodyId, Boolean on, PHATCommandListener listener) {
        super(listener);
        this.bodyId = bodyId;
        this.on = on;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }

    public SetShortStepsCommand(String bodyId, Boolean on) {
        this(bodyId, on, null);
    }

    @Override
    public void runCommand(Application app) {
        BodiesAppState bodiesAppState = app.getStateManager().getState(BodiesAppState.class);

        Node body = bodiesAppState.getAvailableBodies().get(bodyId);
        if (body != null) {
            if (on) {
                active(body);
            } else {
                desactive(body);
            }
        }
        setState(PHATCommand.State.Success);
    }

    @Override
    public void interruptCommand(Application app) {
        setState(PHATCommand.State.Fail);
    }

    private void active(Node body) {
        ShortStepsControl sc = body.getControl(ShortStepsControl.class);
        if (sc == null) {
            sc = new ShortStepsControl();
            body.addControl(sc);
        }
    }

    private void desactive(Node body) {
        ShortStepsControl sc = body.getControl(ShortStepsControl.class);
        if (sc != null) {
            body.removeControl(sc);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + bodyId + ", on=" + on + ")";
    }
}