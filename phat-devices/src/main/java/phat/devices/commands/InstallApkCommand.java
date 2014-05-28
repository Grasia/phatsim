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
public class InstallApkCommand extends PHATDeviceCommand {

    private String smartphoneId;
    private String apkFile;

    public InstallApkCommand(String smartphoneId, String apkFile) {
        this(smartphoneId, apkFile, null);
    }

    public InstallApkCommand(String smartphoneId, String apkFile, PHATCommandListener listener) {
        super(listener);
        this.smartphoneId = smartphoneId;
        this.apkFile = apkFile;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }

    @Override
    public void runCommand(Application app) {
        DevicesAppState devicesAppState = app.getStateManager().getState(DevicesAppState.class);
        Node smartphone = devicesAppState.getDevice(smartphoneId);
        if(smartphone != null) {
            AndroidVirtualDevice avd = devicesAppState.getAVD(smartphoneId);
            if(avd != null) {
                avd.install(apkFile);
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
        return getClass().getSimpleName() + "(" + smartphoneId + ", " + apkFile + ")";
    }
}
