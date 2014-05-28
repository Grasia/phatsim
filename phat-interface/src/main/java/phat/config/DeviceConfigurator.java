package phat.config;

import phat.devices.commands.PHATDeviceCommand;

public interface DeviceConfigurator {

    public void runCommand(PHATDeviceCommand command);
}
