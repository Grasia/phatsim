/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.body.commands;

import com.jme3.app.Application;
import com.jme3.bullet.BulletAppState;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.logging.Level;
import phat.body.BodiesAppState;
import phat.body.control.physics.PHATCharacterControl;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.structures.houses.HouseAppState;
import phat.util.PhysicsUtils;
import phat.util.SpatialUtils;

/**
 *
 * @author pablo
 */
public class SetBodyHeightCommand extends PHATCommand {

    private String bodyId;
    private float height;

    public SetBodyHeightCommand(String bodyId, float height) {
        this(bodyId, height, null);
    }

    public SetBodyHeightCommand(String bodyId, float height, PHATCommandListener listener) {
        super(listener);
        this.bodyId = bodyId;
        this.height = height;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }

    @Override
    public void runCommand(Application app) {
        BodiesAppState bodiesAppState = app.getStateManager().getState(BodiesAppState.class);
        HouseAppState houseAppState = app.getStateManager().getState(HouseAppState.class);
        BulletAppState bulletAppState = app.getStateManager().getState(BulletAppState.class);

        Node body = bodiesAppState.getAvailableBodies().get(bodyId);

        if (body != null && body.getParent() != null) {
            Vector3f max = SpatialUtils.getMaxBounding(body);
            Vector3f min = SpatialUtils.getMinBounding(body);
            
            float cHeight = max.y-min.y;
            body.scale(height/cHeight);
            
            setState(PHATCommand.State.Success);
            return;
        }
        setState(PHATCommand.State.Fail);
    }

    @Override
    public void interruptCommand(Application app) {
        setState(PHATCommand.State.Fail);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + bodyId + ", height=" + height + ")";
    }
}