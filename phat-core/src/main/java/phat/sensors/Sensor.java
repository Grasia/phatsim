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

import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class to implement a sensor's behaviour
 *
 * @author pablo
 */
public abstract class Sensor extends AbstractControl {

    protected String id;
    protected List<SensorListener> listeners;
    SensorNotificationLauncher launcher;

    public Sensor(String id) {
        super();
        this.id = id;
        this.listeners = new ArrayList<SensorListener>();
        this.launcher = new SensorNotificationLauncher(listeners, this);
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    /**
     * Adds a sensor listener and the sensor is enabled if it was disabled
     * in order to feed data to the listener
     * 
     * @param sensorListener 
     */
    public void add(SensorListener sensorListener) {
        if (!listeners.contains(sensorListener)) {
            listeners.add(sensorListener);
            if (!isEnabled()) {
                setEnabled(true);
            }
        }
    }

    public boolean hasListener(SensorListener sl) {
        return listeners.contains(sl);
    }

    /**
     * Removes a listener and if there are not more listener
     * the sensor is disabled in order to save resources
     * 
     * @param sensorListener 
     */
    public void remove(SensorListener sensorListener) {
        listeners.remove(sensorListener);
        if (listeners.isEmpty() && isEnabled()) {
            setEnabled(false);
        }
    }

    @SuppressWarnings("empty-statement")
    protected void notifyListeners(SensorData sourceData) {
        this.launcher.notify(sourceData);
    }

    /*protected void notifyListeners(SensorData sourceData) {
     for (SensorListener sl : listeners) {
     sl.update(this, sourceData);
     }
     }*/
    protected Control cloneControl(Control control, Spatial spatial) {
        if (control instanceof Sensor) {
            Sensor sensor = (Sensor) control;
            sensor.setId(id);

            sensor.listeners.addAll(listeners);
        }
        return control;
    }

    public void cleanUp() {
        for (SensorListener sl : listeners) {
            sl.cleanUp();
        }
        listeners.clear();
    }
}
