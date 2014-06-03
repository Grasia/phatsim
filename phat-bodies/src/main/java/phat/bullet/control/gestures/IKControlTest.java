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

/**
 *
 * @author pablo
 */
import phat.util.SpatialFactory;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.Bone;
import com.jme3.animation.LoopMode;
import com.jme3.animation.Skeleton;
import com.jme3.animation.SkeletonControl;
import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.control.KinematicRagdollControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.control.ragdoll.RagdollPreset;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.MouseInput;
import com.jme3.input.RawInputListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.debug.SkeletonDebugger;
import java.util.logging.Level;
import java.util.logging.Logger;
import phat.agents.actors.ActorFactory;
import phat.bullet.control.ragdoll.BVHRagdollPreset;
import phat.util.Debug;
import phat.util.SimpleScenario;

/**
 * @author Seth
 */
public class IKControlTest extends SimpleScenario {

    private final Node prime = new Node("Prime");

    /* (non-Javadoc)
     * @see com.jme3.app.SimpleApplication#simpleInitApp()
     */
    @Override
    public void simpleInitApp() {
        ActorFactory.init(rootNode, assetManager, null);
        SpatialFactory.init(assetManager, rootNode);
        
        super.simpleInitApp();
        Logger.getLogger(RagdollPreset.class.getName()).setLevel(Level.FINE);
        
        flyCam.setMoveSpeed(10);
        
        input();
        
    }

    public static void main(String[] args) {
        new IKControlTest().start();
    }

    @Override
    public void createTerrain() {
        Debug.enableDebugGrid(10, assetManager, rootNode);
    }

    @Override
    public void createOtherObjects() {
        Node model = ActorFactory.createActorModel("Patient", "Models/People/Elder/Elder.j3o", 1f);
//model.setLocalScale(0.5f);
        model.setLocalTranslation(0, 0, 0);
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White);
        rootNode.addLight(al);

        Skeleton skeleton = model.getControl(SkeletonControl.class).getSkeleton();

        rootNode.attachChild(model);

        BVHRagdollPreset preset = new BVHRagdollPreset();
        KinematicRagdollControl krc = new KinematicRagdollControl(preset);
        krc.setKinematicMode();
        model.addControl(krc);
        
        bulletAppState.getPhysicsSpace().add(krc);
        
        
        AnimControl control = model.getControl(AnimControl.class);

        /*AnimChannel animChannel = control.createChannel();
		animChannel.setAnim("RunTop");
		animChannel.setLoopMode(LoopMode.Loop);

		animChannel = control.createChannel();
                animChannel.setAnim("RunBase");
		animChannel.setLoopMode(LoopMode.Loop);*/

        /*Node node = new Node("Target L");
        SpatialFactory.init(assetManager, rootNode);
        Geometry g1 = SpatialFactory.createCube(Vector3f.UNIT_XYZ, ColorRGBA.Red);
        g1.scale(0.25f);
        node.attachChild(g1);
        node.setLocalTranslation(1f, 0f, 0);
        prime.attachChild(node);

        Node node2 = new Node("Target R");
        Geometry g2 = SpatialFactory.createCube(Vector3f.UNIT_XYZ, ColorRGBA.Blue);
        g2.scale(0.25f);
        node2.attachChild(g2);
        node2.setLocalTranslation(-1f, 0f, 0);
        prime.attachChild(node2);*/

        Node nodea = new Node("Target AL");
        Geometry ga1 = SpatialFactory.createCube(Vector3f.UNIT_XYZ, ColorRGBA.Red);
        ga1.scale(0.25f);
        nodea.attachChild(ga1);
        nodea.setLocalTranslation(0.2f, 1.5f, 0.5f);
        prime.attachChild(nodea);

        RigidBodyControl rbc = new RigidBodyControl(10f);
        
        nodea.addControl(rbc);
        
        rbc.setKinematic(true);
        
        bulletAppState.getPhysicsSpace().add(rbc);
        
        
        
        /*Node nodea2 = new Node("Target AR");
        Geometry ga2 = SpatialFactory.createCube(Vector3f.UNIT_XYZ, ColorRGBA.Blue);
        ga2.scale(0.25f);
        nodea2.attachChild(ga2);
        nodea2.setLocalTranslation(-1f, 1f, 0);
        prime.attachChild(nodea2);*/

