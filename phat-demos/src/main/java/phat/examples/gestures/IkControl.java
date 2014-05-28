/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.examples.gestures;

import com.jme3.animation.Bone;
import com.jme3.animation.Skeleton;
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

    public IkControl() {
        iterations = 10;
        threshold = 0.001f;
        maxChain = 1;
    }

    public IkControl(Skeleton s) {
        skeleton = s;
        iterations = 10;
        threshold = 0.001f;
        maxChain = 1;
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
        float scalar = 0.1f/count;

// The position of the bone that we are going to rotate
        Vector3f vBone = bone.getModelSpacePosition();

// Stop the calculation if we are closer than threshold
        if (target.distance(vBone) < threshold) {
            return;
        }

// The position of the bone that we want to match to the target
        Vector3f vEnd = tBone.getModelSpacePosition();

        Vector3f vTarget = target.subtract(vBone);
        Vector3f vEndBone = vEnd.subtract(vBone);

        Vector3f axis = vEndBone.cross(vTarget);
        axis.normalizeLocal();

        if (axis.equals(Vector3f.ZERO)) {
            return;
        }

        float angle = (float) Math.acos(vEndBone.dot(vTarget) / (vEndBone.length() * vTarget.length()))*scalar;

// Make sure we don’t rotate by a bad value
        if (Float.isNaN(angle)) {
            return;
        }

        Quaternion q1 = new Quaternion();
        q1.fromAngleAxis(angle, axis);
        //anglesRestriction(q1, FastMath.QUARTER_PI/2f, -FastMath.QUARTER_PI/2f, FastMath.QUARTER_PI/2f, 0, FastMath.QUARTER_PI/2f, -FastMath.QUARTER_PI/2f);
        //anglesRestriction(q1, 0.1f, 0, 0.1f, 0, 0.1f, 0);
        Quaternion q2 = new Quaternion();
        q2.fromAngleAxis(-angle, axis);
        //anglesRestriction(q2, FastMath.QUARTER_PI/2f, -FastMath.QUARTER_PI/2f, FastMath.QUARTER_PI/2f, 0, FastMath.QUARTER_PI/2f, -FastMath.QUARTER_PI/2f);
        //anglesRestriction(q2, 0.1f, 0, 0.1f, 0, 0.1f, 0);

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

// Go up the chain till we reach the root
        if (bone.getParent() != null && count < maxChain) {
            updateBone(target, tBone, bone.getParent(), 1, count + 1);
        }
    }

    float [] angles = new float[3];
    private void anglesRestriction(Quaternion q, float maxX, float minX, float maxY, float minY, float maxZ, float minZ) {
        q.toAngles(angles);
        
        if(angles[0] < minX) {
            angles[0] = minX;
        } else if(angles[0] > maxX) {
            angles[0] = maxX;
        }
        
        if(angles[1] < minY) {
            angles[1] = minY;
        } else if (angles[1] > maxY) {
            angles[1] = maxY;
        }
        
        if(angles[2] < minZ) {
            angles[2] = minZ;
        } else if(angles[2] > maxZ) {
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
