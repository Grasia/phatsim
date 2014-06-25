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

import com.jme3.animation.AnimControl;
import phat.body.control.parkinson.*;
import com.jme3.animation.Bone;
import com.jme3.animation.SkeletonControl;
import com.jme3.bullet.control.KinematicRagdollControl;
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
import phat.util.Debug;
import phat.util.SpatialFactory;
import phat.util.SpatialUtils;

/**
 * It generate a trembling in the head of a character.
 *
 * Depends on <b>SkeletonControl</b>
 *
 * @author pablo
 */
public class LookAtControl extends AbstractControl {
    private static float LEFT_LIMIT_ANGLE = FastMath.HALF_PI;
    private static float RIGHT_LIMIT_ANGLE = FastMath.HALF_PI;
    private static float DOWN_LIMIT_ANGLE = FastMath.QUARTER_PI;
    private static float UP_LIMIT_ANGLE = FastMath.HALF_PI;
    
    SkeletonControl skeletonControl;
    Bone neck;
    Vector3f position = new Vector3f();
    Quaternion rotation = new Quaternion();
    float[] angles = new float[3];
    int index = 2;
    // Rotation on Y
    float leftFactorLimit = 0.9f;       // leftFactorLimit * FastMath.HALF_PI
    float rightFactorLimit = 0.9f;      // rightFactorLimit * FastMath.HALF_PI
    // Rotation on X
    float downFactorLimit = 1f;         // downFactorLimit * FastMath.QUARTER_PI
    float upFactorLimit = 1f;           // upFactorLimit * FastMath.HALF_PI
    
    float angularFactor = 1f;           // angularFactor * FastMath.PI
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
                //Debug.attachLocalCoordinateAxes(skeletonControl.getAttachmentsNode("Neck"), 0.5f, SpatialFactory.getAssetManager(), SpatialFactory.getRootNode());
                //startTest();
            }
        } else {
            resetHead();
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
        Node node = skeletonControl.getAttachmentsNode("Neck");
        node.attachChild(directionGeo);
        //Vector3f headPos =
        //        skeletonControl.getSkeleton().getBone("Head").getModelSpacePosition();
        //directionGeo.setLocalTranslation(headPos);
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
        }
        //showDirection();
    }
    
    public void setTarget(Spatial target) {
        this.target = target;
    }
    
    Quaternion rotLookAt = new Quaternion();
    Quaternion aux = new Quaternion();
    
    public Quaternion getWorldRotation(Bone b) {
        return null;
    }
    
    Transform locTrans = new Transform();
    Transform parentTrans = new Transform();
    Vector3f locDir = new Vector3f();
    
    Geometry geo;
    private void updateNeck(Quaternion rot, float tpf) {
        locTrans.setRotation(neck.getModelSpaceRotation());
        locTrans.setTranslation(neck.getModelSpacePosition());
        locTrans.setScale(Vector3f.UNIT_XYZ);
        
        parentTrans.setRotation(spatial.getLocalRotation());
        parentTrans.setTranslation(spatial.getLocalTranslation());
        parentTrans.setScale(spatial.getLocalScale());
        
        locTrans.combineWithParent(parentTrans);
        
        Vector3f point = SpatialUtils.getCenterBoinding(target);
        Vector3f tDir = point.subtract(locTrans.getTranslation()).normalize();
        //Vector3f cDir = locTrans.getRotation().mult(Vector3f.UNIT_Z).normalize();
        
        /*if(geo != null)
            geo.removeFromParent();
        geo = SpatialFactory.createArrow(tDir, 4f, ColorRGBA.Green);
        SpatialUtils.getRootNode(spatial).attachChild(geo);
        geo.setLocalTranslation(locTrans.getTranslation());*/
        
        rotation.lookAt(tDir, Vector3f.UNIT_Y);
        parentTrans.getRotation().inverseLocal();
        parentTrans.getRotation().mult(rotation, rotation);
        applyLimitations(rotation);
    }
    
    public void applyLimitations(Quaternion rotation) {
        rotation.toAngles(angles);
        float upLimit = -upFactorLimit*UP_LIMIT_ANGLE;
        float downLimit = downFactorLimit*DOWN_LIMIT_ANGLE;
        float leftLimit = leftFactorLimit*LEFT_LIMIT_ANGLE;
        float rightLimit = -rightFactorLimit*RIGHT_LIMIT_ANGLE;
        if(angles[0] < upLimit) {
            angles[0] = upLimit;
        } else if(angles[0] > downLimit) {
            angles[0] = downLimit;
        }
        if(angles[1] > leftLimit) {
            angles[1] = leftLimit;
        } else if(angles[1] < rightLimit) {
            angles[1] = rightLimit;
        }
        rotation.fromAngles(angles);
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

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    @Override
    public Control cloneForSpatial(Spatial sptl) {
        HeadTremblingControl control = new HeadTremblingControl();
        control.setSpatial(sptl);
        return control;
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
}