/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.agents.actors;

import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.Bone;
import com.jme3.animation.SkeletonControl;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.RagdollCollisionListener;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.KinematicRagdollControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.font.BitmapText;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import phat.audio.AudioFactory;
import phat.audio.AudioSpeakerSource;
import phat.audio.textToSpeech.TextToSpeechManager;
import phat.bullet.control.ragdoll.ApplyLinealVelocityAllBody;
import phat.bullet.control.ragdoll.FallDownSound;
import phat.bullet.control.ragdoll.PushControl;
import phat.bullet.control.ragdoll.RagdollTransitionControl;
import phat.bullet.control.ragdoll.SimulateSlip;
import phat.bullet.control.ragdoll.SimulateTripOver;
import phat.controls.animation.BasicCharacterAnimControl;
import phat.controls.movements.AutonomousMovementControl;
import phat.controls.movements.MovementControl;
import phat.controls.movements.StraightMovementControl;
import phat.util.SpatialFactory;

/**
 *
 * @author Pablo
 */
public class BasicActor implements Actor {

    private Node model;
    private CharacterControl characterControl;
    private AutonomousMovementControl autonomousMovementControl;
    private BasicCharacterAnimControl basicCharacterAnimControl;
    private KinematicRagdollControl kinematicRagdollControl;
    private RagdollTransitionControl ragdollTransitionControl;
    private BitmapText showedName;
    protected AudioSpeakerSource speaker;
    
    public BasicActor(Node actorModel) {
        this.model = actorModel;

        characterControl = ActorFactory.findControl(model, CharacterControl.class);
        autonomousMovementControl = ActorFactory.findControl(model, AutonomousMovementControl.class);
        basicCharacterAnimControl = ActorFactory.findControl(model, BasicCharacterAnimControl.class);
        kinematicRagdollControl = ActorFactory.findControl(model, KinematicRagdollControl.class);

        ragdollTransitionControl = new SimulateTripOver(model);
        // TODO esto deberia ir en la clase SimulateTripOver
        addFrictionToBones(kinematicRagdollControl, 0.6f);
    }

    public void setRagdollTransitionControl(RagdollTransitionControl rtc) {
        this.ragdollTransitionControl = rtc;
    }

    private void addFrictionToBones(
            KinematicRagdollControl kinematicRagdollControl, float friction) {

        List<PhysicsRigidBody> objects = new ArrayList<PhysicsRigidBody>();
        AnimControl animControl = ActorFactory.findControl(model, AnimControl.class);
        for (int i = 0; i < animControl.getSkeleton().getBoneCount(); i++) {
            PhysicsRigidBody prb = kinematicRagdollControl.getBoneRigidBody(animControl.getSkeleton().getBone(i).getName());
            if (prb != null) {
                prb.setFriction(friction);
            }
        }
    }

    /*
     * public void pause(boolean enabled) { if(enabled) {
     * characterControl.setEnabled(!enabled);
     * autonomousMovementControl.setEnabled(!enabled);
     * basicCharacterAnimControl.setEnabled(!enabled);
     * kinematicRagdollControl.setEnabled(!enabled); } else {
     *
     * }
     * }
     */
    public boolean moveTo(Vector3f location) {
        if (autonomousMovementControl != null) {
            ((AbstractControl) autonomousMovementControl).setEnabled(true);
            return autonomousMovementControl.moveTo(location);
        }
        return false;
    }
    
    public boolean pickUp(Spatial obj, boolean leftHand) {
        RigidBodyControl rbc = ActorFactory.findControl(obj, RigidBodyControl.class);
        if(rbc != null) {
            rbc.setEnabled(false);
        }
        obj.setLocalTranslation(0,0.05f,0.02f);
        Quaternion q = new Quaternion();
        q.fromAngles(0f, 0f, 90f);
        obj.setLocalRotation(q);
        
        SkeletonControl sc = ActorFactory.findControl(model, SkeletonControl.class);
        Node attachmentsNode;
        if(leftHand) {
            attachmentsNode = sc.getAttachmentsNode("LThumb");
        } else {
            attachmentsNode = sc.getAttachmentsNode("RThumb");
        }
        attachmentsNode.attachChild(obj);
        
        return true;
    }
    
