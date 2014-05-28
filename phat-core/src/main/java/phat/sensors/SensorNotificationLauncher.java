/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
