/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.body.commands;

import com.jme3.app.Application;
import com.jme3.audio.AudioNode;
import com.jme3.effect.ParticleEmitter;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import java.util.logging.Level;

import phat.body.BodiesAppState;
import phat.body.control.navigation.AutonomousControlListener;
import phat.body.control.navigation.navmesh.NavMeshMovementControl;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.commands.PHATCommand.State;
import phat.util.SpatialFactory;
import phat.util.SpatialUtils;

/**
 *
 * @author pablo
 */
public class OpenObjectCommand extends PHATCommand {

    float minDistanceToAction = 1.2f;
    String bodyId;
    String objectId;

    public OpenObjectCommand(String bodyId, String objectId, PHATCommandListener listener) {
        super(listener);
        this.bodyId = bodyId;
        this.objectId = objectId;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }

    public OpenObjectCommand(String bodyId, String objectId) {
        this(bodyId, objectId, null);
    }

    @Override
    public void runCommand(Application app) {
        BodiesAppState bodiesAppState = app.getStateManager().getState(BodiesAppState.class);

        Node body = bodiesAppState.getAvailableBodies().get(bodyId);

        if (body != null && body.getParent() != null) {
            Spatial object = SpatialUtils.getSpatialById(
                    SpatialFactory.getRootNode(), objectId);
            String role = object.getUserData("ROLE");
            if (role != null
                    && object.getWorldTranslation().distance(body.getWorldTranslation()) < minDistanceToAction) {
                if (role.equals("WC") || role.equals("Doorbell")) {
                    Spatial s = ((Node) object).getChild("AudioNode");
                    if (s != null && s instanceof AudioNode) {
                        AudioNode an = (AudioNode) s;
                        an.setLooping(false);
                        an.play();
                    }
                } else {
                    Spatial s = ((Node) object).getChild("AudioNode");
                    if (s != null && s instanceof AudioNode) {
                        AudioNode an = (AudioNode) s;
                        an.setLooping(true);
                        an.play();
                    }
                }
                ParticleEmitter emitter = (ParticleEmitter) ((Node) object).getChild("Emitter");
                if (emitter != null) {
                    emitter.setEnabled(true);
                    emitter.setCullHint(Spatial.CullHint.Inherit);
                }
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
        return getClass().getSimpleName() + "(" + bodyId + ", " + objectId + ")";
    }

    public String getBodyId() {
        return bodyId;
    }

    public String getEntityId() {
        return objectId;
    }
}
