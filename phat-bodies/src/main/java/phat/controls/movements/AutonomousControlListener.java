/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.controls.movements;

import com.jme3.math.Vector3f;

/**
 *
 * @author pablo
 */
public interface AutonomousControlListener {
    public void destinationReached(Vector3f destination);
}
