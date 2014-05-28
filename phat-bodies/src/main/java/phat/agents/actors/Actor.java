/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.agents.actors;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 *
 * @author Pablo
 */
public interface Actor {
    public void updateState(float tpf);
    public String getName();
    public Vector3f getLocation();
    public Node getNode();
    public void say(String text, float volume);
}
