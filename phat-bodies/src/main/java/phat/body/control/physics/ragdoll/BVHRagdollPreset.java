package phat.body.control.physics.ragdoll;

import com.jme3.bullet.control.ragdoll.RagdollPreset;
import com.jme3.bullet.joints.SixDofJoint;
import com.jme3.math.FastMath;

/**
 *
 * @author Pablo
 */
public class BVHRagdollPreset extends RagdollPreset {

    /**
     * @TODO calibrate free-degrees of each bone
     */
    @Override
    protected void initBoneMap() {        
        boneMap.put("Head", new JointPreset(0f,0f,0f,0f,0f,0f));
        boneMap.put("Jaw", new JointPreset(0f,0f,0f,0f,0f,0f));
        boneMap.put("RightShoulder", new JointPreset(0f,0f,0f,0f,0f,0f));
        boneMap.put("LeftShoulder", new JointPreset(0f,0f,0f,0f,0f,0f));
        boneMap.put("Jaw", new JointPreset(0f,0f,0f,0f,0f,0f));
        boneMap.put("Hips", new JointPreset(0f,0f,0f,0f,0f,0f));
        
        boneMap.put("Neck", new JointPreset(FastMath.QUARTER_PI, -FastMath.QUARTER_PI/2f, FastMath.QUARTER_PI, -FastMath.QUARTER_PI, FastMath.QUARTER_PI/2f, -FastMath.QUARTER_PI/2f));
        
        boneMap.put("RightArm", new JointPreset(FastMath.QUARTER_PI/2f, -FastMath.HALF_PI, FastMath.QUARTER_PI, 0, FastMath.HALF_PI, -FastMath.QUARTER_PI/2f));
        boneMap.put("RightForeArm", new JointPreset(FastMath.HALF_PI*1.2f, 0f, FastMath.QUARTER_PI, 0, 0f, 0f));
        boneMap.put("RightHand", new JointPreset(FastMath.QUARTER_PI, -FastMath.QUARTER_PI/2f, 0f, 0f, FastMath.QUARTER_PI/2f, -FastMath.QUARTER_PI/2f));
        
        boneMap.put("LeftArm", new JointPreset(FastMath.QUARTER_PI/2f, -FastMath.HALF_PI, FastMath.QUARTER_PI, 0, FastMath.HALF_PI, -FastMath.QUARTER_PI/2f));
        boneMap.put("LeftForeArm", new JointPreset(FastMath.HALF_PI*1.2f, 0f, FastMath.QUARTER_PI, 0, 0f, 0f));
        boneMap.put("LeftHand", new JointPreset(FastMath.QUARTER_PI, -FastMath.QUARTER_PI/2f, 0f, 0f, FastMath.QUARTER_PI/2f, -FastMath.QUARTER_PI/2f));
        
        //boneMap.put("Spine1", new JointPreset(-FastMath.QUARTER_PI/8f, -FastMath.QUARTER_PI/8f, FastMath.QUARTER_PI/4f, -FastMath.QUARTER_PI/4f, FastMath.QUARTER_PI/4f, -FastMath.QUARTER_PI/4f));
        //boneMap.put("Spine", new JointPreset(-FastMath.QUARTER_PI/8f, -FastMath.QUARTER_PI/8f, FastMath.QUARTER_PI/4f, -FastMath.QUARTER_PI/4f, FastMath.QUARTER_PI/4f, -FastMath.QUARTER_PI/4f));
        //boneMap.put("LowerBack", new JointPreset(-FastMath.QUARTER_PI/8f, -FastMath.QUARTER_PI/8f, FastMath.QUARTER_PI/4f, -FastMath.QUARTER_PI/4f, FastMath.QUARTER_PI/4f, -FastMath.QUARTER_PI/4f));
        
        boneMap.put("RightUpLeg", new JointPreset(FastMath.QUARTER_PI/2f, -FastMath.QUARTER_PI*1.6f, 0f, 0f, FastMath.QUARTER_PI, -FastMath.QUARTER_PI/2f));
        boneMap.put("RightLeg", new JointPreset(FastMath.HALF_PI*1.2f, 0f, 0f,0f,0f,0f));
        
        boneMap.put("LeftUpLeg", new JointPreset(FastMath.QUARTER_PI/2f, -FastMath.QUARTER_PI*1.6f, 0f, 0f, FastMath.QUARTER_PI, -FastMath.QUARTER_PI/2f));
        boneMap.put("LeftLeg", new JointPreset(FastMath.HALF_PI*1.2f, 0f, 0f,0f,0f,0f));
    }
    
