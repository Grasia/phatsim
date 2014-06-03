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

import com.jme3.bullet.control.PhysicsControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import sim.field.continuous.Continuous3D;

/**
 *
 * @author Pablo
 */
public class PhysicsObjectFactory {

    public static PhysicsObject createPhysicsObjectFrom(Spatial spatial, Continuous3D world) {
        PhysicsControl rbc = spatial.getControl(PhysicsControl.class);
        if (rbc == null) {
            return null;
        }

        if (rbc instanceof RigidBodyControl) {
            RigidPhysicsObjectImpl po = new RigidPhysicsObjectImpl(world);
            spatial.addControl(po);
            return po;
        }

        return null;
    }

    public static Vector3f getLocation(Spatial spatial) {
        PhysicsControl rbc = spatial.getControl(PhysicsControl.class);
        if (rbc != null) {
            if (rbc instanceof RigidBodyControl) {
                RigidBodyControl po = (RigidBodyControl) rbc;
                return po.getPhysicsLocation();
            }
        }
        return spatial.getWorldTranslation();
    }
}
