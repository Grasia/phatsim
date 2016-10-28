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
import com.jme3.font.BitmapText;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import java.util.logging.Level;

import phat.body.BodiesAppState;
import phat.commands.PHATCommParam;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.commands.PHATCommand.State;
import phat.commands.PHATCommandAnn;
import phat.util.SpatialFactory;
import phat.util.controls.RemoveSpatialTimerControl;

/**
 *
 * @author pablo
 */
@PHATCommandAnn(name = "BodyLabel", type = "body", debug = true)
public class SetTextOnTopOfBody extends PHATCommand {

    private String bodyId;
    private String text;
    private Boolean show;
    private float seconds = 0f;

    public SetTextOnTopOfBody() {
    }

    public SetTextOnTopOfBody(String bodyId, String text, Boolean show, PHATCommandListener listener) {
        super(listener);
        this.bodyId = bodyId;
        this.text = text;
        this.show = show;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }

    public SetTextOnTopOfBody(String bodyId, String text, Boolean show) {
        this(bodyId, text, show, null);
    }

    @Override
    public void runCommand(Application app) {
        BodiesAppState bodiesAppState = app.getStateManager().getState(BodiesAppState.class);

        Node body = bodiesAppState.getBody(bodyId);
        removeAllText(body);
        
        if (show) {
            Node showedName = SpatialFactory.attachAName(body, bodyId+":"+text);
            showedName.setLocalScale(showedName.getLocalScale().mult(0.5f));
            /*showedName.removeFromParent();
            SpatialFactory.getRootNode().attachChild(showedName);
            Vector3f textCenter = showedName.getWorldBound().getCenter();
            Vector3f bodyCenter = body.getWorldBound().getCenter();
            
            System.out.println("textCenter = "+textCenter);
            System.out.println("bodyCenter = "+bodyCenter);
            
            body.attachChild(showedName);*/
            if(seconds > 0) {
                RemoveSpatialTimerControl rstc = new RemoveSpatialTimerControl(seconds);
                showedName.addControl(rstc);
            } 
            showedName.setLocalTranslation(0f, 3f, 0f);
        }
        setState(State.Success);
    }

    @Override
    public void interruptCommand(Application app) {
        show = false;
        runCommand(app);
    }

    private BitmapText removeAllText(Node body) {
        for (Spatial s : body.getChildren()) {
            if (s instanceof BitmapText) {
                s.removeFromParent();
            }
        }
        return null;
    }

    public String getBodyId() {
        return bodyId;
    }

    public Boolean getShow() {
        return show;
    }

    @PHATCommParam(mandatory = true, order = 1)
    public void setBodyId(String bodyId) {
        this.bodyId = bodyId;
    }

    @PHATCommParam(mandatory = true, order = 2)
    public void setText(String text) {
        this.text = text;
    }

    @PHATCommParam(mandatory = true, order = 3)
    public void setShow(Boolean show) {
        this.show = show;
    }

    @PHATCommParam(mandatory = false, order = 3)
    public void setSeconds(float seconds) {
        this.seconds = seconds;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + bodyId + ",text=" + text + ",show=" + show + ")";
    }
}
