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
package phat.body.control.physics.ragdoll;

import com.jme3.animation.AnimControl;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;

/**
 *
 * @author pablo
 */
public class ApplyLinealVelocityAllBody extends RagdollTransitionControl {
    
    @Override
    public void initBody() {
        
    }

    @Override
    public void applyPhysics(PhysicsSpace ps, float tpf) {
        Vector3f direction = characterControl.getWalkDirection();
        Vector3f speed = direction.divide(characterControl.getPhysicsSpace().getAccuracy());
        for (int i = 0; i < animControl.getSkeleton().getBoneCount(); i++) {
            PhysicsRigidBody prb = kinematicRagdollControl.getBoneRigidBody(animControl.getSkeleton().getBone(i).getName());
            prb.setLinearVelocity(speed);
        }
    }

    @Override
    protected void createSpatialData(Spatial sptl) {
    }

    @Override
    protected void removeSpatialData(Spatial sptl) {
    }

    @Override
    protected void setPhysicsLocation(Vector3f vctrf) {
    }

    @Override
    protected void setPhysicsRotation(Quaternion qtrn) {
    }

    @Override
    public Control cloneForSpatial(Spatial sptl) {
        return new ApplyLinealVelocityAllBody();
    }
    
}
