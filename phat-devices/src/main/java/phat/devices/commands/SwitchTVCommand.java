/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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

    @Override
    public void interruptCommand(Application app) {
        setState(State.Interrupted);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + tvId + ")";
    }
}
