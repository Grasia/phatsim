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
package phat.gui.eventLauncher;

import com.android.sdklib.devices.DeviceManager;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import phat.devices.DevicesAppState;
import phat.devices.commands.VibrateDeviceCommand;

/**
 *
 * @author pablo
 */
public class VibrateEventLauncherPanel extends JPanel {
    DevicesAppState devicesAppState;
    
    JComboBox deviceIds;
    JTextField durationTF;
    
    public VibrateEventLauncherPanel(DevicesAppState devicesAppState) {
        this.devicesAppState = devicesAppState;
        
        setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
        
        JLabel deviceIdLabel = new JLabel("Device Id:");
        deviceIds = new JComboBox(devicesAppState.getDeviceIds().toArray());
        
        JLabel durationLabel = new JLabel("Time (ms):");
        durationTF = new JTextField("1000");
        durationTF.setColumns(4);
        
        JButton sendCommand = new JButton("Send");
        sendCommand.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String deviceId = deviceIds.getModel().getSelectedItem().toString();
                
                if(VibrateEventLauncherPanel.this.devicesAppState.getDevice(deviceId) != null) {
                    String duration = durationTF.getText();
                    
                    int d = Integer.parseInt(duration);
                    
                    VibrateEventLauncherPanel.this.devicesAppState.runCommand(
                            new VibrateDeviceCommand(deviceId, d));
                }
            }
        });
        
        JPanel panel1 = new JPanel();
        panel1.add(deviceIdLabel);
        panel1.add(deviceIds);
        add(panel1);
        
        JPanel panel2 = new JPanel();
        panel2.add(durationLabel);
        panel2.add(durationTF);
        add(panel2);
        
        add(sendCommand);
    }
}
