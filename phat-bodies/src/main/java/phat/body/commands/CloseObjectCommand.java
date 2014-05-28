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
import phat.body.control.physics.PHATCharacterControl;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.commands.PHATCommand.State;
import phat.util.SpatialFactory;
import phat.util.SpatialUtils;

/**
 *
 * @author pablo
 */
public class CloseObjectCommand extends PHATCommand {

    float minDistanceToAction = 1f;
    String bodyId;
    String objectId;

    public CloseObjectCommand(String bodyId, String objectId, PHATCommandListener listener) {
        super(listener);
        this.bodyId = bodyId;
        this.objectId = objectId;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }

    public CloseObjectCommand(String bodyId, String entityId) {
        this(bodyId, entityId, null);
    }

    @Override
    public void runCommand(Application app) {
        BodiesAppState bodiesAppState = app.getStateManager().getState(BodiesAppState.class);

        Node body = bodiesAppState.getAvailableBodies().get(bodyId);

        if (body != null && body.getParent() != null) {
            Spatial object = SpatialUtils.getSpatialById(
                    SpatialFactory.getRootNode(), objectId);
            String rol = object.getUserData("ROLE");
            if (rol != null
                    && object.getWorldTranslation().distance(body.getWorldTranslation()) < minDistanceToAction) {
                ParticleEmitter emitter = (ParticleEmitter) ((Node) object).getChild("Emitter");
                if (emitter != null) {
                    emitter.setEnabled(false);
                    emitter.setCullHint(Spatial.CullHint.Always);
                }
                Spatial s = ((Node) object).getChild("AudioNode");
                if (s != null && s instanceof AudioNode) {
                    AudioNode an = (AudioNode) s;
                    an.stop();
                }
                setState(State.Success);
                return;
            }
        }
        setState(State.Fail);
    }
    
    @Override
	public void interruptCommand(Application app) {
    	setState(State.Interrupted);
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
