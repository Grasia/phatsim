/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.sensors;

/**
 * Interface that have to implement a class that wants to listen a sensor.
 * 
 * @author pablo
 */
public interface SensorListener {
    public void update(Sensor source, SensorData sd);
    public void cleanUp();
}
