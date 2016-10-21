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
import phat.body.control.parkinson.ShortStepsControl;
import phat.commands.PHATCommParam;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandAnn;
import phat.commands.PHATCommandListener;

/**
 *
 * @author pablo
 */
@PHATCommandAnn(name="SetShortSteps", type="body", debug = false)
public class SetShortStepsCommand  extends PHATCommand {

    private String bodyId;
    private Boolean on;

    public SetShortStepsCommand() {
    }

    public SetShortStepsCommand(String bodyId, Boolean on, PHATCommandListener listener) {
        super(listener);
        this.bodyId = bodyId;
        this.on = on;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }

    public SetShortStepsCommand(String bodyId, Boolean on) {
        this(bodyId, on, null);
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
        setState(PHATCommand.State.Success);
    }

    @Override
    public void interruptCommand(Application app) {
        setState(PHATCommand.State.Fail);
    }

    private void active(Node body) {
        ShortStepsControl sc = body.getControl(ShortStepsControl.class);
        if (sc == null) {
            sc = new ShortStepsControl();
            body.addControl(sc);
        }
    }

    private void desactive(Node body) {
        ShortStepsControl sc = body.getControl(ShortStepsControl.class);
        if (sc != null) {
            body.removeControl(sc);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + bodyId + ", on=" + on + ")";
    }

    @PHATCommParam(mandatory=true, order=1)
    public void setBodyId(String bodyId) {
        this.bodyId = bodyId;
    }

    @PHATCommParam(mandatory=true, order=2)
    public void setOn(Boolean on) {
        this.on = on;
    }
}