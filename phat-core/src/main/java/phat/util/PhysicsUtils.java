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
package phat.util;

import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.PhysicsControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;

/**
 *
 * @author pablo
 */
public class PhysicsUtils {

    public static void setHighPhysicsPrecision(Spatial spatial) {
        final float linear = 0.0001f;
        final float angular = 0.0001f;
        final float threshold = 0.0001f;
        final float radius = 1f;

        SceneGraphVisitor visitor = new SceneGraphVisitor() {
            @Override
            public void visit(Spatial spat) {
                RigidBodyControl rbc = spat.getControl(RigidBodyControl.class);
                if (rbc != null) {
                    rbc.setSleepingThresholds(linear, angular);
                    rbc.setCcdMotionThreshold(threshold);
                    rbc.setCcdSweptSphereRadius(radius);
                }
            }
        };
        spatial.depthFirstTraversal(visitor);
    }

    public static void addAllPhysicsControls(Spatial spatial, BulletAppState bulletAppState) {
        for (int i = 0; i < spatial.getNumControls(); i++) {
            if (spatial.getControl(i) instanceof PhysicsControl) {
                bulletAppState.getPhysicsSpace().add(spatial.getControl(i));
            }
        }
    }
    
    public static void updateLocationAndRotation(Spatial spatial) {
        SceneGraphVisitor visitor = new SceneGraphVisitor() {
            @Override
            public void visit(Spatial spat) {
                RigidBodyControl rbc = spat.getControl(RigidBodyControl.class);
                if (rbc != null) {
                    rbc.setPhysicsLocation(spat.getWorldTranslation());
                    rbc.setPhysicsRotation(spat.getWorldRotation());
                }
            }
        };
        spatial.depthFirstTraversal(visitor);
    }
}
