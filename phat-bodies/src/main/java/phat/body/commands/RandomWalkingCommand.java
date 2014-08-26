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
import phat.body.control.animation.AnimFinishedListener;
import phat.body.control.animation.BasicCharacterAnimControl;
import phat.body.control.navigation.RandomWalkControl;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.commands.PHATCommand.State;

/**
 *
 * @author pablo
 */
public class RandomWalkingCommand extends PHATCommand implements
        AnimFinishedListener {

    private String bodyId;
    private boolean enabled;

    public RandomWalkingCommand(String bodyId, boolean enabled) {
        this(bodyId, enabled, null);
    }

    public RandomWalkingCommand(String bodyId, boolean enabled, PHATCommandListener listener) {
        super(listener);
        this.bodyId = bodyId;
        this.enabled = enabled;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }

    @Override
    public void runCommand(Application app) {
        BodiesAppState bodiesAppState = app.getStateManager().getState(
                BodiesAppState.class);

        Node body = bodiesAppState.getBody(bodyId);

        if (body != null && body.getParent() != null) {
            if(enabled) {
                body.addControl(new RandomWalkControl());
            } else {
                RandomWalkControl rwc = body.getControl(RandomWalkControl.class);
                if(rwc != null) {
                    body.removeControl(rwc);
                }
                
            }
        }
    }

    @Override
    public void interruptCommand(Application app) {
        BodiesAppState bodiesAppState = app.getStateManager().getState(
                BodiesAppState.class);

        Node body = bodiesAppState.getBody(bodyId);

        if (body != null && body.getParent() != null) {
            BasicCharacterAnimControl bcac = body
                    .getControl(BasicCharacterAnimControl.class);
            bcac.setManualAnimation(null, null);
            setState(State.Interrupted);
            return;
        }
        setState(State.Fail);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + bodyId + ",enabled="
                + enabled + ")";
    }

    @Override
    public void animFinished(BasicCharacterAnimControl.AnimName animationName) {
        System.out.println("Animation finished = " + animationName.name());
        setState(State.Success);
    }
}
