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
import com.jme3.scene.Spatial;
import java.util.logging.Level;
import phat.body.BodiesAppState;
import phat.body.control.animation.LookAtControl;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.util.SpatialUtils;

/**
 *
 * @author pablo
 */
public class LookAtCommand extends PHATCommand {

    private String bodyId;
    private String targetId;

    public LookAtCommand(String bodyId, String targetId, PHATCommandListener listener) {
        super(listener);
        this.bodyId = bodyId;
        this.targetId = targetId;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }

    public LookAtCommand(String bodyId, String targetId) {
        this(bodyId, targetId, null);
    }

    @Override
    public void runCommand(Application app) {
        BodiesAppState bodiesAppState = app.getStateManager().getState(BodiesAppState.class);

        Node body = bodiesAppState.getBody(bodyId);
        if (body != null) {
            LookAtControl lac = body.getControl(LookAtControl.class);
            if (targetId == null) {
                if (lac != null) {
                    body.removeControl(lac);
                }
                setState(State.Success);
                return;
            } else {
                Spatial obj = SpatialUtils.getSpatialById(SpatialUtils.getRootNode(body), targetId);
                if (obj != null) {
                    if (lac == null) {
                        lac = new LookAtControl();
                        body.addControl(lac);
                    }
                    lac.setTarget(obj);
                    setState(State.Success);
                    return;
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
        return getClass().getSimpleName() + "(" + bodyId + ", targetId=" + targetId + ")";
    }
}
