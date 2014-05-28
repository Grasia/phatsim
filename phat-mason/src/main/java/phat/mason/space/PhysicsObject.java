/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.mason.space;

import sim.field.continuous.Continuous3D;

/**
 *
 * @author Pablo
 */
public interface PhysicsObject extends Roll, SpaceLocation {
    public String getName();
    public Continuous3D world();
}
