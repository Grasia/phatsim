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
package phat.devices.sensors.presence;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import phat.sensors.Sensor;
import phat.sensors.SensorData;
import phat.sensors.SensorListener;

/**
 *
 * @author pablo
 */
public class PresenceStatePanel extends JPanel implements SensorListener {
    JLabel labelId;
    JLabel state;
    
    public PresenceStatePanel() {
        labelId = new JLabel();
        state = new JLabel("?");
        
        JPanel pId = new JPanel();
        pId.add(new JLabel("Id:"));
        pId.add(labelId);
        add(pId);
        
        JPanel pState = new JPanel();
        pState.add(new JLabel("Presence:"));
        pState.add(state);
        add(pState);
        
        TitledBorder titledBorder = BorderFactory.createTitledBorder("Presence Sensor");
        setBorder(titledBorder);
    }
    
    
    @Override
    public void update(Sensor source, SensorData sd) {
        if(sd instanceof PresenceData) {
            PresenceData pd = (PresenceData) sd;
            if(pd.isPresence()) {
                state.setText("YES");
            } else {
                state.setText("NO");
            }
            labelId.setText(source.getId());
            updateUI();
        }
    }

    @Override
    public void cleanUp() {
    }
    
}
