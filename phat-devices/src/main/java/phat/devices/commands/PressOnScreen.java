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
import com.jme3.scene.Node;
import edu.cmu.sphinx.frontend.filter.Preemphasizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import phat.commands.PHATCommandListener;
import phat.devices.DevicesAppState;
import phat.mobile.adm.AndroidVirtualDevice;

/**
 *
 * @author pablo
 */
public class PressOnScreen extends PHATDeviceCommand {

    private String smartphoneId;
    private int x;
    private int y;
    TouchingThread thread;

    public PressOnScreen(String smartphoneId, int x, int y) {
        this(smartphoneId, x, y, null);
    }

    public PressOnScreen(String smartphoneId, int x, int y, PHATCommandListener listener) {
        super(listener);
        this.smartphoneId = smartphoneId;
        this.x = x;
        this.y = y;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }

    @Override
    public void runCommand(Application app) {
        thread = new TouchingThread(app, this);
        thread.start();
    }

    class TouchingThread extends Thread {
        Application app;
        PressOnScreen pos;

        public TouchingThread(Application app, PressOnScreen pos) {
            this.app = app;
            this.pos = pos;
        }
        
        @Override
        public void run() {
            DevicesAppState devicesAppState = app.getStateManager().getState(DevicesAppState.class);
            Node smartphone = devicesAppState.getDevice(smartphoneId);
            if (smartphone != null) {
                AndroidVirtualDevice avd = devicesAppState.getAVD(smartphoneId);
                if (avd != null) {
                    avd.touchDown(x, y);
                    try {
                        sleep(700);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(PressOnScreen.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    avd.touchUp(x, y);
                    pos.setState(PressOnScreen.State.Success);
                    return;
                }
            }
            pos.setState(PressOnScreen.State.Fail);
        }
    }

    @Override
    public void interruptCommand(Application app) {
        setState(State.Interrupted);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + smartphoneId + ", " + x + ", " + y + ")";
    }
}
