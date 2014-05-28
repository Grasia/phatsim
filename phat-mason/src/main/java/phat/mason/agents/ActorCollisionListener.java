/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.mason.agents;

import phat.mason.space.PhysicsObject;

/**
 *
 * @author Pablo
 */
public interface ActorCollisionListener {
    public void collision(PhysicsActor pa, PhysicsObject object);
}
