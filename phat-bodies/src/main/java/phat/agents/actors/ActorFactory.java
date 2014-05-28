/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.agents.actors;

import com.jme3.animation.SkeletonControl;
import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.KinematicRagdollControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.jme3.scene.debug.SkeletonDebugger;
import phat.audio.AudioFactory;
import phat.bullet.control.ragdoll.BVHRagdollPreset;
import phat.bullet.control.ragdoll.FallDownSound;
import phat.controls.animation.BasicCharacterAnimControl;
import phat.controls.animation.FootStepsControl;
import phat.controls.animation.PatientAnimControl;
import phat.controls.movements.AutonomousMovementControl;
import phat.controls.movements.StraightMovementControl;

/**
 *
 * @author Pablo
 */
public class ActorFactory {

    protected static float scale = 1.0f;
    protected static AssetManager assetManager;
    protected static Node rootNode;
    protected static BulletAppState bulletAppState;

    public static void init(Node rootNode, AssetManager assetManager, BulletAppState bulletAppState) {
        ActorFactory.assetManager = assetManager;
        ActorFactory.rootNode = rootNode;
        ActorFactory.bulletAppState = bulletAppState;
    }

    public static Node createActorModel(String name, String actorModelPath, float scale) {
        Node model = (Node) assetManager.loadModel(actorModelPath);
        model.setLocalScale(scale * ActorFactory.scale);
        model.setName(name);

        return model;
    }

    public static BasicActor createBasicActor(String name, String actorModelPath,
            Vector3f location, float entityRadius, float userSpeed, float minDistance) {

        Node model = (Node) createActorModel(name, actorModelPath, 0.9f);

        // CharacterControl
        Node node = createCharacterControl(model, name, location, entityRadius, userSpeed);

        KinematicRagdollControl kinematicRagdollControl = createKinematicRagdollControl(model);

        // StraightMovementControl
        createStraightMovementControl(node, minDistance);

        // BasicCharacterAnimControl
        createBasicCharacterAnimControl(node);

        // FootStepsControl
        createFootStepsControl(node);

        kinematicRagdollControl.setEnabled(false);

        BasicActor ba = new BasicActor(node);

        createFallDownSoundControl(ba);

        return ba;
    }

    public static BasicActor createBasicActor(String name, String actorModelPath,
            Vector3f location, float entityRadius, float userSpeed, float minDistance, Class basicCharacterAnimControl) {

        Node model = (Node) createActorModel(name, actorModelPath, 0.9f);

        // CharacterControl
        Node node = createCharacterControl(model, name, location, entityRadius, userSpeed);

        KinematicRagdollControl kinematicRagdollControl = createKinematicRagdollControl(model);

        // StraightMovementControl
        createStraightMovementControl(node, minDistance);

        // BasicCharacterAnimControl
        if (basicCharacterAnimControl.isAssignableFrom(BasicCharacterAnimControl.class)) {
            createBasicCharacterAnimControl(node);
        } else if (basicCharacterAnimControl.isAssignableFrom(PatientAnimControl.class)) {
            createPatientAnimControl(node);
        }

        kinematicRagdollControl.setEnabled(false);

        // FootStepsControl
        createFootStepsControl(node);

        BasicActor ba = new BasicActor(node);

        createFallDownSoundControl(ba);

        return ba;
    }

    public static Node createCharacterControl(Node model, String name, Vector3f loc, float radius, float speed) {
        Vector3f location = loc.mult(scale);
        float entityRadius = radius * scale;
        float userSpeed = speed * scale;

        Vector3f extent = ((BoundingBox) model.getWorldBound()).getExtent(new Vector3f());

        Node node = new Node();
        node.setName(name);
        node.setUserData("Speed", userSpeed);

        model.setLocalTranslation(new Vector3f(0f, -extent.getY(), 0f));

        node.attachChild(model);
        rootNode.attachChild(node);

        node.move(location);

        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(entityRadius, extent.getY() * 2f * 0.8f);
        CharacterControl characterControl = new CharacterControl(capsuleShape, 0.01f);
        characterControl.setJumpSpeed(10);
        characterControl.setFallSpeed(55);
        characterControl.setGravity(9.8f);

        bulletAppState.getPhysicsSpace().add(characterControl);
        node.addControl(characterControl);

        characterControl.setPhysicsLocation(location.add(0f, 0.1f, 0f));

        return node;
    }

    public static void debugSkeleton(Node model, SkeletonControl control) {
        SkeletonDebugger skeletonDebug =
                new SkeletonDebugger("skeleton", control.getSkeleton());
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Green);
        mat.getAdditionalRenderState().setDepthTest(false);
        skeletonDebug.setMaterial(mat);
        model.attachChild(skeletonDebug);
    }

    public static KinematicRagdollControl createKinematicRagdollControl(Node model) {
        BVHRagdollPreset preset = new BVHRagdollPreset();
        KinematicRagdollControl kinematicRagdollControl = new KinematicRagdollControl(preset, 0.5f);
        //kinematicRagdollControl.setRootMass(90f);  
        //initBones(kinematicRagdollControl);

        bulletAppState.getPhysicsSpace().add(kinematicRagdollControl);
        model.addControl(kinematicRagdollControl);
        kinematicRagdollControl.setEnabled(false);

        return kinematicRagdollControl;
    }

    public static AutonomousMovementControl createStraightMovementControl(Node node, float minDist) {
        float minDistance = minDist * scale;
        StraightMovementControl straightMovementControl = new StraightMovementControl(minDistance);
        node.addControl(straightMovementControl);
        return straightMovementControl;
    }

    public static BasicCharacterAnimControl createBasicCharacterAnimControl(Node node) {
        System.out.println("createBasicCharacterAnimControl()");
        BasicCharacterAnimControl basicCharacterAnimControl = new BasicCharacterAnimControl();
        node.addControl(basicCharacterAnimControl);
        return basicCharacterAnimControl;
    }

    public static FootStepsControl createFootStepsControl(Node node) {
        FootStepsControl footStepsControl = null;
        if (AudioFactory.getInstance() != null) {
            System.out.println("createFootStepsControl()...");
            footStepsControl = new FootStepsControl(assetManager);
            System.out.println("addControl");
            node.addControl(footStepsControl);
            System.out.println("...createFootStepsControl()");
        }
        return footStepsControl;
    }

    public static BasicCharacterAnimControl createPatientAnimControl(Node node) {
        System.out.println("createPatientAnimControl()");
        BasicCharacterAnimControl basicCharacterAnimControl = new PatientAnimControl();
        node.addControl(basicCharacterAnimControl);
        return basicCharacterAnimControl;
    }

    public static <T extends Control> T findControl(Spatial spatial, Class<T> controlType) {
        Control c = spatial.getControl(controlType);
        if (c != null) {
            return (T) c;
        } else if (spatial instanceof Node) {
            Node node = (Node) spatial;
            for (Spatial s : node.getChildren()) {
                Control control = s.getControl(controlType);
                if (control != null) {
                    return (T) control;
                }
            }
        }
        return null;
    }

    public static float getScale() {
        return scale;
    }

    public static void setScale(float scale) {
        ActorFactory.scale = scale;
    }

    public static FallDownSound createFallDownSoundControl(BasicActor ba) {
        /*
        FallDownSound fds = null;
        if (AudioFactory.getInstance() != null) {
            System.out.println("createFallDownSoundControl(BasicActor)...");
            fds = new FallDownSound(ba);
            System.out.println("...createFallDownSoundControl(BasicActor)");
        }
        return fds;*/
        return null;
    }
}
