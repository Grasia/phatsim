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
import static java.lang.Thread.sleep;
import java.util.logging.Level;
import java.util.logging.Logger;
import phat.commands.PHATCommandListener;
import phat.devices.DevicesAppState;
import phat.mobile.adm.AndroidVirtualDevice;
import phat.server.ServerAppState;

/**
 *
 * @author pablo
 */
public class SlideFingerOnScreen extends PHATServerCommand {

    private String smartphoneId;
    private int xSource;
    private int ySource;
    private int xTarget;
    private int yTarget;
    private int duration = 0;
    TouchingThread thread;

    public SlideFingerOnScreen(String smartphoneId, int xSource, int ySource, int xTarget, int yTarget, int duration) {
        this(smartphoneId, xSource, ySource, xTarget, yTarget, duration, null);
    }

    public SlideFingerOnScreen(String smartphoneId, int xSource, int ySource, int xTarget, int yTarget, int duration, PHATCommandListener listener) {
        super(listener);
        this.smartphoneId = smartphoneId;
        this.xSource = xSource;
        this.ySource = ySource;
        this.xTarget = xTarget;
        this.yTarget = yTarget;
        this.duration = duration;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }

    @Override
    public void runCommand(Application app) {
        thread = new TouchingThread(app, this);
        thread.start();
    }

    class TouchingThread extends Thread {

        Application app;
        SlideFingerOnScreen pos;

        public TouchingThread(Application app, SlideFingerOnScreen pos) {
            this.app = app;
            this.pos = pos;
        }

        @Override
        public void run() {
            DevicesAppState devicesAppState = app.getStateManager().getState(DevicesAppState.class);
            ServerAppState serverAppState = app.getStateManager().getState(ServerAppState.class);
            Node smartphone = devicesAppState.getDevice(smartphoneId);
            if (smartphone != null) {
                AndroidVirtualDevice avd = serverAppState.getAVD(smartphoneId);
                if (avd != null) {
                    avd.drag(xSource, ySource, xTarget, yTarget, 5, duration);
                    pos.setState(SlideFingerOnScreen.State.Success);
                    return;
                }
            }
            pos.setState(SlideFingerOnScreen.State.Fail);
        }
    }

    @Override
    public void interruptCommand(Application app) {
        setState(State.Interrupted);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + smartphoneId + ", " + xSource + ", " + ySource + ", " + xTarget+ ", "+ yTarget + ","+duration+")";
    }
}
