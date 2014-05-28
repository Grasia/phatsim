package phat.devices.commands;

import com.jme3.app.Application;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.logging.Level;
import phat.commands.PHATCommandListener;
import phat.devices.DevicesAppState;
import phat.devices.smartphone.SmartPhoneFactory;
import phat.util.Debug;

/**
 *
 * @author pablo
 */
public class CreateSmartphoneCommand extends PHATDeviceCommand {

    private String smartphoneId;
    private boolean cameraSensor = false;
    private boolean accelerometerSensor = false;
    private boolean microphoneSensor = false;
    private boolean attachCoordinateAxes = false;
    
    public CreateSmartphoneCommand(String smartphoneId) {
        this(smartphoneId, null);
    }

    public CreateSmartphoneCommand(String smartphoneId, PHATCommandListener listener) {
        super(listener);
        this.smartphoneId = smartphoneId;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }

    @Override
    public void runCommand(Application app) {
        DevicesAppState devicesAppState = app.getStateManager().getState(DevicesAppState.class);
        Node smartphone = SmartPhoneFactory.createSmartphone(smartphoneId);        
        smartphone.setName(smartphoneId);

        SmartPhoneFactory.enableAccelerometerFacility(smartphone);
        SmartPhoneFactory.enableMicrophoneFacility(smartphone);
        SmartPhoneFactory.enableCameraFacility(smartphone);
        
        if(attachCoordinateAxes) {
            Debug.attachCoordinateAxes(Vector3f.ZERO, 0.5f, SmartPhoneFactory.assetManager, smartphone);
        }
        devicesAppState.addDevice(smartphoneId, smartphone);
        setState(State.Success);
    }

    @Override
    public void interruptCommand(Application app) {
        setState(State.Interrupted);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + smartphoneId +")";
    }

    public CreateSmartphoneCommand setAttachCoordinateAxes(boolean attachCoordinateAxes) {
        this.attachCoordinateAxes = attachCoordinateAxes;
        return this;
    }
}
