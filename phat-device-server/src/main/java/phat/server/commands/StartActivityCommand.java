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
import com.jme3.scene.Node;
import java.util.logging.Level;
import phat.commands.PHATCommandListener;
import phat.devices.DevicesAppState;
import phat.mobile.adm.AndroidVirtualDevice;
import phat.server.ServerAppState;

/**
 *
 * @author pablo
 */
public class StartActivityCommand extends PHATServerCommand {

    private String smartphoneId;
    private String packageName;
    private String activityName;

    public StartActivityCommand(String smartphoneId, String packageName, String activityName) {
        this(smartphoneId, packageName, activityName, null);
    }

    public StartActivityCommand(String smartphoneId, String packageName, String activityName, PHATCommandListener listener) {
        super(listener);
        this.smartphoneId = smartphoneId;
        this.packageName = packageName;
        this.activityName = activityName;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }

    @Override
    public void runCommand(Application app) {
        DevicesAppState devicesAppState = app.getStateManager().getState(DevicesAppState.class);
        ServerAppState serverAppState = app.getStateManager().getState(ServerAppState.class);
        Node smartphone = devicesAppState.getDevice(smartphoneId);
        if(smartphone != null) {
            AndroidVirtualDevice avd = serverAppState.getAVD(smartphoneId);
            if(avd != null) {
                avd.startActivity(packageName, activityName);
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
        return getClass().getSimpleName() + "(" + smartphoneId + ", " + packageName + ", " + activityName + ")";
    }
}
