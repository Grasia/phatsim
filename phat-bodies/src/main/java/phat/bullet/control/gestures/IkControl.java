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
package phat.bullet.control.gestures;

import com.jme3.animation.Bone;
import com.jme3.animation.Skeleton;
import com.jme3.bullet.control.KinematicRagdollControl;
import com.jme3.bullet.joints.SixDofJoint;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import phat.bullet.control.ragdoll.BVHRagdollPreset;

/**
 *
 * @author pablo
 */
public class IkControl extends AbstractControl {

    private Bone targetBone = new Bone();
    private Bone firstBone = new Bone();
    private int maxChain;
    private Skeleton skeleton;
    private Node target;
// How close we have to be before giving up
    private float threshold;
// Number of times to attempt IK solution, more is slower but more accurate
    private int iterations;
    private BVHRagdollPreset bvhrp;

    public IkControl() {
        iterations = 10;
        threshold = 0.001f;
        maxChain = 1;

        bvhrp = new BVHRagdollPreset();
    }

    public IkControl(Skeleton s) {
        this();
        skeleton = s;
    }

    public void setMaxChain(int max) {
        maxChain = max;
    }

    public int getMaxChain() {
        return maxChain;
    }

    public void setSkeleton(Skeleton s) {
        skeleton = s;
    }

    public Skeleton getSkeleton() {
        return skeleton;
    }

    public void setIterations(int i) {
        iterations = i;
    }

    public int getIterations() {
        return iterations;
    }

    public void setThreshold(float newThreshold) {
        threshold = newThreshold;
    }

    public float getThreshold() {
        return threshold;
    }

    public void setTargetBone(Bone target) {
        targetBone = target;
    }

    public Bone getTargetBone() {
        return targetBone;
    }

    public void setFirstBone(Bone first) {
        firstBone = first;
    }

    public Bone getFirstBone() {
        return firstBone;
    }

    private void updateBonePositions(Bone bone) {
        Transform t = new Transform();
        for (Bone b : bone.getChildren()) {
            t = b.getCombinedTransform(bone.getModelSpacePosition(), bone.getModelSpaceRotation());
            b.setUserTransformsWorld(t.getTranslation(), b.getModelSpaceRotation());
            updateBonePositions(b);
        }
    }

    public void updateBone(Vector3f target, Bone tBone, Bone bone, float tpf, int count) {
        if (count < 1) {
            return;
        }

// The position of the bone that we are going to rotate
        Vector3f vBone = bone.getModelSpacePosition();
        
// The position of the bone that we want to match to the target
        Vector3f vEnd = tBone.getModelSpacePosition();
        
        // Stop the calculation if we are closer than threshold
        if (target.distance(vEnd) < threshold) {
            return;
        }
        
        Vector3f vTarget = target.subtract(vBone);
        Vector3f vEndBone = vEnd.subtract(vBone);

        Vector3f axis = vEndBone.cross(vTarget);
        axis.normalizeLocal();

        if (axis.equals(Vector3f.ZERO)) {
            return;
        }

        float angle = (float) Math.acos(vEndBone.dot(vTarget) / (vEndBone.length() * vTarget.length()));

// Make sure we don’t rotate by a bad value
        if (Float.isNaN(angle)) {
            return;
        }
        float maxX = 0f;
        float minX = 0f;
        float maxY = 0f;
        float minY = 0f;
        float maxZ = 0f;
        float minZ = 0f;

        SixDofJoint preset;
        KinematicRagdollControl krc = spatial.getControl(KinematicRagdollControl.class);
        
        if(krc == null) {
            return;
        }
        /*
        preset = krc.getJoint(bone.getName());
        //bvhrp.setupJointForBone(bone.getName(), preset);
        //bvhrp.getJointPreset(bone.getName());
        if (preset != null) {
            maxX = preset.getRotationalLimitMotor(0).getHiLimit();
            minX = preset.getRotationalLimitMotor(0).getLoLimit();
            maxY = preset.getRotationalLimitMotor(1).getHiLimit();
            minY = preset.getRotationalLimitMotor(1).getLoLimit();
            maxZ = preset.getRotationalLimitMotor(2).getHiLimit();
            minZ = preset.getRotationalLimitMotor(2).getLoLimit();
            //System.out.println("Limits "+bone.getName()+": "+maxX+", "+minX+", "+maxY+", "+minY+", "+maxZ+", "+minZ+", ");
        } else {
            System.out.println("Bone "+bone.getName()+" is equals to null");
        }
        
        */
        Quaternion q1 = new Quaternion();
        q1.fromAngleAxis(angle, axis);
        /*betweenPIandPI(q1);
        //anglesRestriction(q1, FastMath.QUARTER_PI/2f, -FastMath.QUARTER_PI/2f, FastMath.QUARTER_PI/2f, 0, FastMath.QUARTER_PI/2f, -FastMath.QUARTER_PI/2f);
        //anglesRestriction(q1, maxX, minX, maxY, minY, maxZ, minZ);
        */
        Quaternion q2 = new Quaternion();
        /*
        q2.fromAngleAxis(-angle, axis);
        //anglesRestriction(q2, FastMath.QUARTER_PI/2f, -FastMath.QUARTER_PI/2f, FastMath.QUARTER_PI/2f, 0, FastMath.QUARTER_PI/2f, -FastMath.QUARTER_PI/2f);
        betweenPIandPI(q2);
        //anglesRestriction(q2, maxX, minX, maxY, minY, maxZ, minZ);
        */
                
        Quaternion modelSpaceRotation = bone.getModelSpaceRotation().clone();

        bone.setUserTransformsWorld(vBone, modelSpaceRotation.mult(q1));
        updateBonePositions(bone);
        float dist1 = tBone.getModelSpacePosition().distance(target);
        bone.setUserTransformsWorld(vBone, modelSpaceRotation.mult(q2));
        updateBonePositions(bone);
        float dist2 = tBone.getModelSpacePosition().distance(target);

// We choose the orientation that makes distance smallest (also helps reduce popping for some reason)
        if (dist1 < dist2) {
            bone.setUserTransformsWorld(vBone, modelSpaceRotation.mult(q1));
        } else {
            bone.setUserTransformsWorld(vBone, modelSpaceRotation.mult(q2));
        }

        updateBonePositions(bone);
        
        //vEnd = tBone.getModelSpacePosition();
        //tBone.getModelSpaceRotation().lookAt(target.subtract(vEnd), Vector3f.UNIT_Y);
        //updateBonePositions(tBone);

// Go up the chain till we reach the root
        if (bone.getParent() != null && count < maxChain) {
            updateBone(target, tBone, bone.getParent(), 1, count + 1);
        }
    }
    float[] angles = new float[3];

