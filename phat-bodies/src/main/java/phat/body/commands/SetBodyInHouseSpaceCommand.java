/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.body.commands;

import com.jme3.app.Application;
import com.jme3.bullet.BulletAppState;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import java.util.logging.Level;

import phat.body.BodiesAppState;
import phat.body.control.physics.PHATCharacterControl;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.structures.houses.House;
import phat.structures.houses.HouseAppState;
import phat.util.PhysicsUtils;
import phat.util.SpatialUtils;

/**
 *
 * @author pablo
 */
public class SetBodyInHouseSpaceCommand extends PHATCommand {

    private String bodyId;
    private String houseId;
    private String spaceId;

    public SetBodyInHouseSpaceCommand(String bodyId, String houseId, String spaceId) {
        this(bodyId, houseId, spaceId, null);
    }

    public SetBodyInHouseSpaceCommand(String bodyId, String houseId, String spaceId, PHATCommandListener listener) {
        super(listener);
        this.bodyId = bodyId;
        this.houseId = houseId;
        this.spaceId = spaceId;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }

    @Override
    public void runCommand(Application app) {
        BodiesAppState bodiesAppState = app.getStateManager().getState(BodiesAppState.class);
        HouseAppState houseAppState = app.getStateManager().getState(HouseAppState.class);
        BulletAppState bulletAppState = app.getStateManager().getState(BulletAppState.class);

        Node body = bodiesAppState.getAvailableBodies().get(bodyId);

        if (body != null && body.getParent() == null) {
            Vector3f loc = null;
            House house = houseAppState.getHouse(houseId);
            if (houseAppState != null && house != null) {
                loc = house.getCoordenatesOfSpaceById(spaceId);

                Spatial spatial = SpatialUtils.getSpatialById(house.getRootNode(), spaceId);
                if (spatial != null) {
                    System.out.println("spatial = " + spatial.getName());
                    loc = spatial.getWorldTranslation();
                }
            }

            if (loc != null) {
                PHATCharacterControl cc = body.getControl(PHATCharacterControl.class);
                bodiesAppState.getBodiesNode().attachChild(body);

                PhysicsUtils.addAllPhysicsControls(body, bulletAppState);
                //bulletAppState.getPhysicsSpace().addAll(body);

                cc.warp(loc);
                setState(State.Success);
                return;
            }
        }
        setState(State.Fail);
    }

    @Override
    public void interruptCommand(Application app) {
        setState(State.Fail);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + bodyId + ", " + spaceId + ")";
    }
}
