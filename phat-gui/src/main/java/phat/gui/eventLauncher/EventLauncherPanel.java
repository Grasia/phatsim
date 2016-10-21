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

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowListener;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import phat.devices.DevicesAppState;

/**
 *
 * @author pablo
 */
public class EventLauncherPanel implements ItemListener {

    DevicesAppState devicesAppState;
    JFrame frame;
    JPanel cards; //a panel that uses CardLayout
    final static String VIBRATE = "Vibrate Device";
    final static String INCOMING_CALL = "Incoming Call";
    final static String TTS = "Text To Speach";

    public EventLauncherPanel(DevicesAppState devicesAppState) {
        this.devicesAppState = devicesAppState;
    }

    public void itemStateChanged(ItemEvent evt) {
        CardLayout cl = (CardLayout) (cards.getLayout());
        cl.show(cards, (String) evt.getItem());
    }

    /**
     * Create the GUI and show it. For thread safety, this method should be
     * invoked from the event dispatch thread.
     */
    public static void createAndShowGUI(DevicesAppState devicesAppState) {
        //Create and set up the window.
        JFrame frame = new JFrame("CardLayoutDemo");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                windowEvent.getWindow().setVisible(false);
                windowEvent.getWindow().removeAll();
            }
        });
        //Create and set up the content pane.
        EventLauncherPanel demo = new EventLauncherPanel(devicesAppState);
        demo.addComponentToPane(frame.getContentPane());

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public void addComponentToPane(Container pane) {
        //Put the JComboBox in a JPanel to get a nicer look.
        JPanel comboBoxPane = new JPanel(); //use FlowLayout
        String comboBoxItems[] = {VIBRATE, INCOMING_CALL, TTS};
        JComboBox cb = new JComboBox(comboBoxItems);
        cb.setEditable(false);
        cb.addItemListener(this);
        comboBoxPane.add(cb);

        //Create the "cards".
        VibrateEventLauncherPanel vibrateEventLauncherPanel = new VibrateEventLauncherPanel(devicesAppState);

        IncomingCallEventLauncherPanel incomingCallEventLauncherPanel = new IncomingCallEventLauncherPanel(devicesAppState);

        TTSLauncherPanel tTSLauncherPanel = new TTSLauncherPanel(devicesAppState);

        //Create the panel that contains the "cards".
        cards = new JPanel(new CardLayout());
        cards.add(vibrateEventLauncherPanel, VIBRATE);
        cards.add(incomingCallEventLauncherPanel, INCOMING_CALL);
        cards.add(tTSLauncherPanel, TTS);

        pane.add(comboBoxPane, BorderLayout.PAGE_START);
        pane.add(cards, BorderLayout.CENTER);
    }

    public void setVisible(boolean visible) {
        frame.setVisible(visible);
    }

    public boolean isVisible() {
        return frame.isVisible();
    }
}
