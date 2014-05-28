/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.body.control.physics;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.KinematicRagdollControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;

/**
 * This extends BetterCharacterControl that is a bullet character class. A
 * RigidBody with cylinder collision shape is used and its velocity is set
 * continuously, a ray test is used to check if the character is on the ground.
 *
 * The character keeps his own local coordinate system which adapts based on the
 * gravity working on the character so the character will always stand upright.
 *
 * Forces in the local x/z plane are dampened while those in the local y
 * direction are applied fully (e.g. jumping, falling).
 *
 * @author normenhansen
 * @author pablo campillo-sanchez
 */
public class PHATCharacterControl extends BetterCharacterControl implements Control {

    public PHATCharacterControl() {
        super(0.25f, 1.9f, 80f);
    }

    public PHATCharacterControl(float radius, float height, float mass) {
        super(radius, height, mass);
        setJumpForce(new Vector3f(0f, mass / 20f, 0f));
    }

    private KinematicRagdollControl getKinematicRagdollControl() {
        return spatial.getControl(KinematicRagdollControl.class);
    }
    Vector3f lastLocation = new Vector3f();
    float time = 0f;

    @Override
    public void update(float tpf) {
        super.update(tpf);
        if (isEnabled()) {
            if (location.distance(lastLocation) < 0.001f
                    && walkDirection.length() > 0.1f) {
                //setGravity(new Vector3f(0f, 1f, 0f));
                time += tpf;
                if (time > 0.5f) {
                    //setGravity(new Vector3f(0f, -0.1f, 0f));
                    jump = true;
                }
            } else {
                jump = false;
                //setGravity(new Vector3f(0f, -9.8f, 0f));
                time = 0f;
            }
            lastLocation.set(location);
        }
    }

    @Override
    public Control cloneForSpatial(Spatial spatial) {
        PHATCharacterControl control = new PHATCharacterControl(radius, height, mass);
        control.setJumpForce(jumpForce);
        return control;
    }

    public float getRadius() {
        return radius;
    }

    public Vector3f getLocation() {
        return location;
    }
}
