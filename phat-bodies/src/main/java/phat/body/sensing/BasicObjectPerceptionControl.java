/*
 * Copyright (C) 2014 pablo <pabcampi@ucm.es>
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
package phat.body.sensing;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import java.util.Observable;
import java.util.Observer;
import phat.body.control.physics.PHATCharacterControl;

/**
 *
 * @author pablo <pabcampi@ucm.es>
 */
public class BasicObjectPerceptionControl extends AbstractControl {
    
    float distance = 0.5f;
    float frecuency = 0.5f;
    float timer = frecuency;
    
    Node target;
    
    PerceptionNotificator observable;
    
    Vector3f loc1;
    Vector3f loc2;
    
    public BasicObjectPerceptionControl() {
        observable = new PerceptionNotificator();
    }
    
    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);

        if (spatial == null) {
            // Finish
        } else {
            // Init
            timer = frecuency;
        }
    }
    
    private Vector3f getLocation(Spatial s) {
        if(s.getControl(PHATCharacterControl.class) != null) {
            return s.getControl(PHATCharacterControl.class).getLocation();
        } else if(s.getControl(RigidBodyControl.class) != null) {
            return s.getControl(RigidBodyControl.class).getPhysicsLocation();
        } else {
            return s.getWorldTranslation();
        }
    }
    
    @Override
    protected void controlUpdate(float f) {
        if(timer <= 0) {
            timer = frecuency;
            if(hasBeenPerceived()) {
                observable.setChanged();
                observable.notifyObservers();
            }
        }
        timer -= f;
    }
    
    public void addObserver(Observer o) {
        observable.addObserver(o);
    }
    
    public void deleteObserver(Observer o) {
        observable.deleteObserver(o);
    }
    
    public int countObservers() {
        return observable.countObservers();
    }

    public boolean hasBeenPerceived() {
        if(getLocation(spatial).distance(getLocation(target)) <= distance) {
            return true;
        }
        return false;
    }

    public class PerceptionNotificator extends Observable {
        @Override
        public void setChanged() {
            super.setChanged();
        }
    }
    
    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public float getFrecuency() {
        return frecuency;
    }

    public void setFrecuency(float frecuency) {
        this.frecuency = frecuency;
    }

    public Node getTarget() {
        return target;
    }

    public void setTarget(Node target) {
        this.target = target;
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}
