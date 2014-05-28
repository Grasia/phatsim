/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.body.commands;

import com.jme3.app.Application;
import com.jme3.scene.Node;
import java.util.logging.Level;
import phat.body.BodiesAppState;
import phat.commands.PHATCommand;

/**
 *
 * @author pablo
 */
public class RemoveBodyFromSpaceCommand extends PHATCommand {

    private String bodyId;
    
    public RemoveBodyFromSpaceCommand(String bodyId) {
        super(null);
        this.bodyId = bodyId;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }
    
    @Override
    public void runCommand(Application app) {
        BodiesAppState bodiesAppState = app.getStateManager().getState(BodiesAppState.class);
        Node body = bodiesAppState.getAvailableBodies().get(bodyId);

        if (body != null && body.getParent() != null) {
            body.removeFromParent();
        }
        setState(State.Success);
    }
    
    @Override
	public void interruptCommand(Application app) {
    	setState(State.Fail);
	}
    
    @Override
    public String toString() {
        return getClass().getSimpleName()+"("+bodyId+")";
    }
}
