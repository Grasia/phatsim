/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.body.commands;

import com.jme3.app.Application;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import java.util.logging.Level;
import phat.body.BodiesAppState;
import phat.body.control.navigation.AutonomousControlListener;
import phat.body.control.physics.PHATCharacterControl;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.structures.houses.HouseAppState;
import phat.util.PHATSceneGraphVisitor;
import phat.util.SpatialUtils;

/**
 *
 * @author pablo
 */
public class AlignWithCommand extends PHATCommand implements AutonomousControlListener {

    private String bodyId;
    private String entityId;
    private boolean oposite = false;

    public AlignWithCommand(String bodyId, String entityId, PHATCommandListener listener) {
        super(listener);
        this.bodyId = bodyId;
        this.entityId = entityId;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }

    public AlignWithCommand(String bodyId, String entityId) {
        this(bodyId, entityId, null);
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
                    Vector3f dir = s.getLocalRotation().mult(Vector3f.UNIT_Z);
                    cc.setViewDirection(dir);
                    setState(State.Success);
                    return;
                }
            }
        }
        setState(State.Fail);
    }
    
    @Override
	public void interruptCommand(Application app) {
		BodiesAppState bodiesAppState = app.getStateManager().getState(BodiesAppState.class);

        Node body = bodiesAppState.getAvailableBodies().get(bodyId);

        if (body != null && body.getParent() != null) {
            PHATCharacterControl cc = body.getControl(PHATCharacterControl.class);
            if (cc != null) {
                cc.setViewDirection(body.getWorldRotation().mult(Vector3f.UNIT_Z));
                setState(State.Interrupted);
                return;
            }
        }
        setState(State.Fail);
	}

    public boolean isOposite() {
        return oposite;
    }

    public void setOposite(boolean oposite) {
        this.oposite = oposite;
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + bodyId + ", " + entityId + ")";
    }

    @Override
    public void destinationReached(Vector3f destination) {
        setState(State.Success);
    }
}
