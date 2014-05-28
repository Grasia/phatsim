/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.mason.space;

/**
 *
 * @author Pablo
 */
public interface RigidPhysicsObject extends PhysicsObject {
    public void setMass(float mass);
    public void setFriction(float friction);
}
