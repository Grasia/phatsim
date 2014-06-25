/*
 * Copyright (C) 2014 pablo <pabcampi@ucm.es>
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
package phat.body.sensing.vision;

import com.jme3.animation.Bone;
import com.jme3.animation.SkeletonControl;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Line;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Transform;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.control.AbstractControl;
import com.jme3.util.BufferUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import phat.scene.control.PHATKeepObjectAtOffset;
import phat.util.SpatialFactory;
import phat.util.SpatialUtils;

/**
 * This class simulates eyes of the human and keeps a list of current visible object
 * in @see VisibleObjectManager.
 * Vision are modeled using several parameters. 
 * Two angles, horizontal and vertical, and a distance define visual field.
 * Also, a distance is defined for perceiving near objects, the default value is 1.0m.
 * 
 * The algorithm first detects the objects at a distance less than frontDistance.
 * Then checks if a object is visible by aiming a ray (a straight line) 
 * between three differents points of the object and the eye. 
 * If one ray does not find an obstacle then the object is visible.
 * 
 * Due to the cost of ray casting, a frecuency is defined. 
 * So, the algorithm is not executed every iteration.
 * 
 * @author pablo <pabcampi@ucm.es>
 */
public class VisionControl extends AbstractControl {

    Geometry view;
    SkeletonControl skeletonControl;
    float frontDistance = 5f;
    float roundDistance = 1f;
    float angleHor = FastMath.HALF_PI;
    float angleVer = FastMath.QUARTER_PI;
    float frecuency = 0.5f;
    float count = 0f;
    Map<String, Spatial> map = new HashMap<String, Spatial>();
    List<Spatial> nearest = new ArrayList<>();
    Node head;
    Bone headBone;
    Node spaceNode;
    VisibleObjectManager visibleObjectManager = new VisibleObjectManager();

    private void init() {
        skeletonControl = spatial.getControl(SkeletonControl.class);

        // pyramid mesh
        float vh = frontDistance / FastMath.cos(angleVer / 2f);
        float y = FastMath.sqrt(vh * vh - frontDistance * frontDistance);

        float hh = frontDistance / FastMath.cos(angleHor / 2f);
        float x = FastMath.sqrt(hh * hh - frontDistance * frontDistance);

        Vector3f[] vertices = new Vector3f[5];
        vertices[0] = new Vector3f(0, 0, 0);            // O
        vertices[1] = new Vector3f(-x, y, frontDistance);    // A
        vertices[2] = new Vector3f(x, y, frontDistance);     // B
        vertices[3] = new Vector3f(-x, -y, frontDistance);   // C
        vertices[4] = new Vector3f(x, -y, frontDistance);    // D

        Vector2f[] texCoord = new Vector2f[4];
        texCoord[0] = new Vector2f(0, 0);
        texCoord[1] = new Vector2f(1, 0);
        texCoord[2] = new Vector2f(0, 1);
        texCoord[3] = new Vector2f(1, 1);

        // Counter-clockwise
        int[] indexes = {0, 1, 2, 0, 2, 4, 0, 4, 3, 0, 3, 1};

        Mesh mesh = new Mesh();
        mesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
        mesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
        mesh.setBuffer(Type.Index, 3, BufferUtils.createIntBuffer(indexes));
        mesh.updateBound();

        view = new Geometry("VisionField", mesh); // wrap shape into geometry
        Material mat = new Material(SpatialFactory.getAssetManager(),
                "Common/MatDefs/Misc/Unshaded.j3md");   // create material
        mat.setColor("Color", new ColorRGBA(1, 0, 0, 0.3f));
        mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);  // !
        view.setQueueBucket(Bucket.Transparent);                        // !
        view.setMaterial(mat);                         // assign material to geometry

