/*
 * Copyright (C) 2014 Pablo Campillo-Sanchez <pabcampi@ucm.es>
 *
 * This software has been developed as part of the 
 * SociAAL project directed by Jorge J. Gomez Sanz
 * (http://grasia.fdi.ucm.es/sociaal)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package phat.body.control.animation;

import phat.body.control.parkinson.*;
import com.jme3.animation.Bone;
import com.jme3.animation.SkeletonControl;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import com.jme3.scene.debug.Arrow;
import java.io.IOException;
import phat.util.SpatialFactory;

/**
 * It generate a trembling in the head of a character.
 *
 * Depends on <b>SkeletonControl</b>
 *
 * @author pablo
 */
public class LookAtControl extends AbstractControl {

    SkeletonControl skeletonControl;
    Bone neck;
    Vector3f position = new Vector3f();
    Quaternion rotation = new Quaternion();
    float[] angles = new float[3];
    int index = 2;
    float minAngle = -FastMath.QUARTER_PI / 4f;
    float maxAngle = FastMath.QUARTER_PI / 4f;
    float angular = FastMath.PI;
    boolean min = true;
    Spatial target;
    Geometry directionGeo;

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);

        if (spatial != null) {
            skeletonControl = spatial.getControl(SkeletonControl.class);
            if (skeletonControl != null) {
                neck = skeletonControl.getSkeleton().getBone("Neck");
                rotation = new Quaternion();
                startTest();
            }
        } else {
            skeletonControl = null;
            rotation = null;
        }
    }

    private void showDirection() {
        if (directionGeo == null) {
            Spatial eyes = ((Node) spatial).getChild("universal/eyes/blue");
            Arrow arrow = new Arrow(Vector3f.UNIT_Z.mult(0.5f));
            arrow.setLineWidth(2); // make arrow thicker                            
            directionGeo = SpatialFactory.createShape("StraightPath", arrow, ColorRGBA.Red);
            ((Node)spatial).attachChild(directionGeo);
        }
        Node node = skeletonControl.getAttachmentsNode("Head");
        node.attachChild(directionGeo);
        //Vector3f headPos =
        //        skeletonControl.getSkeleton().getBone("Head").getModelSpacePosition();
        //directionGeo.setLocalTranslation(headPos);
    }
    
    private void startTest() {
        Spatial s = SpatialFactory.createCube(Vector3f.UNIT_XYZ.mult(0.1f), ColorRGBA.Blue);
        this.target = s;
        s.setLocalTranslation(Vector3f.UNIT_Z.add(-1f, 2.0f, 0f));
        MoveArroundControl mac = new MoveArroundControl();
        mac.setTarget(this.spatial);
        s.addControl(mac);
        ((Node)spatial).attachChild(s);        
    }
    
    class MoveArroundControl extends AbstractControl {
        Spatial target;
        
        @Override
        protected void controlUpdate(float f) {
            if(target != null) {
                
            }
        }
        
        public void setTarget(Spatial target) {
            this.target = target;
        }
        
        public Spatial getTarget() {
            return this.target;
        }

        @Override
        protected void controlRender(RenderManager rm, ViewPort vp) {
            
        }
        
    }
    
    private void resetHead() {
        if (neck != null) {
            setUserControlFrom(neck, true);
            neck.getCombinedTransform(position, rotation);
            angles[0] = 0;
            angles[1] = 0;
            angles[2] = 0;
            rotation.fromAngles(angles);
            neck.setUserTransforms(position, rotation, Vector3f.UNIT_XYZ);
            updateBonePositions(neck);
        }
    }

    @Override
    protected void controlUpdate(float fps) {
        if (neck != null) {
            setUserControlFrom(neck, true);
            neck.getCombinedTransform(position, rotation);
            updateNeck(rotation, fps);
            neck.setUserTransforms(position, rotation, Vector3f.UNIT_XYZ);
            updateBonePositions(neck);
            //setUserControlFrom(neck, false);
        }
        showDirection();
    }

    private void updateNeck(Quaternion rotation, float tpf) {
        rotation.lookAt(target.getWorldTranslation(), Vector3f.UNIT_Y);    
        //rotation.set(neck.getWorldBindRotation());    
        //rotation.lookAt(target.getWorldTranslation(), Vector3f.UNIT_Y);
    }

    private void updateBonePositions(Bone bone) {
        Transform t = new Transform();
        for (Bone b : bone.getChildren()) {
            t = b.getCombinedTransform(bone.getModelSpacePosition(), bone.getModelSpaceRotation());
            b.setUserTransformsWorld(t.getTranslation(), b.getModelSpaceRotation());
            updateBonePositions(b);
        }
    }

    private void setUserControlFrom(Bone bone, boolean userControl) {
        bone.setUserControl(userControl);
        for (Bone b : bone.getChildren()) {
            setUserControlFrom(b, userControl);
        }
    }

    public float getMinAngle() {
        return minAngle;
    }

    public void setMinAngle(float minAngle) {
        this.minAngle = minAngle;
    }

    public float getMaxAngle() {
        return maxAngle;
    }

    public void setMaxAngle(float maxAngle) {
        this.maxAngle = maxAngle;
    }

    public float getAngular() {
        return angular;
    }

    public void setAngular(float angular) {
        this.angular = angular;
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    @Override
    public Control cloneForSpatial(Spatial sptl) {
        HeadTremblingControl control = new HeadTremblingControl();
        control.setSpatial(sptl);
        control.setAngular(angular);
        control.setMaxAngle(maxAngle);
        control.setMinAngle(minAngle);
        return control;
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule oc = ex.getCapsule(this);
        oc.write(angular, "angular", FastMath.PI);
        oc.write(maxAngle, "maxAngle", FastMath.QUARTER_PI / 4f);
        oc.write(minAngle, "minAngle", -FastMath.QUARTER_PI / 4f);

    }

    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule ic = im.getCapsule(this);
        angular = ic.readFloat("angular", FastMath.PI);
        maxAngle = ic.readFloat("maxAngle", FastMath.QUARTER_PI / 4f);
        minAngle = ic.readFloat("minAngle", -FastMath.QUARTER_PI / 4f);
    }
}