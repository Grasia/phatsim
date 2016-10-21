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
import phat.body.sensing.vision.AttachLabelToVisibleObjectsControl;
import phat.body.sensing.vision.VisionControl;
import phat.commands.PHATCommParam;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandAnn;
import phat.commands.PHATCommandListener;

/**
 *
 * @author pablo
 */
@PHATCommandAnn(name="ShowLabelsOfVisibleObjects", type="body", debug = true)
public class ShowLabelsOfVisibleObjectsCommand extends PHATCommand {

    private String bodyId;
    private boolean on;

    public ShowLabelsOfVisibleObjectsCommand() {
    }

    public ShowLabelsOfVisibleObjectsCommand(String bodyId, boolean on, PHATCommandListener listener) {
        super(listener);
        this.bodyId = bodyId;
        this.on = on;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }

    public ShowLabelsOfVisibleObjectsCommand(String bodyId, boolean on) {
        this(bodyId, on, null);
    }

    @Override
    public void runCommand(Application app) {
        BodiesAppState bodiesAppState = app.getStateManager().getState(BodiesAppState.class);

        Node body = bodiesAppState.getBody(bodyId);
        if (body != null) {
            VisionControl vc = body.getControl(VisionControl.class);
            if (vc != null) {
                if (on) {
                    AttachLabelToVisibleObjectsControl altvo = body.getControl(AttachLabelToVisibleObjectsControl.class);
                    if (altvo == null) {
                        altvo = new AttachLabelToVisibleObjectsControl();
                        body.addControl(altvo);
                    }
                    altvo.setEnabled(on);
                    setState(State.Success);
                    return;
                } else {
                    AttachLabelToVisibleObjectsControl altvo = body.getControl(AttachLabelToVisibleObjectsControl.class);
                    if (altvo != null) {
                        altvo.setEnabled(false);
                        setState(State.Success);
                        return;
                    }
                }
            }
        }
        setState(State.Fail);
    }

    @Override
    public void interruptCommand(Application app) {
        setState(State.Fail);
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
    public void setOn(boolean on) {
        this.on = on;
    }    
}
