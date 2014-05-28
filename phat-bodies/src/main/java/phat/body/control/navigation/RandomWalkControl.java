/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.body.control.navigation;

import com.jme3.ai.steering.Obstacle;
import com.jme3.ai.steering.behaviour.ObstacleAvoid;
import com.jme3.ai.steering.behaviour.Persuit;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Plane;
import com.jme3.math.Plane.Side;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import phat.body.BodiesAppState;
import phat.body.control.physics.PHATCharacterControl;
import phat.util.SpatialFactory;
import phat.util.SpatialUtils;

/**
 *
 * @author Pablo
 */
public class RandomWalkControl extends AbstractControl {

    private PHATCharacterControl characterControl;

    public RandomWalkControl() {
        super();
    }
    
    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);

        if (spatial != null) {
            characterControl = spatial.getControl(PHATCharacterControl.class);
            
            characterControl.setWalkDirection(Vector3f.UNIT_Z);
        }
    }

    private Vector3f walkDir = new Vector3f();
    
    @Override
    protected void controlUpdate(float tpf) {
        if (!characterControl.isEnabled()) {
            return;
        }
        
        if(FastMath.rand.nextDouble() < 0.02f) {
            walkDir.set(characterControl.getViewDirection());
            walkDir.addLocal(0.02f, 0f, 0f).normalizeLocal();
            characterControl.setWalkDirection(walkDir.multLocal(getSpeed(spatial)));
            characterControl.setViewDirection(walkDir);
        }
    }
     

    private float getSpeed(Spatial spatial) {
        return spatial.getUserData("Speed");
    }

    @Override
    public Control cloneForSpatial(Spatial sptl) {
        RandomWalkControl smc = new RandomWalkControl();
        smc.setSpatial(sptl);
        return smc;
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule oc = ex.getCapsule(this);

    }

    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule ic = im.getCapsule(this);
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        
    }
}
