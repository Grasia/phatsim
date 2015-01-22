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
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix4f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.logging.Level;
import phat.body.BodiesAppState;
import phat.body.control.physics.PHATCharacterControl;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.structures.houses.HouseAppState;
import phat.util.PhysicsUtils;
import phat.util.SpatialUtils;

/**
 *
 * @author pablo
 */
public class SetBodyColorCommand extends PHATCommand {

    private String bodyId;
    private ColorRGBA color;

    public SetBodyColorCommand(String bodyId, ColorRGBA color) {
        this(bodyId, color, null);
    }

    public SetBodyColorCommand(String bodyId, ColorRGBA color, PHATCommandListener listener) {
        super(listener);
        this.bodyId = bodyId;
        this.color = color;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }

    @Override
    public void runCommand(Application app) {
        BodiesAppState bodiesAppState = app.getStateManager().getState(BodiesAppState.class);

        Node body = bodiesAppState.getBody(bodyId);

        if (body != null && body.getParent() != null) {
            for(Spatial s: body.getChildren()) {
                if(s instanceof Geometry) {
                    Geometry geo = (Geometry)s;
                    //com.jme3.util.TangentBinormalGenerator.generate(geo.getMesh());
                    geo.setMaterial(getMaterial(color, app.getAssetManager()));
                }
            }
            
            setState(PHATCommand.State.Success);
            return;
        }
        setState(PHATCommand.State.Fail);
    }

    private Material getMaterial(ColorRGBA color, AssetManager assetManager) {
        Material mat = new Material(assetManager, // Create new material and...
                "Common/MatDefs/Light/Lighting.j3md"); // ... specify .j3md file to use (illuminated).
        //mat.setFloat("Shininess", 120f);
        //mat.setColor("Specular",ColorRGBA.Black);
        //mat.setColor("GlowColor",ColorRGBA.Red);
        mat.setBoolean("UseMaterialColors", true);  // Set some parameters, e.g. blue.
        mat.setColor("Ambient", color);   // ... color of this object
        mat.setColor("Diffuse", color);   // ... color of light being reflected
        //mat.setTexture("NormalMap", assetManager.loadTexture("Textures/terrain/grass_normal.jpg")); // with Lighting.j3md
        return mat;
    }
    
    @Override
    public void interruptCommand(Application app) {
        setState(PHATCommand.State.Fail);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + bodyId + ", height=" + color + ")";
    }
}