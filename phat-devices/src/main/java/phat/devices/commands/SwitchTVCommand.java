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
package phat.devices.commands;

import com.jme3.app.Application;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;
import java.util.logging.Level;
import phat.commands.PHATCommandListener;
import phat.devices.DevicesAppState;
import phat.mobile.adm.AndroidVirtualDevice;
import phat.structures.houses.HouseAppState;
import phat.util.SpatialFactory;
import phat.util.SpatialUtils;

/**
 *
 * @author pablo
 */
public class SwitchTVCommand extends PHATDeviceCommand {
    static String TV_STATE_KEY = "TV_STATE_KEY";
    
    private String tvId;
    private boolean on;

    public SwitchTVCommand(String tvId, boolean on) {
        this(tvId, on, null);
    }

    public SwitchTVCommand(String tvId, boolean on, PHATCommandListener listener) {
        super(listener);
        this.tvId = tvId;
        this.on = on;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }

    @Override
    public void runCommand(Application app) {
        HouseAppState houseAppState = app.getStateManager().getState(HouseAppState.class);
        Spatial tv = SpatialUtils.getSpatialById(SpatialFactory.getRootNode(), tvId);
        if (tv != null) {
            String imagePath = "Textures/on.png";
            if (!on) {
                imagePath = "Textures/off.png";
            }
            Geometry display = (Geometry) ((Node) (tv.getParent()).getChild("Geometries")).getChild("tv3");
            Material mat = new Material(SpatialFactory.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
            Texture cube1Tex = SpatialFactory.getAssetManager().loadTexture(imagePath);
            mat.setTexture("ColorMap", cube1Tex);
            display.setMaterial(mat);
        }
        setState(State.Fail);
    }

    public static boolean isTVOn(String tvId) {
        Spatial tv = SpatialUtils.getSpatialById(SpatialFactory.getRootNode(), tvId);
        Boolean state = tv.getUserData(TV_STATE_KEY);
        if(state != null && state) {
            return true;
        }
        return false;
    }
    @Override
    public void interruptCommand(Application app) {
        setState(State.Interrupted);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + tvId + ")";
    }
}
