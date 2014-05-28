/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.devices.commands;

import com.jme3.app.Application;
import com.jme3.scene.Node;
import java.util.logging.Level;
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
        DevicesAppState devicesAppState = app.getStateManager().getState(DevicesAppState.class);
        Node smartphone = devicesAppState.getDevice(smartphoneId);
        if(smartphone != null) {
            AndroidVirtualDevice avd = devicesAppState.getAVD(smartphoneId);
            if(avd != null) {
                avd.tap(x,y);
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
        return getClass().getSimpleName() + "(" + smartphoneId + ", " + x + ", " + y + ")";
    }
}
