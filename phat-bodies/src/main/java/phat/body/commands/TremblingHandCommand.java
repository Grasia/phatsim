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
import com.jme3.scene.Node;
import java.util.logging.Level;
import phat.body.BodiesAppState;
import phat.body.control.parkinson.LeftHandTremblingControl;
import phat.body.control.parkinson.RightHandTremblingControl;
import phat.commands.PHATCommParam;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandAnn;
import phat.commands.PHATCommandListener;

/**
 *
 * @author pablo
 */
@PHATCommandAnn(name="TremblingHand", type="body", debug = false)
public class TremblingHandCommand extends PHATCommand {

    private String bodyId;
    private Boolean on;
    private Boolean left;
    private Float minAngle;
    private Float maxAngle;
    private Float angular;

    public TremblingHandCommand() {
    }

    public TremblingHandCommand(String bodyId, Boolean on, Boolean left, PHATCommandListener listener) {
        super(listener);
        this.bodyId = bodyId;
        this.on = on;
        this.left = left;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }

    public TremblingHandCommand(String bodyId, Boolean on, Boolean left) {
        this(bodyId, on, left, null);
    }

    @Override
    public void runCommand(Application app) {
        BodiesAppState bodiesAppState = app.getStateManager().getState(BodiesAppState.class);

        Node body = bodiesAppState.getBody(bodyId);
        if (body != null) {
            if (on) {
                active(body);
            } else {
                desactive(body);
            }
        }
        setState(State.Success);
    }

    @Override
    public void interruptCommand(Application app) {
        setState(State.Fail);
    }

    private void active(Node body) {
        if (left) {
            LeftHandTremblingControl htc = body.getControl(LeftHandTremblingControl.class);
            if (htc == null) {
                htc = new LeftHandTremblingControl();
                body.addControl(htc);
            }
            if (minAngle != null) {
                htc.setMinAngle(minAngle);
            }
            if (maxAngle != null) {
                htc.setMaxAngle(maxAngle);
            }
            if (angular != null) {
                htc.setAngular(angular);
            }
        } else {
            RightHandTremblingControl htc = body.getControl(RightHandTremblingControl.class);
            if (htc == null) {
                htc = new RightHandTremblingControl();
                body.addControl(htc);
            }
            if (minAngle != null) {
                htc.setMinAngle(minAngle);
            }
            if (maxAngle != null) {
                htc.setMaxAngle(maxAngle);
            }
            if (angular != null) {
                htc.setAngular(angular);
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

    public Float getMinAngle() {
        return minAngle;
    }

    public void setMinAngle(Float minAngle) {
        this.minAngle = minAngle;
    }

    public Float getMaxAngle() {
        return maxAngle;
    }

    public void setMaxAngle(Float maxAngle) {
        this.maxAngle = maxAngle;
    }

    public Float getAngular() {
        return angular;
    }

    public void setAngular(Float angular) {
        this.angular = angular;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + bodyId + ", on=" + on + ", left=" + left + ")";
    }

    @PHATCommParam(mandatory=true, order=1)
    public void setBodyId(String bodyId) {
        this.bodyId = bodyId;
    }

    @PHATCommParam(mandatory=true, order=2)
    public void setOn(Boolean on) {
        this.on = on;
    }

    @PHATCommParam(mandatory=true, order=3)
    public void setLeft(Boolean left) {
        this.left = left;
    }
}
