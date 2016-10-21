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
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import java.util.logging.Level;

import phat.body.BodiesAppState;
import phat.body.control.navigation.AutonomousControlListener;
import phat.body.control.physics.PHATCharacterControl;
import phat.commands.PHATCommParam;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.commands.PHATCommand.State;
import phat.commands.PHATCommandAnn;
import phat.util.SpatialUtils;

/**
 *
 * @author pablo
 */
@PHATCommandAnn(name="RotateToward", type="body", debug = false)
public class RotateTowardCommand extends PHATCommand implements AutonomousControlListener {

    private String bodyId;
    private String entityId;
    private boolean oposite = false;

    public RotateTowardCommand() {
    }

    public RotateTowardCommand(String bodyId, String entityId, PHATCommandListener listener) {
        super(listener);
        this.bodyId = bodyId;
        this.entityId = entityId;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }

    public RotateTowardCommand(String bodyId, String entityId) {
        this(bodyId, entityId, null);
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
                    Vector3f dir = s.getWorldTranslation().subtract(cc.getLocation());
                    if(oposite) {
                        dir.negateLocal();
                    }
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

        Node body = bodiesAppState.getBody(bodyId);

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
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + bodyId + ", " + entityId + ")";
    }

    @Override
    public void destinationReached(Vector3f destination) {
        setState(State.Success);
    }

    @PHATCommParam(mandatory=true, order=1)
    public void setBodyId(String bodyId) {
        this.bodyId = bodyId;
    }

    @PHATCommParam(mandatory=true, order=2)
    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    @PHATCommParam(mandatory=false, order=3)
    public void setOposite(boolean oposite) {
        this.oposite = oposite;
    }
    
}
