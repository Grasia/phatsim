/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.body.commands;

import com.jme3.animation.SkeletonControl;
import com.jme3.app.Application;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import java.util.logging.Level;

import phat.agents.actors.ActorFactory;
import phat.body.BodiesAppState;
import phat.body.control.navigation.AutonomousControlListener;
import phat.body.control.navigation.navmesh.NavMeshMovementControl;
import phat.body.control.physics.PHATCharacterControl;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.commands.PHATCommand.State;
import phat.util.SpatialUtils;

/**
 *
 * @author pablo
 */
public class PickUpCommand extends PHATCommand implements AutonomousControlListener {

    public enum Hand {Left, Right}
    
    float minDistance = 0.5f;
    String bodyId;
    String entityId;
    Hand hand;
    
    public PickUpCommand(String bodyId, String entityId, Hand hand, PHATCommandListener listener) {
        super(listener);
        this.bodyId = bodyId;
        this.entityId = entityId;
        this.hand = hand;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }

    public PickUpCommand(String bodyId, String entityId, Hand hand) {
        this(bodyId, entityId, hand, null);
    }

    @Override
    public void runCommand(Application app) {
        BodiesAppState bodiesAppState = app.getStateManager().getState(BodiesAppState.class);

        Node body = bodiesAppState.getAvailableBodies().get(bodyId);

        if (body != null && body.getParent() != null) {
            PHATCharacterControl cc = body.getControl(PHATCharacterControl.class);
            if (cc != null) {
                Node rootNode = SpatialUtils.getRootNode(body);
                Spatial s = SpatialUtils.getSpatialById(rootNode, entityId);
                if (s != null) {
                    if(body.getWorldTranslation().distance(s.getWorldTranslation()) < minDistance &&
                            pickUp(body, s)) {
                        setState(State.Success);
                        return;
                    }
                }
            }
        }
        setState(State.Fail);
    }

    @Override
	public void interruptCommand(Application app) {
		setState(State.Fail);
	}
    
    private boolean pickUp(Spatial body, Spatial obj) {
        RigidBodyControl rbc = ActorFactory.findControl(obj, RigidBodyControl.class);
        if (rbc != null) {
            rbc.setEnabled(false);
        }
        obj.setLocalTranslation(0, 0.05f, 0.02f);
        Quaternion q = new Quaternion();
        q.fromAngles(0f, 0f, 90f);
        obj.setLocalRotation(q);

        SkeletonControl sc = ActorFactory.findControl(body, SkeletonControl.class);
        Node attachmentsNode;
        if (hand == Hand.Left) {
            attachmentsNode = sc.getAttachmentsNode("LThumb");
        } else {
            attachmentsNode = sc.getAttachmentsNode("RThumb");
        }
        attachmentsNode.attachChild(obj);

        return true;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + bodyId + ", " + entityId + ")";
    }

    @Override
    public void destinationReached(Vector3f destination) {
        setState(State.Success);
    }

    public float getMinDistance() {
        return minDistance;
    }

    public void setMinDistance(float minDistance) {
        this.minDistance = minDistance;
    }

    public String getBodyId() {
        return bodyId;
    }

    public String getEntityId() {
        return entityId;
    }

    public Hand getHand() {
        return hand;
    }
}
