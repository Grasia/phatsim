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
