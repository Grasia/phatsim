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

import com.jme3.animation.Bone;
import com.jme3.animation.SkeletonControl;
import com.jme3.app.Application;
import com.jme3.scene.Node;
import java.util.logging.Level;
import phat.body.BodiesAppState;
import phat.body.control.parkinson.LeftHandTremblingControl;
import phat.body.control.parkinson.RightHandTremblingControl;
import phat.body.control.parkinson.RigidLeftArmControl;
import phat.body.control.parkinson.RigidRightArmControl;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;

/**
 *
 * @author pablo
 */
public class SetRigidArmCommand extends PHATCommand {

    private String bodyId;
    private Boolean on;
    private Boolean left;

    public SetRigidArmCommand(String bodyId, Boolean on, Boolean left, PHATCommandListener listener) {
        super(listener);
        this.bodyId = bodyId;
        this.on = on;
        this.left = left;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }

    public SetRigidArmCommand(String bodyId, Boolean on, Boolean left) {
        this(bodyId, on, left, null);
    }

    @Override
    public void runCommand(Application app) {
        BodiesAppState bodiesAppState = app.getStateManager().getState(BodiesAppState.class);

        Node body = bodiesAppState.getAvailableBodies().get(bodyId);
        if (body != null) {
            if (on) {
                active(body);
            } else {
                desactive(body);
            }
        }
        setState(PHATCommand.State.Success);
    }

    private void setUserControlFrom(Bone bone, boolean userControl) {
        bone.setUserControl(userControl);
        for (Bone b : bone.getChildren()) {
            setUserControlFrom(b, userControl);
        }
    }

    @Override
    public void interruptCommand(Application app) {
        setState(PHATCommand.State.Fail);
    }

    private void active(Node body) {
        if (left) {
            RigidLeftArmControl htc = body.getControl(RigidLeftArmControl.class);
            if (htc == null) {
                htc = new RigidLeftArmControl();
                body.addControl(htc);
            }
        } else {
            RigidRightArmControl htc = body.getControl(RigidRightArmControl.class);
            if (htc == null) {
                htc = new RigidRightArmControl();
                body.addControl(htc);
            }
        }
    }

    private void desactive(Node body) {
        if (left) {
            LeftHandTremblingControl lhtc = body.getControl(LeftHandTremblingControl.class);
            if (lhtc != null) {
                body.removeControl(lhtc);
            }
        } else {
            RightHandTremblingControl lhtc = body.getControl(RightHandTremblingControl.class);
            if (lhtc != null) {
                body.removeControl(lhtc);
            }
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + bodyId + ", on=" + on + ", left=" + left + ")";
    }
}