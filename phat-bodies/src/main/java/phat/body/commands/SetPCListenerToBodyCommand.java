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
import phat.audio.AudioAppState;
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
public class SetPCListenerToBodyCommand extends PHATCommand {

    private String bodyId;

    public SetPCListenerToBodyCommand(String bodyId, PHATCommandListener listener) {
        super(listener);
        this.bodyId = bodyId;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }

    public SetPCListenerToBodyCommand(String bodyId) {
        this(bodyId, null);
    }

    @Override
    public void runCommand(Application app) {
        BodiesAppState bodiesAppState = app.getStateManager().getState(BodiesAppState.class);
        AudioAppState audioAppState = app.getStateManager().getState(AudioAppState.class);

        if (audioAppState != null) {
            Node body = bodiesAppState.getAvailableBodies().get(bodyId);
            if (body != null && body.getParent() != null) {
                audioAppState.setPCSpeakerTo(body);
            }
        } else {
            setState(State.Fail);
            return;
        }
        setState(State.Success);
    }

    @Override
	public void interruptCommand(Application app) {
    	setState(State.Fail);
	}
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + bodyId + ")";
    }
}
