/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.body.commands;

import com.jme3.app.Application;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import java.util.logging.Level;
import phat.commands.PHATCommParam;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandAnn;
import phat.commands.PHATCommandListener;
import phat.util.PhysicsUtils;
import phat.util.SpatialFactory;
import phat.util.SpatialUtils;

/**
 *
 * @author pablo
 */
@PHATCommandAnn(name = "DropObj", type = "body", debug = false)
public class DropObjCommand extends PHATCommand {

    private String objectId;

    public DropObjCommand() {
    }

    public DropObjCommand(String objectId) {
        this(objectId, null);
    }

    public DropObjCommand(String objectId, PHATCommandListener listener) {
        super(listener);
        this.objectId = objectId;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }

    @Override
    public void runCommand(Application app) {
        BulletAppState bulletAppState = app.getStateManager().getState(BulletAppState.class);
        Spatial object = SpatialUtils.getSpatialById(SpatialFactory.getRootNode(), objectId);

        if (object != null) {
            Vector3f loc = object.getWorldTranslation();
            
            object.setLocalTranslation(Vector3f.ZERO);
            object.getControl(RigidBodyControl.class).setEnabled(true);
            object.getControl(RigidBodyControl.class).setPhysicsLocation(loc);
            
            PhysicsUtils.setHighPhysicsPrecision(object);
            bulletAppState.getPhysicsSpace().addAll(object);

            SpatialFactory.getRootNode().attachChild(object);
            //device.getControl(RigidBodyControl.class).setPhysicsLocation(places.get);
            setState(State.Success);
            return;
        }
        setState(State.Fail);
    }

    @Override
    public void interruptCommand(Application app) {
        setState(State.Interrupted);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + objectId + ")";
    }

    @PHATCommParam(mandatory = true, order = 1)
    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }
}
