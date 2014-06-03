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
package phat.mason.utils;

import java.awt.Font;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

/**
 *
 * @author Pablo
 */
public class AlarmFrame extends JFrame {
    
    public static void main(String [] args) {
        AlarmFrame f = new AlarmFrame();
        f.setVisible(true);
    }
    
    public AlarmFrame() {
        setTitle("System Alarm");
        
        JTextArea ta = new JTextArea("Time out: "+"The patient has been on the floor of the bathroom for 5 minutes!");        
        Font font = new Font("Verdana", Font.BOLD, 12);
        ta.setFont(font);
        
        getContentPane().add(ta);
        pack();
        try {
            ImageIcon ii = new ImageIcon(new URL("http://www.xlab.tv/images/Icon-Danger.gif"));
            JOptionPane.showMessageDialog(this, "Time out: "+"The patient has been on the floor of the bathroom for 5 minutes!", "System Alarm",-1, ii);
        } catch (MalformedURLException ex) {
            Logger.getLogger(AlarmFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        //setSize(550, 80);
    }
}
