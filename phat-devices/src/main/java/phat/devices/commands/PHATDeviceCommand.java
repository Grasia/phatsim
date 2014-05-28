/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.devices.commands;

import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;

/**
 *
 * @author pablo
 */
public abstract class PHATDeviceCommand extends PHATCommand {
    
    public PHATDeviceCommand(PHATCommandListener listener) {
        super(listener);
    }
    
}
