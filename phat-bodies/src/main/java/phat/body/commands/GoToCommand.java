/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.body.commands;

import com.jme3.app.Application;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

import java.util.logging.Level;

import phat.body.BodiesAppState;
import phat.body.control.navigation.AutonomousControlListener;
import phat.body.control.navigation.navmesh.NavMeshMovementControl;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.commands.PHATCommand.State;
import phat.structures.houses.HouseAppState;
import phat.util.Lazy;

/**
 *
 * @author pablo
 */
public class GoToCommand extends PHATCommand implements AutonomousControlListener {

    private String bodyId;
    private Lazy<Vector3f> destiny;
    private float minDistance;
    
    public GoToCommand(String bodyId, Lazy<Vector3f> destiny, PHATCommandListener listener) {
        super(listener);
        this.bodyId = bodyId;
        this.destiny = destiny;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }
    
    public GoToCommand(String bodyId, Lazy<Vector3f> destiny) {
        this(bodyId, destiny, null);
    }
    
    @Override
    public void runCommand(Application app) {
        BodiesAppState bodiesAppState = app.getStateManager().getState(BodiesAppState.class);
        
        Node body = bodiesAppState.getAvailableBodies().get(bodyId);

        if (body != null && body.getParent() != null) {
                NavMeshMovementControl nmmc = body.getControl(NavMeshMovementControl.class);
                if(nmmc != null) {
                    nmmc.setMinDistance(minDistance);
                    boolean reachable = nmmc.moveTo(destiny.getLazy());
                    if(reachable) {
                        nmmc.setListener(this);
                        return;
                    }
            }
        }
        setState(State.Fail);
    }
    
    @Override
	public void interruptCommand(Application app) {
		BodiesAppState bodiesAppState = app.getStateManager().getState(
				BodiesAppState.class);

		Node body = bodiesAppState.getAvailableBodies().get(bodyId);

		if (body != null && body.getParent() != null) {
			NavMeshMovementControl nmmc = body
					.getControl(NavMeshMovementControl.class);
			nmmc.moveTo(null);
			setState(State.Interrupted);
			return;
		}
		setState(State.Fail);
	}
    
    @Override
    public String toString() {
        return getClass().getSimpleName()+"("+bodyId+", "+destiny.getLazy()+")";
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
}
