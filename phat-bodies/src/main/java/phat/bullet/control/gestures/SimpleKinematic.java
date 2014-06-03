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
package phat.bullet.control.gestures;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;

/**
 *
 * @author pablo
 */
public class SimpleKinematic extends AbstractControl {

    Spatial target;
    
    Vector3f velocity;
    Vector3f rotation;
    
    public SimpleKinematic(Spatial target) {
        this.target = target;
    }
    
    float [] angles = new float[3];
    
    @Override
    protected void controlUpdate(float fps) {
        
        // update position and orientation
        Vector3f position = target.getLocalTranslation();
        target.setLocalTranslation(position.add(velocity.mult(fps)));
        
        Quaternion orientation = target.getLocalRotation();
        orientation.toAngles(angles);
        
        angles[0] += rotation.getX()*fps;
        angles[1] += rotation.getY()*fps;
        angles[2] += rotation.getZ()*fps;
        
        orientation.fromAngles(angles);

        // update the velocity and rotation
        //velocity = 
          RigidBodyControl rbc = new RigidBodyControl(10f);
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        
    }

    @Override
    public Control cloneForSpatial(Spatial sptl) {
        return new SimpleKinematic(target);
    }
    
}
