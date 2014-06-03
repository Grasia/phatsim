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
package phat.bullet.control.ragdoll;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.control.KinematicRagdollControl;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Vector3f;
import java.util.List;

/**
 *
 * @author Pablo
 */
public class PushControl implements PhysicsTickListener {
    private List<PhysicsRigidBody> physicsRigidBodies;
    private Vector3f force;
    private KinematicRagdollControl kinematicRagdollControl;
    
    public PushControl(List<PhysicsRigidBody> physicsRigidBodies, Vector3f force, KinematicRagdollControl kinematicRagdollControl) {
        this.physicsRigidBodies = physicsRigidBodies;
        this.force = force;
        this.kinematicRagdollControl = kinematicRagdollControl;
        kinematicRagdollControl.getPhysicsSpace().addTickListener(this);
    }
    
    
    @Override
    public void prePhysicsTick(PhysicsSpace ps, float f) {
        kinematicRagdollControl.setEnabled(true);
        kinematicRagdollControl.setRagdollMode();
        for(PhysicsRigidBody prb: physicsRigidBodies) {
            prb.setLinearVelocity(force);
            //prb.applyImpulse(force.mult(prb.getMass()), Vector3f.ZERO);
        }
    }

    @Override
    public void physicsTick(PhysicsSpace ps, float f) {
        kinematicRagdollControl.getPhysicsSpace().removeTickListener(this);
    }
    
}
