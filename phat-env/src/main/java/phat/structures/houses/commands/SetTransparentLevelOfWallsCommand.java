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
package phat.structures.houses.commands;

import com.jme3.app.Application;
import com.jme3.asset.AssetManager;
import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.structures.houses.House;
import phat.structures.houses.HouseAppState;
import phat.util.SpatialUtils;

/**
 *
 * @author pablo
 */
public class SetTransparentLevelOfWallsCommand extends PHATCommand {

    float level;
    String houseName;
    Material transMat;

    public SetTransparentLevelOfWallsCommand(String houseName, float level) {
        this(houseName, level, null);
    }

    public SetTransparentLevelOfWallsCommand(String houseName, float level, PHATCommandListener l) {
        super(l);
        this.houseName = houseName;
        this.level = level;
    }

    private Material createMat(AssetManager assetManager) {
        Material mat = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", new ColorRGBA(1.0f, 1.0f, 1.0f, level));
        return mat;
    }

    @Override
    public void runCommand(Application app) {
        HouseAppState houseAppState = app.getStateManager().getState(HouseAppState.class);
        AssetManager assetManager = app.getAssetManager();
        Material newMat = createMat(assetManager);
        
        if (houseAppState != null) {
            House house = houseAppState.getHouse(houseName);
            if (house != null) {
                for (Spatial wall : SpatialUtils.getSpatialsByRole(house.getRootNode(), "WALL")) {
                    System.out.println("Wall = " + wall.getUserData("ID"));
                    if (wall instanceof Geometry) {
                        //wall.removeFromParent();
                        Geometry wallGeo = (Geometry) wall;
                        /*Material wallMat = wallGeo.getMaterial();
                        System.out.println("****************************");
                        for (MatParam mp : wallMat.getParams()) {
                            System.out.println(mp.getName() + ":" + mp.getValueAsString());
                        }
                        System.out.println("****************************");
                        MatParam matParam = wallMat.getParam("Color");
                        if (matParam != null) {
                            ColorRGBA color = (ColorRGBA) matParam.getValue();
                            color.set(color.getRed(), color.getGreen(), color.getBlue(), level);
                        } else {
                            wallMat.setBoolean("UseAlpha", true);
                        }*/
                        Material wallMat = newMat;
                        wallMat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);  // !
                        wallGeo.setQueueBucket(Bucket.Transparent);
                        wallGeo.setMaterial(wallMat);
                    }
                }
                house.getRootNode();
                setState(PHATCommand.State.Success);
                return;
            }
        }
        setState(PHATCommand.State.Fail);
    }

    @Override
    public void interruptCommand(Application app) {
        setState(PHATCommand.State.Fail);
    }
}
