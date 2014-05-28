/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.bullet.control.gestures;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;

/**
 *
 * @author pablo
 */
public class SimpleKinematic extends AbstractControl {

    Spatial target;
    
    Vector3f velocity;
    Vector3f rotation;
    
    public SimpleKinematic(Spatial target) {
        this.target = target;
    }
    
    float [] angles = new float[3];
    
    @Override
    protected void controlUpdate(float fps) {
        
        // update position and orientation
        Vector3f position = target.getLocalTranslation();
        target.setLocalTranslation(position.add(velocity.mult(fps)));
        
        Quaternion orientation = target.getLocalRotation();
        orientation.toAngles(angles);
        
        angles[0] += rotation.getX()*fps;
        angles[1] += rotation.getY()*fps;
        angles[2] += rotation.getZ()*fps;
        
        orientation.fromAngles(angles);

        // update the velocity and rotation
        //velocity = 
          RigidBodyControl rbc = new RigidBodyControl(10f);
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        
    }

    @Override
    public Control cloneForSpatial(Spatial sptl) {
        return new SimpleKinematic(target);
    }
    
}
