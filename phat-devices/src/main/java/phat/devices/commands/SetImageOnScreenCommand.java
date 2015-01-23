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
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.PhysicsControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
import java.util.List;

import java.util.logging.Level;

import phat.commands.PHATCommandListener;
import phat.devices.DevicesAppState;
import static phat.devices.smartphone.SmartPhoneFactory.assetManager;
import phat.util.SpatialFactory;
import phat.util.SpatialUtils;

/**
 *
 * @author pablo
 */
public class SetImageOnScreenCommand extends PHATDeviceCommand {

    private String deviceId;
    private String imagePath;

    public SetImageOnScreenCommand(String deviceId, String imagePath) {
        this(deviceId, imagePath, null);
    }

    public SetImageOnScreenCommand(String deviceId, String imagePath, PHATCommandListener listener) {
        super(listener);
        this.deviceId = deviceId;
        this.imagePath = imagePath;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }

    @Override
    public void runCommand(Application app) {
        DevicesAppState devicesAppState = app.getStateManager().getState(DevicesAppState.class);

        Node device = devicesAppState.getDevice(deviceId);

        if (device != null) {
            List<Spatial> screens = SpatialUtils.getSpatialsByRole(device, "Screen");
            if (screens.size() > 0) {
                Geometry geo = (Geometry) screens.get(0);
                Node p = geo.getParent();
                geo.removeFromParent();
                geo = new Geometry("Screen", new Quad(1f, 0.6f));
                geo.setUserData("ID", "Screen1");
                geo.setUserData("ROLE", "Screen");
                Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                mat.setTexture("ColorMap", assetManager.loadTexture(imagePath));
                geo.setMaterial(mat);
                geo.move(-1.4f, 0f, -0.6f);
                geo.rotate(-FastMath.HALF_PI, 0f, 0f);
                geo.setLocalScale(Vector3f.UNIT_XYZ.divide(p.getWorldScale()));
                p.attachChild(geo);
                setState(State.Success);
                return;
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
        return getClass().getSimpleName() + "(" + deviceId + ", " + imagePath + ")";
    }
}
