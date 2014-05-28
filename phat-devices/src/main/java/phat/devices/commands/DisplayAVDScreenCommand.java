package phat.devices.commands;

import com.jme3.app.Application;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import phat.commands.PHATCommandListener;
import phat.devices.DevicesAppState;
import phat.devices.controls.ScreenAVDControl;
import phat.devices.smartphone.SmartPhoneFactory;
import phat.mobile.adm.AndroidVirtualDevice;

/**
 *
 * @author pablo
 */
public class DisplayAVDScreenCommand extends PHATDeviceCommand {

    private String smartphoneId;
    private String avdId;
    private float frecuency = 1f;

    public DisplayAVDScreenCommand(String smartphoneId, String avdId) {
        this(smartphoneId, avdId, null);
    }

    public DisplayAVDScreenCommand(String smartphoneId, String avdId, PHATCommandListener listener) {
        super(listener);
        this.smartphoneId = smartphoneId;
        this.avdId = avdId;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }

    @Override
    public void runCommand(Application app) {
        DevicesAppState devicesAppState = app.getStateManager().getState(DevicesAppState.class);
        Node device = devicesAppState.getDevice(smartphoneId);
        AndroidVirtualDevice avd = devicesAppState.getAVD(avdId);
        if (device != null && avd != null) {
            ScreenAVDControl c = device.getControl(ScreenAVDControl.class);
            if(c == null) {
                c = new ScreenAVDControl(device, avd);
                device.addControl(c);                
            }
            c.setFrecuency(frecuency);
            setState(State.Success);
            return;
        }
        setState(State.Fail);
    }

    @Override
    public void interruptCommand(Application app) {
        setState(State.Interrupted);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + smartphoneId + ", " + avdId + ")";
    }

    public float getFrecuency() {
        return frecuency;
    }

    public void setFrecuency(float frecuency) {
        this.frecuency = frecuency;
    }
}
