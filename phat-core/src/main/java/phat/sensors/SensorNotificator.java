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

/**
 *
 * @author Pablo
 */
public class SensorNotificator extends Thread {
    Sensor sensor;
    SensorData data;
    SensorListener sensorListener;
    
    boolean isFinished = false;
    
    public SensorNotificator(Sensor sensor, SensorData data, SensorListener sensorListener) {
        this.sensor = sensor;
        this.data = data;
        this.sensorListener = sensorListener;
    }

    @Override
    public void run() {
        sensorListener.update(sensor, data);
        isFinished = true;
    }
    
    public boolean isFinished() {
        return isFinished;
    }
}