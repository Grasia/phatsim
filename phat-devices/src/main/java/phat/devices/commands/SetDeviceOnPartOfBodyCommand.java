/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.devices.commands;

import phat.devices.commands.tests.*;
import com.jme3.animation.SkeletonControl;
import com.jme3.app.Application;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import java.util.logging.Level;

import phat.agents.actors.ActorFactory;
import phat.body.BodiesAppState;
import phat.body.control.navigation.AutonomousControlListener;
import phat.body.control.physics.PHATCharacterControl;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.commands.PHATCommand.State;
import phat.devices.DevicesAppState;
import phat.util.SpatialUtils;

/**
 *
 * @author pablo
 */
public class SetDeviceOnPartOfBodyCommand extends PHATDeviceCommand implements AutonomousControlListener {

    public enum PartOfBody {
        LeftHand, RightHand, LeftWrist, RightWrist, LeftUnkle, RightUnkle, Head, Chest
    }
    String bodyId;
    String deviceId;
    PartOfBody partOfBody;

    public SetDeviceOnPartOfBodyCommand(String bodyId, String deviceId, PartOfBody partOfBody, PHATCommandListener listener) {
        super(listener);
        this.bodyId = bodyId;
        this.deviceId = deviceId;
        this.partOfBody = partOfBody;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }

    public SetDeviceOnPartOfBodyCommand(String bodyId, String deviceId, PartOfBody partOfBody) {
        this(bodyId, deviceId, partOfBody, null);
    }

    @Override
    public void runCommand(Application app) {
        BodiesAppState bodiesAppState = app.getStateManager().getState(BodiesAppState.class);
        DevicesAppState devicesAppState = app.getStateManager().getState(DevicesAppState.class);

        Node body = bodiesAppState.getAvailableBodies().get(bodyId);

        if (body != null && body.getParent() != null) {
            Spatial s = devicesAppState.getDevice(deviceId);
            if (s != null) {
                if (attachDevice(body, s)) {
                    setState(State.Success);
                    return;
                }
            }
        }
        setState(State.Fail);
    }

    @Override
    public void interruptCommand(Application app) {
        setState(State.Fail);
    }

    private boolean attachDevice(Spatial body, Spatial obj) {
        RigidBodyControl rbc = obj.getControl(RigidBodyControl.class);
        if (rbc != null) {
            rbc.setEnabled(false);
        }
        SkeletonControl sc = ActorFactory.findControl(body, SkeletonControl.class);
        Node attachmentsNode = null;
        if (partOfBody == PartOfBody.LeftHand) {
            obj.setLocalTranslation(0, 0.05f, 0.02f);
            Quaternion q = new Quaternion();
            q.fromAngles(0f, 0f, 90f);
            obj.setLocalRotation(q);
            attachmentsNode = sc.getAttachmentsNode("LThumb");
        } else if (partOfBody == PartOfBody.RightHand) {
            obj.setLocalTranslation(0, 0.05f, 0.02f);
            Quaternion q = new Quaternion();
            q.fromAngles(0f, 0f, 90f);
            obj.setLocalRotation(q);
            attachmentsNode = sc.getAttachmentsNode("RThumb");
        } else if (partOfBody == PartOfBody.RightWrist) {
            obj.setLocalTranslation(0, 0.0f, 0.02f);
            attachmentsNode = sc.getAttachmentsNode("RightHand");
        } else if (partOfBody == PartOfBody.LeftWrist) {
            obj.setLocalTranslation(0, 0.0f, 0.02f);
            attachmentsNode = sc.getAttachmentsNode("LeftHand");
        } else if (partOfBody == PartOfBody.LeftWrist) {
            obj.setLocalTranslation(0, 0.0f, 0.02f);
            attachmentsNode = sc.getAttachmentsNode("LeftHand");
        } else if (partOfBody == PartOfBody.LeftUnkle) {
            obj.setLocalRotation(new Quaternion().fromAngles(0f, FastMath.HALF_PI, -FastMath.HALF_PI));
            obj.setLocalTranslation(0.04f, 0f, -0.1f);
            attachmentsNode = sc.getAttachmentsNode("LeftFoot");
        } else if (partOfBody == PartOfBody.RightUnkle) {
            obj.setLocalRotation(new Quaternion().fromAngles(0f, FastMath.HALF_PI, -FastMath.HALF_PI));
            obj.setLocalTranslation(-0.04f, 0f, -0.1f);
            attachmentsNode = sc.getAttachmentsNode("RightFoot");
        } else if (partOfBody == PartOfBody.Chest) {
            obj.setLocalTranslation(0f, 0.1f, 0.2f);
            attachmentsNode = sc.getAttachmentsNode("Spine1");
        }
        
        if (attachmentsNode != null) {
            attachmentsNode.attachChild(obj);
            return true;
        }

        return false;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + bodyId + ", " + deviceId + ", " + partOfBody.name() + ")";
    }

    @Override
    public void destinationReached(Vector3f destination) {
        setState(State.Success);
    }

    public String getBodyId() {
        return bodyId;
    }

    public String getEntityId() {
        return deviceId;
    }
}
