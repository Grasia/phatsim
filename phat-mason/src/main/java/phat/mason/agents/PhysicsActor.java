/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.mason.agents;

import java.util.Collection;
import phat.mason.space.PhysicsObject;
import sim.util.Double3D;

/**
 *
 * @author pablo
 */
public interface PhysicsActor extends PhysicsObject {
    public void moveTo(Double3D loc);
    public void moveTo(Double3D loc, float distance);
    public void stopMoving();
    public void playAnimation(String name);
    public String currentAnimName();
    public void showName(boolean showName);
    public void tripOver();
    public void slip();
    public void standUp();
    public Agent agent();
    public void putAgent(Agent agent);
    public Collection<String> animationName();
    public boolean hasAnimation(String animationName);
    public void say(String text, float volume);
    
    public String getCurrentAction();
}
