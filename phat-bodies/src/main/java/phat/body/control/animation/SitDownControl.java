package phat.body.control.animation;

import com.jme3.animation.Bone;
import com.jme3.animation.SkeletonControl;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
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
import java.io.IOException;
import phat.body.control.physics.PHATCharacterControl;
import phat.util.SpatialUtils;

/**
 * It generate a trembling in the head of a character.
 *
 * Depends on <b>SkeletonControl</b>
 *
 * @author pablo
 */
public class SitDownControl extends AbstractControl {

    SkeletonControl skeletonControl;
    Bone lowerBack;
    Bone leftLeg;
    Bone rightLeg;
    Bone root;
    Vector3f position = new Vector3f();
    Quaternion rotation = new Quaternion();
    float[] angles = new float[3];
    float angle = FastMath.HALF_PI;
    boolean min = true;

    Spatial seat;
    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);

        if (spatial != null) {
            skeletonControl = spatial.getControl(SkeletonControl.class);
            if (skeletonControl != null) {
                lowerBack = skeletonControl.getSkeleton().getBone("LowerBack");
                leftLeg = skeletonControl.getSkeleton().getBone("LeftLeg");
                rightLeg = skeletonControl.getSkeleton().getBone("RightLeg");
                root = skeletonControl.getSkeleton().getBone("Root");
                rotation = new Quaternion();
            }
        } else {
            resetBones();
            skeletonControl = null;
            rotation = null;            
        }
    }

    private void reset(Bone bone) {
        if (bone != null) {
            bone.setUserControl(true);
            bone.getCombinedTransform(position, rotation);
            angles[0] = 0;
            angles[1] = 0;
            angles[2] = 0;
            rotation.fromAngles(angles);
            bone.setUserTransforms(position, rotation, Vector3f.UNIT_XYZ);
            //updateBonePositions(bone);
        }
    }

    private void update(Bone bone, float fps) {
        if (bone != null) {
            bone.setUserControl(true);
            bone.getCombinedTransform(position, rotation);
            updateRotation(rotation, fps);
            bone.setUserTransforms(position, rotation, Vector3f.UNIT_XYZ);
            //updateBonePositions(bone);
            //setUserControlFrom(neck, false);
        }
    }

    private void resetBones() {
        reset(lowerBack);
        reset(leftLeg);
        reset(rightLeg);
        reset(root);
        lowerBack.setUserControl(false);
        leftLeg.setUserControl(false);
        rightLeg.setUserControl(false);
        root.setUserControl(false);
    }

    @Override
    protected void controlUpdate(float fps) {
        update(lowerBack, fps);
        update(leftLeg, fps);
        update(rightLeg, fps);
        angle = -angle;
        update(root, fps);
        angle = -angle;
        locateAndRotate();
    }
    
    public void standUp() {
        Spatial s = this.spatial;
        s.removeControl(this);
        s.setLocalTranslation(((Node)getSeat()).getChild("Access").getWorldTranslation());
        PHATCharacterControl cc = s.getControl(PHATCharacterControl.class);        
        cc.setEnabled(true);
        BasicCharacterAnimControl bcac = s.getControl(BasicCharacterAnimControl.class);
        bcac.setEnabled(true);
    }
    
    private void locateAndRotate() {
        Vector3f targetLoc = seat.getWorldTranslation();
        /*Quaternion targetRot = seat.getWorldRotation();
        float[] a = new float[3];
        targetRot.toAngles(a);
        a[1] = -a[1];
        targetRot.fromAngles(a);
        spatial.setLocalRotation(targetRot);*/
        //Vector3f center = SpatialUtils.getCenterBoinding(spatial);
        Node attach = skeletonControl.getAttachmentsNode("LowerBack");        
        Vector3f center = attach.getWorldTranslation();
        Vector3f currentLoc = spatial.getWorldTranslation();
        Vector3f lastLoc = currentLoc.add(targetLoc.subtract(center));
        lastLoc.addLocal(0f,0.1f,0f);
        spatial.setLocalTranslation(lastLoc);
    }
    private void updateRotation(Quaternion rotation, float tpf) {
        rotation.toAngles(angles);
        angles[1] = 0;
        angles[2] = 0;
        angles[0] = angle;

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

    public Spatial getSeat() {
        return seat;
    }

    public void setSeat(Spatial seat) {
        this.seat = seat;
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    @Override
    public Control cloneForSpatial(Spatial sptl) {
        SitDownControl control = new SitDownControl();
        control.setSpatial(sptl);
        control.setSeat(seat);
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