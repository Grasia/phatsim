package phat.config.impl;

import phat.config.DeviceConfigurator;
import phat.devices.DevicesAppState;
import phat.devices.commands.PHATDeviceCommand;

public class DeviceConfiguratorImpl implements DeviceConfigurator {

    DevicesAppState devicesAppState;

    public DeviceConfiguratorImpl(DevicesAppState devicesAppState) {
        super();
        this.devicesAppState = devicesAppState;
    }

    @Override
    public void runCommand(PHATDeviceCommand command) {
        devicesAppState.runCommand(command);
    }

    public DevicesAppState getDevicesAppState() {
        return devicesAppState;
    }
}
