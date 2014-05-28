/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
