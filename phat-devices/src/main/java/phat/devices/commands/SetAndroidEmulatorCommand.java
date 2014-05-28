package phat.devices.commands;

import com.jme3.app.Application;
import com.jme3.scene.Node;
import com.sun.corba.se.spi.activation.ServerManager;
import java.util.logging.Level;
import phat.commands.PHATCommandListener;
import phat.devices.DevicesAppState;
import phat.devices.smartphone.SmartPhoneFactory;
import phat.mobile.adm.AndroidVirtualDevice;
import phat.sensors.accelerometer.AccelerometerControl;
import phat.sensors.camera.CameraSensor;
import phat.sensors.microphone.MicrophoneControl;
import phat.server.PHATServerManager;
import phat.server.accelerometer.TCPAccelerometerServer;
import phat.server.camera.TCPCameraSensorServer;
import phat.server.microphone.TCPAudioMicroServer;

/**
 *
 * @author pablo
 */
public class SetAndroidEmulatorCommand extends PHATDeviceCommand {

    private String smartphoneId;
    private String avdId;
    private String serialEmulator;

    public SetAndroidEmulatorCommand(String smartphoneId, String avdId, String serialEmulator) {
        this(smartphoneId, avdId, serialEmulator, null);
    }

    public SetAndroidEmulatorCommand(String smartphoneId, String avdId, String serialEmulator, PHATCommandListener listener) {
        super(listener);
        this.smartphoneId = smartphoneId;
        this.avdId = avdId;
        this.serialEmulator = serialEmulator;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }

    @Override
    public void runCommand(Application app) {
        DevicesAppState devicesAppState = app.getStateManager().getState(DevicesAppState.class);
        Node device = devicesAppState.getDevice(smartphoneId);
        if (device != null) {
            AndroidVirtualDevice avd = new AndroidVirtualDevice(avdId, serialEmulator, smartphoneId);
            PHATServerManager serverManager = devicesAppState.getServerManager();
            avd.sendConfigFileForService(serverManager.getIP(), serverManager.getPort());

            CameraSensor cameraSensor = device.getControl(CameraSensor.class);
            if (cameraSensor != null) {
                TCPCameraSensorServer cameraServer = devicesAppState.getServerManager().createAndStartCameraServer(avdId, cameraSensor);
                cameraServer.setRate(1f);
            }

            MicrophoneControl micSensor = device.getControl(MicrophoneControl.class);
            if(micSensor != null) {
                TCPAudioMicroServer audioServer = serverManager.createAndStartAudioMicroServer(avdId, micSensor);
            }
            
            AccelerometerControl accSensor = device.getControl(AccelerometerControl.class);
            if(accSensor != null) {
                TCPAccelerometerServer accServer = serverManager.createAndStartAccelerometerServer(avdId, accSensor);
            }
            
            devicesAppState.addAVD(smartphoneId, avd);

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
}
