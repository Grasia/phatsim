/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.body.commands;

import com.jme3.app.Application;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.font.BitmapText;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import java.util.logging.Level;

import phat.body.BodiesAppState;
import phat.body.control.navigation.navmesh.NavMeshMovementControl;
import phat.body.control.physics.PHATCharacterControl;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.commands.PHATCommand.State;
import phat.structures.houses.HouseAppState;
import phat.util.SpatialFactory;

/**
 *
 * @author pablo
 */
public class BodyLabelCommand extends PHATCommand {

    private String bodyId;
    private Boolean show;

    public BodyLabelCommand(String bodyId, Boolean show, PHATCommandListener listener) {
        super(listener);
        this.bodyId = bodyId;
        this.show = show;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }

    public BodyLabelCommand(String bodyId, Boolean show) {
        this(bodyId, show, null);
    }

    @Override
    public void runCommand(Application app) {
        BodiesAppState bodiesAppState = app.getStateManager().getState(BodiesAppState.class);

        Node body = bodiesAppState.getAvailableBodies().get(bodyId);
        BitmapText bitmapText = getName(body);
        
        if (show && bitmapText == null) {
            Node showedName = SpatialFactory.attachAName(body);
            showedName.setLocalTranslation(0f, 2f, 0f);
        } else if (!show && bitmapText != null) {
            bitmapText.removeFromParent();
        }
        setState(State.Success);
    }

    @Override
	public void interruptCommand(Application app) {
		show = false;
		runCommand(app);
	}
    
    private BitmapText getName(Node body) {
        for (Spatial s : body.getChildren()) {
            if (s instanceof BitmapText) {
                if (((BitmapText) s).getText().equals(body.getName())) {
                    return ((BitmapText) s);
                }
            }
        }
        return null;
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + bodyId + ",show=" + show + ")";
    }
}
