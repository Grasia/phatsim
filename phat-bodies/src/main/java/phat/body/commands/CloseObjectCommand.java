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
import com.jme3.audio.AudioNode;
import com.jme3.effect.ParticleEmitter;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;

import java.util.logging.Level;

import phat.body.BodiesAppState;
import phat.commands.PHATCommParam;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.commands.PHATCommand.State;
import phat.commands.PHATCommandAnn;
import phat.controls.FridgeDoorControl;
import phat.util.SpatialFactory;
import phat.util.SpatialUtils;

/**
 *
 * @author pablo
 */
@PHATCommandAnn(name = "CloseObject", type = "body", debug = false)
public class CloseObjectCommand extends PHATCommand {

    float minDistanceToAction = 1f;
    String bodyId;
    String objectId;

    public CloseObjectCommand() {
    }

    public CloseObjectCommand(String bodyId, String objectId, PHATCommandListener listener) {
        super(listener);
        this.bodyId = bodyId;
        this.objectId = objectId;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }

    public CloseObjectCommand(String bodyId, String entityId) {
        this(bodyId, entityId, null);
    }

    @Override
    public void runCommand(Application app) {
        BodiesAppState bodiesAppState = app.getStateManager().getState(BodiesAppState.class);

        Node body = bodiesAppState.getBody(bodyId);

        if (body != null && body.getParent() != null) {
            Spatial object = SpatialUtils.getSpatialById(
                    SpatialFactory.getRootNode(), objectId);
            String rol = object.getUserData("ROLE");
            if (rol != null
                    && object.getWorldTranslation().distance(body.getWorldTranslation()) < minDistanceToAction) {
                ParticleEmitter emitter = (ParticleEmitter) ((Node) object).getChild("Emitter");
                if (emitter != null) {
                    emitter.setEnabled(false);
                    emitter.setCullHint(Spatial.CullHint.Always);
                }
                Spatial s = ((Node) object).getChild("AudioNode");
                if (s != null && s instanceof AudioNode) {
                    AudioNode an = (AudioNode) s;
                    an.stop();
                }
                if (rol.equals("Fridge")) {
                    Node fridge = object.getParent();
                    if (fridge.getChild("Hinge") != null) {
                        FridgeDoorControl control = fridge.getChild("Hinge").getControl(FridgeDoorControl.class);
                        if (control != null) {
                            control.setState(FridgeDoorControl.STATE.CLOSED);
                            setState(State.Success);
                            return;
                        }
                    }
                    setState(State.Fail);
                    return;
                } else if (rol.equals("TV")) {
                    Spatial tv = SpatialUtils.getSpatialById(SpatialFactory.getRootNode(), objectId);
                    if (tv != null) {
                        String imagePath = "Textures/off.png";
                        Geometry display = (Geometry) ((Node) (tv.getParent()).getChild("Geometries")).getChild("tv3");
                        Material mat = new Material(SpatialFactory.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
                        Texture cube1Tex = SpatialFactory.getAssetManager().loadTexture(imagePath);
                        mat.setTexture("ColorMap", cube1Tex);
                        display.setMaterial(mat);
                    }
                }
                setState(State.Success);
                return;
            }
        }
        setState(State.Fail);
    }

    @Override
    public void interruptCommand(Application app) {
        setState(State.Interrupted);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + bodyId + ", " + objectId + ")";
    }

    public String getBodyId() {
        return bodyId;
    }

    public String getEntityId() {
        return objectId;
    }

    public float getMinDistanceToAction() {
        return minDistanceToAction;
    }

    public String getObjectId() {
        return objectId;
    }

    @PHATCommParam(mandatory = true, order = 1)
    public void setBodyId(String bodyId) {
        this.bodyId = bodyId;
    }

    @PHATCommParam(mandatory = true, order = 2)
    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    @PHATCommParam(mandatory = false, order = 3)
    public void setMinDistance(float minDistanceToAction) {
        this.minDistanceToAction = minDistanceToAction;
    }
}
