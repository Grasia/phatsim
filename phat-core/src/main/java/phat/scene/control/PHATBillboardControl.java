/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.scene.control;

import com.jme3.bounding.BoundingVolume;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.BillboardControl;
import phat.util.SpatialUtils;

/**
 *
 * @author pablo
 */
public class PHATBillboardControl extends BillboardControl {
    Vector3f offset = new Vector3f(0f, 0.5f, 0f);
    Vector3f center = new Vector3f();
    Vector3f loc = new Vector3f();
    Spatial head;
    Quaternion rotation = new Quaternion();
    
    boolean updatedLoc = false;
    
    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
    }
    
    public void update(float fps) {
        super.update(fps);
        updateLocation(spatial);
    }
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        super.controlRender(rm, vp);
    }

    float [] angles = new float[3];
    
    private void updateLocation(Spatial spatial) {
        if(head == null) {
            head = spatial.getParent().getParent().getChild("male/head/middle_aged");
        }
        loc.set(spatial.getParent().getWorldTranslation());
        center.set(SpatialUtils.getCenterBoinding(head));
        //System.out.println("Human Loc = "+loc+", head Loc = "+center);
        center.subtractLocal(loc);
        center.addLocal(offset);
        spatial.setLocalTranslation(center);
        
        rotation.set(spatial.getParent().getParent().getLocalRotation());
        rotation.toAngles(angles);
        angles[0] *= -1;
        angles[1] *= -1;
        angles[2] *= -1;
        rotation.fromAngles(angles);        
        
        spatial.getParent().setLocalRotation(rotation);
    }

    public Vector3f getOffset() {
        return offset;
    }

    public void setOffset(Vector3f offset) {
        this.offset = offset;
    }
}
