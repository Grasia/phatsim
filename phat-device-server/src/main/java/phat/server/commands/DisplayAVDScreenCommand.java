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
package phat.server.commands;

import com.jme3.app.Application;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.List;
import java.util.logging.Level;
import phat.commands.PHATCommandListener;
import phat.devices.DevicesAppState;
import phat.devices.controls.ScreenAVDControl;
import static phat.devices.smartphone.SmartPhoneFactory.assetManager;
import phat.mobile.adm.AndroidVirtualDevice;
import phat.server.ServerAppState;
import phat.util.SpatialUtils;

/**
 *
 * @author pablo
 */
public class DisplayAVDScreenCommand extends PHATServerCommand {

    private String smartphoneId;
    private String avdId;
    private float frecuency = 1f;
    private boolean on = true;

    public DisplayAVDScreenCommand(String smartphoneId, String avdId) {
        this(smartphoneId, avdId, null);
    }

    public DisplayAVDScreenCommand(String smartphoneId, String avdId, boolean on) {
        this(smartphoneId, avdId, on, null);
    }

    public DisplayAVDScreenCommand(String smartphoneId, String avdId, PHATCommandListener listener) {
        this(smartphoneId, avdId, true, null);
    }

    public DisplayAVDScreenCommand(String smartphoneId, String avdId, boolean on, PHATCommandListener listener) {
        super(listener);
        this.smartphoneId = smartphoneId;
        this.avdId = avdId;
        this.on = on;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }

    @Override
    public void runCommand(Application app) {
        DevicesAppState devicesAppState = app.getStateManager().getState(DevicesAppState.class);
        ServerAppState serverAppState = app.getStateManager().getState(ServerAppState.class);
        Node device = devicesAppState.getDevice(smartphoneId);
        AndroidVirtualDevice avd = serverAppState.getAVD(smartphoneId);
        if (device != null && avd != null) {
            List<Spatial> screens = SpatialUtils.getSpatialsByRole(device, "Screen");
            System.out.println("#Screen = " + screens.size());
            if (screens.size() > 0) {
                Spatial screen = screens.get(0);
                ScreenAVDControl c = screen.getControl(ScreenAVDControl.class);
                if (on && c == null) {
                    screen.setMaterial(new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md"));
                    c = new ScreenAVDControl((Geometry) screen, avd);
                    screen.addControl(c);
                    c.setFrecuency(frecuency);

                } else if (!on && c != null) {
                    screen.removeControl(c);
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
        return getClass().getSimpleName() + "(" + smartphoneId + ", " + avdId + ", " + on + ")";
    }

    public float getFrecuency() {
        return frecuency;
    }

    public void setFrecuency(float frecuency) {
        this.frecuency = frecuency;
    }
}
