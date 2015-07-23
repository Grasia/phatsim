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

package phat.sensors.camera;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import phat.sensors.Sensor;
import phat.sensors.SensorData;
import phat.sensors.SensorListener;

/**
 *
 * @author Pablo
 */
public class CameraSensorListenerFrame extends JPanel implements SensorListener {

    BufferedImage image;
    Object imageMutex = new Object();
    
    public CameraSensorListenerFrame() {
        super();

        setVisible(true);
        setSize(1280, 720);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        synchronized (imageMutex) {
            g.drawImage(image, 0, 0, null); // see javadoc for more info on the parameters
        }
    }
    int c = 0;

    @Override
    public void update(Sensor source, SensorData sd) {
        if (source instanceof CameraSensor && sd instanceof CameraSensorData) {
            CameraSensor cs = (CameraSensor) source;
            CameraSensorData csd = (CameraSensorData) sd;

            setSize(csd.getWidth(), csd.getHeigh());

            synchronized (imageMutex) {
                image = csd.getImage();
                repaint();
            }
        }
    }

    @Override
    public void cleanUp() {
        
    }
}
