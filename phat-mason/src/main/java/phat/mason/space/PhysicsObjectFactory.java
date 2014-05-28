/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
