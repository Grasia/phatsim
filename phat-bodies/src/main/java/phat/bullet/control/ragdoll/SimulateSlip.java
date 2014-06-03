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

import com.jme3.animation.AnimControl;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author pablo
 */
public class SimulateSlip extends RagdollTransitionControl {
    
    List<PhysicsRigidBody> objectsToPush = new ArrayList<PhysicsRigidBody>();
    float frictionFactor = 0f;
    
    public SimulateSlip(Node model) {
        super(model);
    }
    
    @Override
    public void initBody() {
        objectsToPush.add(kinematicRagdollControl.getBoneRigidBody("LeftToeBase"));
        objectsToPush.add(kinematicRagdollControl.getBoneRigidBody("LeftFoot"));
        objectsToPush.add(kinematicRagdollControl.getBoneRigidBody("LeftToeBase"));
        
        
        /*
        kinematicRagdollControl.getBoneRigidBody("RightToeBase").setFriction(frictionFactor);
        kinematicRagdollControl.getBoneRigidBody("RightFoot").setFriction(frictionFactor);
        kinematicRagdollControl.getBoneRigidBody("RightLeg").setFriction(frictionFactor);
        
        kinematicRagdollControl.getBoneRigidBody("LeftToeBase").setFriction(frictionFactor);
        kinematicRagdollControl.getBoneRigidBody("LeftFoot").setFriction(frictionFactor);
        kinematicRagdollControl.getBoneRigidBody("LeftLeg").setFriction(frictionFactor);*/
    }

    @Override
    public void applyPhysics(PhysicsSpace ps, float tpf) {
        /*Vector3f direction = characterControl.getWalkDirection();
        Vector3f speed = direction.divide(characterControl.getPhysicsSpace().getAccuracy());
        System.out.println("SPEED = "+speed);
        for (PhysicsRigidBody prb: objectsToPush) {
            prb.setLinearVelocity(new Vector3f(FastMath.rand.nextFloat(), 0f, FastMath.rand.nextFloat()).normalize().mult(3));
        }*/
    }
    
}
