/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.devices.commands;

import com.jme3.app.Application;
import com.jme3.bullet.control.KinematicRagdollControl;
import com.jme3.scene.Node;
import java.util.logging.Level;
import phat.commands.PHATCommandListener;
import phat.devices.DevicesAppState;
import phat.devices.smartphone.SmartPhoneFactory;
import phat.mobile.adm.AndroidVirtualDevice;

/**
 *
 * @author pablo
 */
public class StartActivityCommand extends PHATDeviceCommand {

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
        Node smartphone = devicesAppState.getDevice(smartphoneId);
        if(smartphone != null) {
            AndroidVirtualDevice avd = devicesAppState.getAVD(smartphoneId);
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