        head = skeletonControl.getAttachmentsNode("Head");
        //head.attachChild(view);
        headBone = skeletonControl.getSkeleton().getBone("Head");
        //spaceNode = (Node) SpatialUtils.getSpatialById(SpatialFactory.getRootNode(), "House1");
        if (spaceNode == null) {
            spaceNode = SpatialFactory.getRootNode();
        }
        SpatialUtils.getAllSpatialWithId(spaceNode, map);
    }
    
    Transform locTrans = new Transform();
    Transform parentTrans = new Transform();
    Quaternion rotation = new Quaternion();
    Vector3f worldCurrentDir = new Vector3f();
    Vector3f wYProjectedDir = new Vector3f();
    Vector3f wXProjectedDir = new Vector3f();
    Vector3f tYProjectedDir = new Vector3f();
    Vector3f tXProjectedDir = new Vector3f();
    float[] angles = new float[3];

    public void computeClosest() {
        locTrans.setRotation(headBone.getModelSpaceRotation());
        locTrans.setTranslation(headBone.getModelSpacePosition());
        locTrans.setScale(Vector3f.UNIT_XYZ);

        parentTrans.setRotation(spatial.getLocalRotation());
        parentTrans.setTranslation(spatial.getLocalTranslation());
        parentTrans.setScale(spatial.getLocalScale());

        locTrans.combineWithParent(parentTrans);

        worldCurrentDir = locTrans.getRotation().mult(Vector3f.UNIT_Z).normalize();
        Vector3f loc = locTrans.getTranslation();
        for (Spatial s : map.values()) {
            String id = s.getUserData("ID");
            if (!s.equals(spatial)) {
                Vector3f center = SpatialUtils.getCenterBoinding(s);
                float distance = center.distance(loc);
                if (distance < roundDistance && isVisible(spatial, loc, s)) {
                    visibleObjectManager.update(id, s, loc, center);
                } else if (distance < frontDistance) {
                    Vector3f point = SpatialUtils.getCenterBoinding(s);
                    Vector3f tDir = point.subtract(loc).normalize();
                    float angle = worldCurrentDir.angleBetween(tDir);
                    if (angle < angleHor / 2f && isVisible(spatial, loc, s)) {
                        visibleObjectManager.update(id, s, loc, center);
                    } else {
                        visibleObjectManager.remove(id);
                    }
                } else {
                    visibleObjectManager.remove(id);
                }
            }
        }
    }
    CollisionResults collisionResults = new CollisionResults();
    Ray ray = new Ray();
    Vector3f rayDir = new Vector3f();

    private boolean isVisible(Spatial origin, Vector3f po, Spatial target) {
        ray.setOrigin(po);

        Vector3f c = SpatialUtils.getCenterBoinding(target);
        if (c != null && !areObstacle(origin, po, target, c)) {
            return true;
        }
       
        Vector3f max = SpatialUtils.getMaxBounding(target);
        if (max != null && !areObstacle(origin, po, target, max)) {
            return true;
        }
        Vector3f min = SpatialUtils.getMinBounding(target);
        if (min != null && !areObstacle(origin, po, target, min)) {
            return true;
        }
        return false;
    }
    
    private boolean areObstacle(Spatial origin, Vector3f po, Spatial target, Vector3f targetPos) {
        ray.setOrigin(po);
        
        if (targetPos != null) {
            rayDir.set(targetPos).subtractLocal(po).normalizeLocal();
            ray.setDirection(rayDir);
            //ray.setOrigin(po.add(rayDir.mult(0.2f)));
            float range = targetPos.distance(po);
            ray.setLimit(range);
            collisionResults.clear();
            spaceNode.collideWith(ray, collisionResults);
            /*if (target.getUserData("ID").equals("Chair2")) {
             debug(ray, max);
             }*/
            //System.out.println("CollisionResult.size() = "+collisionResults.size());
            if (collisionResults.size() > 0) {
                for (int i = 0; i < collisionResults.size(); i++) {
                    CollisionResult cr = collisionResults.getCollision(i);
                    if (cr != null) {
                        if (!SpatialUtils.contains(origin, cr.getGeometry())
                                && !SpatialUtils.contains(target, cr.getGeometry())
                                && !cr.getGeometry().getName().equals("NavMesh")
                                && isInTheMiddle(po, targetPos, cr.getContactPoint(), range)) {
                            /*if (target.getUserData("ID").equals("Chair2")) {
                             debug(cr.getContactPoint());
                             SpatialUtils.printParents(cr.getGeometry());
                             System.out.println("distance = " + cr.getDistance());
                             }*/
                            //System.out.println("distance = "+cr.getDistance());
                            //SpatialUtils.printParents(cr.getGeometry());
                            return true;
                        }
                    }
                }
            }
        } else {
            System.out.println("No Max Bound - Target = " + target.getUserData("ID"));
        }
        return false;
    }
    
    Line line = new Line();

    private boolean isInTheMiddle(Vector3f origin, Vector3f target, Vector3f point, float range) {
        if (origin.distance(point) < range && target.distance(point) < range) {
            return true;
        }
        return false;
    }
    Geometry geo;

    private void debug(Ray ray, Vector3f targetLoc) {
        if (geo != null) {
            geo.removeFromParent();
        }
        geo = SpatialFactory.createArrow(ray.getDirection().mult(ray.getLimit()), 4f, ColorRGBA.Green);
        SpatialUtils.getRootNode(spatial).attachChild(geo);
        geo.setLocalTranslation(ray.getOrigin());
    }
    Geometry geo2;

    private void debug(Vector3f point) {
        if (geo2 != null) {
            geo2.removeFromParent();
        }
        geo2 = SpatialFactory.createCube(Vector3f.UNIT_XYZ.mult(0.1f), ColorRGBA.Green);
        SpatialUtils.getRootNode(spatial).attachChild(geo2);
        geo2.setLocalTranslation(point);
    }
    
    @Override
    protected void controlUpdate(float f) {
        if (skeletonControl == null) {
            init();
        } else if (count >= frecuency) {
            computeClosest();
            count = 0f;
        }
        count += f;
    }

    public VisibleObjectManager getVisibleObjectManager() {
        return visibleObjectManager;
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}
