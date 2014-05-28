/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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