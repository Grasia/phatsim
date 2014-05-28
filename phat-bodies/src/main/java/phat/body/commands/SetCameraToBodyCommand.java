/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.body.commands;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
import com.jme3.scene.control.CameraControl.ControlDirection;
import java.util.logging.Level;
import phat.body.BodiesAppState;
import phat.body.control.navigation.navmesh.NavMeshMovementControl;
import phat.body.control.physics.PHATCharacterControl;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.structures.houses.HouseAppState;

/**
 *
 * @author pablo
 */
public class SetCameraToBodyCommand extends PHATCommand {

    private String bodyId;
    CameraNode camNode;
    float distance = 2f;
    float height = 2f;

    public SetCameraToBodyCommand(String bodyId, PHATCommandListener listener) {
        super(listener);
        this.bodyId = bodyId;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }

    public SetCameraToBodyCommand(String bodyId) {
        this(bodyId, null);
    }

    @Override
    public void runCommand(Application app) {
        BodiesAppState bodiesAppState = app.getStateManager().getState(BodiesAppState.class);

        Node body = bodiesAppState.getAvailableBodies().get(bodyId);
        if (app instanceof SimpleApplication) {
            ((SimpleApplication) app).getFlyByCamera().setEnabled(false);
        }
        if (body != null && body.getParent() != null) {
            camNode = new CameraNode("CamNode", app.getCamera());
            camNode.setControlDir(ControlDirection.SpatialToCamera);
            camNode.setLocalTranslation(new Vector3f(0, height, distance));
            camNode.lookAt(body.getLocalTranslation().add(Vector3f.UNIT_Y), Vector3f.UNIT_Y);
            body.attachChild(camNode);
            setState(State.Success);
            return;
        }
        setState(State.Fail);
    }

    @Override
    public void interruptCommand(Application app) {
        BodiesAppState bodiesAppState = app.getStateManager().getState(BodiesAppState.class);

        Node body = bodiesAppState.getAvailableBodies().get(bodyId);
        if (body != null && body.getParent() != null) {
            camNode.removeFromParent();
            setState(State.Interrupted);
            return;
        }
        setState(State.Fail);
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public boolean isFront() {
        return distance > 0;
    }

    public void setFront(boolean front) {
        if((front && distance < 0) || (!front && distance > 0)) {
            distance *= -1;
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + bodyId + ",distance="+Math.abs(distance)+",height="+height+",front="+isFront()+")";
    }
}
