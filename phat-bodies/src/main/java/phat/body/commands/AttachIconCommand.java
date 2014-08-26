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
import com.jme3.scene.control.BillboardControl;
import java.util.logging.Level;
import phat.body.BodiesAppState;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.scene.control.PHATBillboardControl;
import phat.util.SpatialFactory;

/**
 *
 * @author pablo
 */
public class AttachIconCommand extends PHATCommand {    
    private String bodyId;
    private String imagePath;
    private Boolean show;

    public AttachIconCommand(String bodyId, String imagePath, Boolean show, PHATCommandListener listener) {
        super(listener);
        this.bodyId = bodyId;
        this.imagePath = imagePath;
        this.show = show;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }

    public AttachIconCommand(String bodyId, String imagePath, Boolean show) {
        this(bodyId, imagePath, show, null);
    }

    @Override
    public void runCommand(Application app) {
        BodiesAppState bodiesAppState = app.getStateManager().getState(BodiesAppState.class);

        Node body = bodiesAppState.getBody(bodyId);
        Node pivot = (Node) body.getChild("Pivot");
        if(pivot == null) {
            pivot = new Node("Pivot");
        }
        Spatial iconGeo = pivot.getChild(imagePath);

        if (show && iconGeo == null) {
            iconGeo = SpatialFactory.createImageTextureSurface(imagePath, 0.2f, 0.2f);
            iconGeo.setName(imagePath);
            iconGeo.setLocalTranslation(0f, 2f, 0f);
            pivot.attachChild(iconGeo);
            body.attachChild(pivot);
           BillboardControl control = new BillboardControl();
            iconGeo.addControl(control);            
        } else if (!show && iconGeo != null) {
            iconGeo.removeFromParent();
        }
        setState(PHATCommand.State.Success);
    }

    @Override
    public void interruptCommand(Application app) {
        show = false;
        runCommand(app);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + bodyId + ",imagePath="+imagePath+",show=" + show + ")";
    }
}
