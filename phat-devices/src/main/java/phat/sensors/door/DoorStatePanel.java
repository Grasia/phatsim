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
package phat.sensors.door;

import java.util.Calendar;
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
public class DoorStatePanel extends JPanel implements SensorListener {

    JLabel labelId;
    JLabel state;
    JLabel timestamp;
    Calendar calendar;

    public DoorStatePanel() {
        calendar = Calendar.getInstance();

        labelId = new JLabel();
        state = new JLabel("?");
        timestamp = new JLabel("?");

        JPanel pId = new JPanel();
        pId.add(new JLabel("Id:"));
        pId.add(labelId);
        add(pId);

        JPanel pState = new JPanel();
        pState.add(new JLabel("Presence:"));
        pState.add(state);
        add(pState);

        JPanel pTS = new JPanel();
        pTS.add(new JLabel("TS:"));
        pTS.add(timestamp);
        add(pTS);

        TitledBorder titledBorder = BorderFactory.createTitledBorder("Presence Sensor");
        setBorder(titledBorder);
    }

    @Override
    public void update(Sensor source, SensorData sd) {
        if (sd instanceof DoorData) {
            DoorData pd = (DoorData) sd;
            if (pd.isOpened()) {
                state.setText("YES");
            } else {
                state.setText("NO");
            }
            labelId.setText(source.getId());
            calendar.setTimeInMillis(pd.getTimestamp());
            timestamp.setText(
                    calendar.get(Calendar.HOUR_OF_DAY) + ":"
                    + calendar.get(Calendar.MINUTE) + ":"
                    + calendar.get(Calendar.SECOND) + " " +
                    getDayOfMonth()+"/"+
                    getMonth()+"/"+
                    getYear());
            updateUI();
        }
    }

    public int getYear() {
        return calendar.get(Calendar.YEAR);
    }

    public int getMonth() {
        return calendar.get(Calendar.MONTH) + 1;
    }

    public int getDayOfMonth() {
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    public void cleanUp() {
    }
}
