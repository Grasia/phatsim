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

import com.jme3.animation.SkeletonControl;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.CameraControl.ControlDirection;
import java.util.logging.Level;
import phat.audio.AudioAppState;
import phat.body.BodiesAppState;
import phat.body.control.navigation.navmesh.NavMeshMovementControl;
import phat.body.control.physics.PHATCharacterControl;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.structures.houses.HouseAppState;
import phat.util.SpatialUtils;

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
            Node body = bodiesAppState.getBody(bodyId);
            SkeletonControl skeletonControl = body.getControl(SkeletonControl.class);
            Node head = skeletonControl.getAttachmentsNode("Head");
            if (head != null && body.getParent() != null) {
                audioAppState.setPCSpeakerTo(head);
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
