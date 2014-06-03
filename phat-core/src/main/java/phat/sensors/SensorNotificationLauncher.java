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
package phat.sensors;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Pablo
 */
public class SensorNotificationLauncher {
    List<SensorListener> listeners;
    List<SensorNotificator> notificators = new ArrayList<SensorNotificator>();
    
    Sensor sensor;

    long counter = 0L;
    
    public SensorNotificationLauncher(List<SensorListener> listeners, Sensor sensor) {
        this.listeners = listeners;
        this.sensor = sensor;
    }
    
    
    @SuppressWarnings("empty-statement")
    public synchronized void notify(SensorData data) {
        counter++;
        //long t1 = System.currentTimeMillis();
        //while(!isFinished());
        /*long t2 = System.currentTimeMillis();
        long time = t2-t1;
        if(time > 0) {
            System.out.println(counter+": Waiting for notification: "+(t2-t1));
        }*/
        notificators.clear();
        for (SensorListener sl : listeners) {
            SensorNotificator sn = new SensorNotificator(sensor, data, sl);
            notificators.add(sn);
            sn.start();
        }
    }
    
    private boolean isFinished() {
        for(SensorNotificator sn: notificators) {
            if(!sn.isFinished) {
                return false;
            }
        }
        return true;
    }
}
