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
import phat.devices.actuators.VibratorActuator;
import phat.mobile.adm.AndroidVirtualDevice;
import phat.sensors.accelerometer.AccelerometerControl;
import phat.sensors.camera.CameraSensor;
import phat.sensors.microphone.MicrophoneControl;
import phat.server.PHATServerManager;
import phat.server.ServerAppState;
import phat.server.accelerometer.TCPAccelerometerServer;
import phat.server.camera.TCPCameraSensorServer;
import phat.server.microphone.TCPAudioMicroServer;

/**
 *
 * @author pablo
 */
public class SetAndroidEmulatorCommand extends PHATServerCommand {

    private String smartphoneId;
    private String avdId;
    private String serialEmulator;
    private String emuOptions;

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
        ServerAppState serverAppState = app.getStateManager().getState(ServerAppState.class);
        devicesAppState.registerAllAndroidDevicesInScenario();
        Node device = devicesAppState.getDevice(smartphoneId);
        if (device != null) {
            AndroidVirtualDevice avd = new AndroidVirtualDevice(avdId, serialEmulator, smartphoneId);
            if(emuOptions != null) {
                avd.setEmuOptions(emuOptions);
            }
            PHATServerManager serverManager = serverAppState.getServerManager();
            avd.sendConfigFileForService(serverManager.getIP(), serverManager.getPort());

            CameraSensor cameraSensor = device.getControl(CameraSensor.class);
            if (cameraSensor != null) {
                TCPCameraSensorServer cameraServer = serverAppState.getServerManager().createAndStartCameraServer(smartphoneId, "camback-"+smartphoneId, cameraSensor);
                cameraServer.setRate(1f);
                }

            MicrophoneControl micSensor = device.getControl(MicrophoneControl.class);
            if(micSensor != null) {
                TCPAudioMicroServer audioServer = serverManager.createAndStartAudioMicroServer(smartphoneId, "mic-"+smartphoneId, micSensor);
            }

            AccelerometerControl accSensor = device.getControl(AccelerometerControl.class);
            if(accSensor != null) {
                TCPAccelerometerServer accServer = serverManager.createAndStartAccelerometerServer(smartphoneId, "gsensor-"+smartphoneId, accSensor);
            }

            VibratorActuator vibratorActuator = device.getControl(VibratorActuator.class);
            if(vibratorActuator != null) {
                serverManager.createAndStartVibratorServer(smartphoneId, "vibrator-"+smartphoneId, vibratorActuator);
            }
            
            serverManager.createAndStartAudioSpeakerServer(serverAppState, smartphoneId, "speaker-"+smartphoneId, device);
            
            serverAppState.addAVD(smartphoneId, avd);

            setState(State.Success);
            return;
        }
        setState(State.Fail);
    }

    public String getEmuOptions() {
        return emuOptions;
    }

    public SetAndroidEmulatorCommand setEmuOptions(String emuOptions) {
        this.emuOptions = emuOptions;
        return this;
    }

    public String getSmartphoneId() {
        return smartphoneId;
    }

    public String getAvdId() {
        return avdId;
    }

    public String getSerialEmulator() {
        return serialEmulator;
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