    public boolean moveTo(Vector3f location, float distance) {
        if (autonomousMovementControl != null) {
            ((AbstractControl) autonomousMovementControl).setEnabled(true);
            autonomousMovementControl.setMinDistance(distance);
            return autonomousMovementControl.moveTo(location);
        }
        return false;
    }
    
    public void stopMoving() {
        if (autonomousMovementControl != null) {
            ((AbstractControl) autonomousMovementControl).setEnabled(false);
        }
    }

    public void setAnimation(String name) {
        if (kinematicRagdollControl.isEnabled()) {
            kinematicRagdollControl.setKinematicMode();
        }
        basicCharacterAnimControl.setAnimation(name);
    }

    public String getCurrentAnimationName() {
        if (kinematicRagdollControl.isEnabled()) {
            return "Ragdoll";
        }
        return basicCharacterAnimControl.getCurrentAnimationName();
    }

    public Collection<String> getAnimationName() {
        if(basicCharacterAnimControl != null) {
            return basicCharacterAnimControl.getAnimations();
        }
        return null;
    }
    
    public boolean hasAnimation(String animationName) {
        if(basicCharacterAnimControl != null) {
            return basicCharacterAnimControl.hasAnimation(animationName);
        }
        return false;
    }
    
    public void showName(boolean showName) {
        if (showName) {
            if (showedName == null) {
                showedName = SpatialFactory.attachAName(model);
                showedName.setLocalTranslation(0f, 2f, 0f);
            }
        } else if (showedName != null) {
            showedName.removeFromParent();
            showedName = null;
        }

    }

    public void tripOver() {
        ragdollTransitionControl.activate();

        ((AbstractControl) autonomousMovementControl).setEnabled(false);
    }

    public void slip() {
        ragdollTransitionControl = new SimulateSlip(model);
        ragdollTransitionControl.activate();

        ((AbstractControl) autonomousMovementControl).setEnabled(false);
        
        if (AudioFactory.getInstance() != null) {
            System.out.println("FallDownSound!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            getNode().addControl(new FallDownSound());
        }
    }

    public void standUp() {
        model.getChild(0).setLocalRotation(model.getWorldRotation());
        kinematicRagdollControl.setKinematicMode();
        if (basicCharacterAnimControl != null) {
            basicCharacterAnimControl.standUpAnimation();
        }
        FallDownSound fds = ActorFactory.findControl(getNode(), FallDownSound.class);
        if(fds != null) {
            fds.setEnabled(false);
        }
        kinematicRagdollControl.setEnabled(false);
    }
    
    @Override
    public void updateState(float tpf) {
        if (autonomousMovementControl != null && autonomousMovementControl.isMoving()) {
            System.out.println("Distance to target = " + autonomousMovementControl.getDistanceToTarget());
        }
    }

    @Override
    public String getName() {
        return model.getName();
    }

    @Override
    public Vector3f getLocation() {
        if (characterControl != null && characterControl.isEnabled()) {
            return characterControl.getPhysicsLocation();
        } else {
            model.updateModelBound();
            return model.getWorldBound().getCenter();
        }
    }

    @Override
    public Node getNode() {
        return model;
    }

    @Override
    public void say(String text, float volume) {
        if(speaker != null) {
            speaker.stop();
            speaker.removeFromParent();
        }
        
        speaker = AudioFactory.getInstance().makeAudioSpeakerSource(text, text, Vector3f.ZERO);
        speaker.setVolume(volume);
        speaker.setLooping(false); 
        speaker.setRefDistance(2f);
        
        model.attachChild(speaker);
        
        speaker.play();
    }
}