    private float de2Rad(float degree) {
        return degree*FastMath.TWO_PI/360f;
    }

    private class LexiconEntry2 extends LexiconEntry {
        public LexiconEntry2() {
            super();
        }
    }
    
    public SixDofJoint getJointPreset(String boneName) {
        JointPreset jp = boneMap.get(boneName);
        if(jp != null) {
            SixDofJoint result = new SixDofJoint();
            jp.setupJoint(result);
            return result;  
        }
        return null;
    }
        
    @Override
    protected void initLexicon() {
        
        LexiconEntry entry = new LexiconEntry2();
        entry.addSynonym("Neck", 100);        
        lexicon.put("Neck", entry);

        entry = new LexiconEntry2();
        entry.addSynonym("RightHand", 100);
        entry.addSynonym("RightForeArm", 50); 
        entry.addSynonym("RightArm", 50);
        lexicon.put("RightHand", entry);
        
        entry = new LexiconEntry2();
        entry.addSynonym("LeftForeArm", 100); 
        entry.addSynonym("LeftArm", 50);
        lexicon.put("LeftForeArm", entry);
        
        entry = new LexiconEntry2(); 
        entry.addSynonym("LeftArm", 100);
        entry.addSynonym("LeftShoulder", 50);
        lexicon.put("LeftArm", entry);
        
        entry = new LexiconEntry2();
        entry.addSynonym("LeftShoulder", 100);
        lexicon.put("LeftShoulder", entry);
        
        entry = new LexiconEntry2();
        entry.addSynonym("LeftHand", 100);
        entry.addSynonym("LeftForeArm", 50); 
        entry.addSynonym("LeftArm", 50);
        lexicon.put("LeftHand", entry);
        
        
        //entry = new LexiconEntry2();
        //entry.addSynonym("Spine1", 100);
        //entry.addSynonym("Spine", 70);
        //entry.addSynonym("LowerBack", 50);
        //lexicon.put("Spine1", entry);
        
        
        entry = new LexiconEntry2();
        entry.addSynonym("LeftUpLeg", 100);
        entry.addSynonym("LeftLeg", 60);
        lexicon.put("LeftUpLeg", entry);

        entry = new LexiconEntry2();
        entry.addSynonym("RightUpLeg", 100);
        entry.addSynonym("RightLeg", 60);
        lexicon.put("RightUpLeg", entry);

        /*
        entry = new LexiconEntry();
        entry.addSynonym("LeftLeg", 100);
        entry.addSynonym("LeftUpLeg", 50);
        lexicon.put("LeftLeg", entry);
        
        entry = new LexiconEntry();
        entry.addSynonym("RightLeg", 100);
        entry.addSynonym("RightUpLeg", 50);
        lexicon.put("RightLeg", entry);
        
        entry = new LexiconEntry();
        entry.addSynonym("LeftFoot", 100);
        entry.addSynonym("LeftLeg", 75);   
        lexicon.put("LeftFoot", entry);
        
        entry = new LexiconEntry();
        entry.addSynonym("RightFoot", 100);
        entry.addSynonym("RightLeg", 75);   
        lexicon.put("RightFoot", entry);
        
        
        entry = new LexiconEntry();
        entry.addSynonym("upperarm", 100);
        entry.addSynonym("humerus", 100); 
        entry.addSynonym("shoulder", 50);
        entry.addSynonym("arm", 40);
        entry.addSynonym("high", 10);
        entry.addSynonym("up", 15);
        entry.addSynonym("upper", 15);
        lexicon.put("upperarm", entry);

        entry = new LexiconEntry();
        entry.addSynonym("lowerarm", 100);
        entry.addSynonym("ulna", 100);
        entry.addSynonym("elbow", 75);
        entry.addSynonym("arm", 50);
        entry.addSynonym("low", 10);
        entry.addSynonym("lower", 10);
        lexicon.put("lowerarm", entry);
        
        entry = new LexiconEntry();
        entry.addSynonym("hand", 100);
        entry.addSynonym("fist", 100);   
        entry.addSynonym("wrist", 75);           
        lexicon.put("hand", entry);*/
    }
    
}
