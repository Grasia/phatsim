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
package phat.mason.space;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import sim.field.continuous.Continuous3D;
import sim.util.Double3D;

/**
 *
 * @author Pablo
 */
public class RigidPhysicsObjectImpl extends AbstractControl implements RigidPhysicsObject {
    private RigidBodyControl rigidBodyControl;
    private Continuous3D world;
    
    public RigidPhysicsObjectImpl(Continuous3D world) {
        this.world = world;
    }
    
    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        if(spatial != null) {
            this.rigidBodyControl = spatial.getControl(RigidBodyControl.class);
        }
    }
    
    @Override
    public String getName() {
        return spatial.getName();
    }
    
    public void setName(String name) {
        spatial.setName(name);
    }

    @Override
    public String getRoll() {
        return spatial.getUserData("Roll");
    }
    
    public void setRoll(String roll) {
        spatial.setUserData("Roll", roll);
    }

    @Override
    public Double3D getLocation() {
        //Vector3f location = rigidBodyControl.getPhysicsLocation();
        //return Util.get(location);
        return world.getObjectLocation(this);
    }

    @Override
    public void setMass(float mass) {
        rigidBodyControl.setMass(mass);
    }

    @Override
    public void setFriction(float friction) {
        rigidBodyControl.setFriction(friction);
    }

    public void setWorld(Continuous3D world) {
        this.world = world;
        world.setObjectLocation(this, getLocation());
    }
    
    @Override
    public Continuous3D world() {
        return world;
    }

    @Override
    protected void controlUpdate(float f) {
        updateMASONWorldLoaction();
    }

    private void updateMASONWorldLoaction() {
        Double3D loc = Util.get(rigidBodyControl.getPhysicsLocation());
        if(loc != null && loc.distance(getLocation()) >= world.discretization) {
            world.setObjectLocation(this, loc);
        }
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    @Override
    public Control cloneForSpatial(Spatial sptl) {
        RigidPhysicsObjectImpl rpoi = new RigidPhysicsObjectImpl(world);
        return rpoi;
    }
    
    @Override
    public String toString() {
        return getName();
    }
}
