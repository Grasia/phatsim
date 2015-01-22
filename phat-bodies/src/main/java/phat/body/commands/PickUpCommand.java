/*
 * Copyright (C) 2014 Pablo Campillo-Sanchez <pabcampi@ucm.es>
 *
 * This software has been developed as part of the 
 * SociAAL project directed by Jorge J. Gomez Sanz
 * (http://grasia.fdi.ucm.es/sociaal)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
    
    float minDistance = 0.75f;
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

        Node body = bodiesAppState.getBody(bodyId);

        if (body != null && body.getParent() != null) {
            PHATCharacterControl cc = body.getControl(PHATCharacterControl.class);
            if (cc != null) {
                Node rootNode = SpatialUtils.getRootNode(body);
                Spatial s = SpatialUtils.getSpatialById(rootNode, entityId);
                if (s != null) {
                    if(SpatialUtils.getCenterBoinding(body).distance(SpatialUtils.getCenterBoinding(s)) < minDistance &&
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
        System.out.println("PICK_UP!!!!!");
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
