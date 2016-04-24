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
package phat.devices.actuators;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
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
public abstract class Actuator extends AbstractControl {

    public enum STATE {
        ON, OFF
    }
    STATE cState = STATE.OFF;
    
    protected String id;
    protected List<ActuatorListener> listeners = new ArrayList<>();

    public Actuator(String id) {
        super();
        this.id = id;
    }

    public STATE getState() {
        return cState;
    }
    
    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
    
    public void add(ActuatorListener actuatorListener) {
        if (!listeners.contains(actuatorListener)) {
            listeners.add(actuatorListener);
        }
    }

    public boolean hasListener(ActuatorListener sl) {
        return listeners.contains(sl);
    }
    
    public void remove(ActuatorListener actuatorListener) {
        listeners.remove(actuatorListener);
    }

    protected void notifyListeners() {
        System.out.println("notifyListener #"+listeners.size());
        for(ActuatorListener al: listeners) {
            al.stateChanged(this);
        }
    }
    
    protected Control cloneControl(Control control, Spatial spatial) {
        if (control instanceof Actuator) {
            Actuator sensor = (Actuator) control;
            sensor.setId(id);
        }
        return control;
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
    
    public void cleanUp() {
        
    }
}
