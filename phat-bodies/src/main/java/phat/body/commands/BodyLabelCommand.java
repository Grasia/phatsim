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

        Node body = bodiesAppState.getBody(bodyId);
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