        /*model.addControl(ikControl);
        ikControl.setTarget(node);
        ikControl.setFirstBone(skeleton.getBone("LeftUpLeg"));
        ikControl.setMaxChain(2);
        ikControl.setIterations(50);
        ikControl.setTargetBone(skeleton.getBone("LeftFoot"));

        ikControl = new IkControl(skeleton);
        model.addControl(ikControl);

        ikControl.setTarget(node2);
        ikControl.setFirstBone(skeleton.getBone("RightUpLeg"));
        ikControl.setMaxChain(2);
        ikControl.setIterations(50);

        ikControl.setTargetBone(skeleton.getBone("RightFoot"));*/

// =======================================
        IkControl ikControl = new IkControl(skeleton);
        model.addControl(ikControl);
        ikControl.setTarget(nodea);
        ikControl.setFirstBone(skeleton.getBone("LeftHand"));
        ikControl.setMaxChain(3);
        ikControl.setIterations(50);
        ikControl.setTargetBone(skeleton.getBone("LMiddleFingerTip"));

        
        /*ikControl = new IkControl(skeleton);
        model.addControl(ikControl);

        ikControl.setTarget(nodea2);
        ikControl.setFirstBone(skeleton.getBone("RightShoulder"));
        ikControl.setMaxChain(2);
        ikControl.setIterations(50);
        ikControl.setTargetBone(skeleton.getBone("RightHand"));*/

        skeleton.reset();
        skeleton.updateWorldVectors();

        rootNode.attachChild(prime);

        for (Bone bone : skeleton.getRoots()) {
            System.out.println(bone);
        }

        SkeletonDebugger skeletonDebug = new SkeletonDebugger("skeleton", skeleton);
        Material mat2 = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat2.getAdditionalRenderState().setWireframe(true);
        mat2.setColor("Color", ColorRGBA.Green);
        mat2.getAdditionalRenderState().setDepthTest(false);
        skeletonDebug.setMaterial(mat2);
        skeletonDebug.setLocalTranslation(model.getLocalTranslation());
        rootNode.attachChild(skeletonDebug);

        flyCam.setDragToRotate(true);
        FlyCamAppState state = stateManager.getState(FlyCamAppState.class);
        stateManager.detach(state);
        flyCam.registerWithInput(inputManager);
    }
    
    private void input() {
        inputManager.deleteMapping("FLYCAM_ZoomIn");
        inputManager.deleteMapping("FLYCAM_ZoomOut");
        inputManager.deleteMapping("FLYCAM_RotateDrag");
        inputManager.addMapping("FLYCAM_RotateDrag", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        inputManager.addListener(flyCam, "FLYCAM_RotateDrag");

        inputManager.addRawInputListener(new RawInputListener() {
            Geometry target;
            float distance;

            @Override
            public void onTouchEvent(TouchEvent evt) {
            }

            @Override
            public void onMouseMotionEvent(MouseMotionEvent evt) {
                if (target != null) {
                    Vector3f dir = cam.getWorldCoordinates(inputManager.getCursorPosition(), .9f);

                    target.getParent().setLocalTranslation(dir);
                }
            }

            @Override
            public void onMouseButtonEvent(MouseButtonEvent evt) {
                System.out.println("ress");
                if (evt.isPressed() && evt.getButtonIndex() == MouseInput.BUTTON_LEFT) {

                    Vector3f pos = cam.getWorldCoordinates(inputManager.getCursorPosition(), .0f);
                    Vector3f dir = cam.getWorldCoordinates(inputManager.getCursorPosition(), .3f);
                    dir.subtractLocal(pos).normalizeLocal();

                    CollisionResults rs = new CollisionResults();
                    prime.collideWith(new Ray(pos, dir), rs);

                    System.out.println("press");
                    if (rs.size() > 0) {
                        CollisionResult result = rs.getClosestCollision();
                        System.out.println("result");
                        distance = result.getDistance();
//if(result.getGeometry().getName().startsWith("Target")) {
                        target = result.getGeometry();
                        System.out.println("result2");
//}
                    }
                } else {
                    target = null;
                }
            }

            @Override
            public void onKeyEvent(KeyInputEvent evt) {
            }

            @Override
            public void onJoyButtonEvent(JoyButtonEvent evt) {
            }

            @Override
            public void onJoyAxisEvent(JoyAxisEvent evt) {
            }

            @Override
            public void endInput() {
            }

            @Override
            public void beginInput() {
            }
        });
    }
}