    private void betweenPIandPI(Quaternion q) {
        q.toAngles(angles);
        
        for(int i = 0; i < 3; i++) {
            if(angles[i] < -FastMath.PI) {
                angles[i] += 2*FastMath.PI;
            } else if(angles[i] > FastMath.PI) {
                angles[i] -= 2*FastMath.PI;
            }
        }
        
        q.fromAngles(angles);
    }
    
    private void anglesRestriction(Quaternion q, float maxX, float minX, float maxY, float minY, float maxZ, float minZ) {
        q.toAngles(angles);
        
        if (angles[0] < minX) {
            angles[0] = minX;
        } else if (angles[0] > maxX) {
            angles[0] = maxX;
        }

        if (angles[1] < minY) {
            angles[1] = minY;
        } else if (angles[1] > maxY) {
            angles[1] = maxY;
        }

        if (angles[2] < minZ) {
            angles[2] = minZ;
        } else if (angles[2] > maxZ) {
            angles[2] = maxZ;
        }

        q.fromAngles(angles);
    }

    public void setTarget(Node node) {
        target = node;
    }

    public Node getTarget() {
        return target;
    }

    public void solveIk() {
        for (int i = 0; i < skeleton.getBoneCount(); i++) {
            skeleton.getBone(i).setUserControl(true);
        }

        Vector3f targetLocal = spatial.worldToLocal(target.getWorldTranslation(), null);
        for (int i = 0; i < iterations; i++) {
            updateBone(targetLocal, targetBone, firstBone, 1, 1);
        }
        
        //vEnd = tBone.getModelSpacePosition();
        //tBone.getModelSpaceRotation().lookAt(target.subtract(vEnd), Vector3f.UNIT_Y);
        //updateBonePositions(tBone);
        
        /*targetBone.setUserTransforms(
                Vector3f.ZERO, 
                Quaternion.IDENTITY.fromAngles(0, FastMath.HALF_PI, 0f), 
                Vector3f.UNIT_XYZ);
        updateBonePositions(targetBone);*/

        for (int i = 0; i < skeleton.getBoneCount(); i++) {
            skeleton.getBone(i).setUserControl(false);
        }
    }

    @Override
    protected void controlUpdate(float tpf) {
        solveIk();
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    public Control cloneForSpatial(Spatial spatial) {
        IkControl control = new IkControl();
        control.setSkeleton(skeleton);
        control.setSpatial(spatial);
        return control;
    }
}