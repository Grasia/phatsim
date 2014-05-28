/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.body.control.navigation;

import com.jme3.ai.steering.Obstacle;
import com.jme3.ai.steering.behaviour.ObstacleAvoid;
import com.jme3.ai.steering.behaviour.Persuit;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Plane;
import com.jme3.math.Plane.Side;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import phat.body.control.physics.PHATCharacterControl;
import phat.util.SpatialFactory;
import phat.util.SpatialUtils;

/**
 *
 * @author Pablo
 */
public class PersuitAndAvoidControl extends AbstractControl {

    private PHATCharacterControl characterControl;
    float maxTurnForce = 0.5f;
    float maxSlope = 0.2f;
    //private Spatial target;
    boolean debug = false;
    float[] checkAngles = {
        FastMath.HALF_PI * 0.4f, -FastMath.HALF_PI * 0.4f, 0,
        FastMath.HALF_PI * 0.7f, -FastMath.HALF_PI * 0.7f,
        FastMath.HALF_PI, -FastMath.HALF_PI};
    List<Geometry> geoObstacles = new ArrayList<Geometry>();
    float lastTime = 0f;
    float frecuency = 0.2f;

    public PersuitAndAvoidControl() {
        super();
    }
    Persuit persuit = new Persuit();
    ObstacleAvoid avoid = new ObstacleAvoid();
    List<Obstacle> obstacles = new ArrayList<Obstacle>();
    Node rootNode;
    Geometry geoDir1;
    Geometry geoDir2;
    Geometry geoDir3;

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);

        if (spatial != null) {
            characterControl = spatial.getControl(PHATCharacterControl.class);
            rootNode = SpatialUtils.getRootNode(spatial);
            lastTime = 0f;
        }
    }

    public Node getRootNode() {
        if (rootNode == null) {
            rootNode = SpatialUtils.getRootNode(spatial);
        }
        return rootNode;
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (lastTime < frecuency) {
            lastTime += tpf;
            return;
        }
        lastTime = 0f;
        if (!characterControl.isEnabled()) {
            return;
        }

        if (debug) {
            for (Geometry g : geoObstacles) {
                g.removeFromParent();
            }
            geoObstacles.clear();
        }

        walkDirNorm.set(characterControl.getWalkDirection());
        walkDirNorm.normalizeLocal();
        location.set(characterControl.getLocation());

        fillObstacles(obstacles);

        float speed = spatial.getUserData("Speed");

        Vector3f avoidance = avoidance(
                location,
                characterControl.getVelocity(),
                speed,
                characterControl.getRadius(),
                maxTurnForce,
                tpf,
                obstacles);

        if (debug) {
            updateGeoObstacles();
        }

        if (avoidance.equals(Vector3f.ZERO)) {
            return;
        }

        // add the force to the velocity
        Vector3f avoidanceDir = avoidance.setY(0f).normalize();
        Vector3f steeringDir = characterControl.getWalkDirection().setY(0f).normalize();
        move(avoidanceDir, steeringDir, tpf);
        //characterControl.setViewDirection(dir.normalize());

        if (debug) {
            Vector3f pos = characterControl.getLocation().addLocal(0f, 1f, 0f);

            Geometry geo = SpatialFactory.createArrow(steeringDir.normalize(), 2, ColorRGBA.Blue);
            geo.setLocalTranslation(pos.add(0f, 0.1f, 0f));
            rootNode.attachChild(geo);
            geoObstacles.add(geo);

            geo = SpatialFactory.createArrow(avoidanceDir, 2, ColorRGBA.Orange);
            geo.setLocalTranslation(pos.add(0f, 0.2f, 0f));
            rootNode.attachChild(geo);
            geoObstacles.add(geo);

            geo = SpatialFactory.createArrow(characterControl.getWalkDirection().normalize(), 2, ColorRGBA.Green);
            geo.setLocalTranslation(pos.add(0f, 0.3f, 0f));
            rootNode.attachChild(geo);
            geoObstacles.add(geo);
        }
    }

    private void move(Vector3f avoidanceDir, Vector3f steeringDir, float tpf) {
        Vector3f currentDir = characterControl.getWalkDirection().normalize();
        Vector3f directionVector = steeringDir.add(avoidanceDir.mult(0.5f));
        Vector3f targetDir = directionVector.setY(0f).normalize();
        Vector3f diff = targetDir.subtract(currentDir);
        Vector3f effectDir = currentDir.add(diff).normalize();
        //characterControl.setViewDirection(effectDir);
        characterControl.setWalkDirection(effectDir.mult(getSpeed(diff)));
    }

    private float getSpeed(Vector3f diff) {
        float speed = 0.01f;
        if (diff.length() < 2f) {
            speed = getSpeed(spatial) * (1f - (diff.length() / 2f));
        }
        return speed;
    }
    private Vector3f result = new Vector3f(0f, 0f, 0);
    private Vector3f projLoc = new Vector3f();
    private Vector3f projLocObj = new Vector3f();

    public Vector3f avoidance(Vector3f location, Vector3f velocity, float collisionRadius,
            float speed, float turnSpeed, float tpf,
            List<Obstacle> obstacles) {
        result.set(0f, 0f, 0f);
        projLoc.set(location).setY(0f);
        // assuming obsticals are ordered from closest to farthest
        for (Obstacle obstacle : obstacles) {
            projLocObj.set(obstacle.getLocation());
            projLocObj.setY(0f);
            projLocObj.subtractLocal(projLoc);
            result.addLocal(projLocObj);
        }
        if (result.equals(Vector3f.ZERO)) {
            return result;
        }
        return result.negateLocal();
    }

    public void updateGeoObstacles() {
        for (Obstacle o : obstacles) {
            Geometry geo = SpatialFactory.createCube(new Vector3f(0.1f, 0.1f, 0.1f), ColorRGBA.Blue);
            geo.setLocalTranslation(o.getLocation());
            getRootNode().attachChild(geo);
            geoObstacles.add(geo);
        }
    }

    private float getSpeed(Spatial spatial) {
        return spatial.getUserData("Speed");
    }

    private Vector3f getVelocity(Spatial spatial) {
        if (spatial.getControl(PHATCharacterControl.class) != null) {
            return spatial.getControl(PHATCharacterControl.class).getVelocity();
        } else if (spatial.getControl(RigidBodyControl.class) != null) {
            return spatial.getControl(RigidBodyControl.class).getLinearVelocity();
        }
        return Vector3f.ZERO;
    }

    /*public Spatial getTarget() {
     return target;
     }

     public void setTarget(Spatial target) {
     this.target = target;
     }*/
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    @Override
    public Control cloneForSpatial(Spatial sptl) {
        PersuitAndAvoidControl smc = new PersuitAndAvoidControl();
        smc.setSpatial(sptl);
        return smc;
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

    private Node getHousesNode() {
        Node world = (Node) getRootNode().getChild("World");
        Spatial houses = world.getChild("Houses");
        if (houses == null) {
            houses = new Node("Houses");
            world.attachChild(houses);
        }
        return (Node) houses;
    }
    Vector3f location = new Vector3f();
    Vector3f walkDirNorm = new Vector3f();
    private Vector3f iniArrow = new Vector3f();
    private Vector3f endArrow = new Vector3f();
    private Vector3f cDir = new Vector3f();

    private void fillObstacles(List<Obstacle> obstacles) {
        obstacles.clear();

        iniArrow.set(location);
        iniArrow.addLocal(0f, 1.8f, 0f).addLocal(walkDirNorm.mult(0.2f));

        endArrow.set(walkDirNorm.mult(0.5f));

        Quaternion q = new Quaternion();
        for (float angle : checkAngles) {
            q.fromAngles(0f, angle, 0f);
            q.mult(endArrow, cDir);
            Obstacle obs = getObstacle(iniArrow, cDir);
            if (obs != null) {
                obstacles.add(obs);
            }
            if (debug) {
                updateDirArrow(iniArrow, characterControl.getLocation().add(cDir).subtract(iniArrow));
            }
        }
    }

    private Geometry updateDirArrow(Vector3f pos, Vector3f dir) {
        Geometry geo = SpatialFactory.createArrow(dir, 2f, ColorRGBA.Red);
        geo.setLocalTranslation(pos);
        getRootNode().attachChild(geo);
        geoObstacles.add(geo);
        return geo;
    }

    private CollisionResult getClosestCollision(CollisionResults results) {
        CollisionResult result = null;
        float minDist = Float.MAX_VALUE;

        for (CollisionResult cr : results) {
            if (!cr.getGeometry().getParent().equals(spatial)) {
                float dist = cr.getDistance();
                if (dist < minDist) {
                    minDist = dist;
                    result = cr;
                }
            }
        }
        return result;
    }
    private Vector3f dirRay = new Vector3f();

    private Obstacle getObstacle(Vector3f iniArrow, Vector3f dir) {
        CollisionResults results = new CollisionResults();

        dirRay.set(location).addLocal(dir).subtractLocal(iniArrow);
        //Vector3f dirRay = characterControl.getLocation().add(dir).subtract(iniArrow);
        Ray ray = new Ray(iniArrow, dirRay);
        getRootNode().collideWith(ray, results);

        if (results.size() > 0) {
            // The closest collision point is what was truly hit:
            CollisionResult closest = getClosestCollision(results);
            if (closest != null) {
                if (closest.getContactPoint().getY()
                        < characterControl.getLocation().getY() + maxSlope) {
                    return null;
                }
                return new FastStaticObstacle(closest.getContactPoint());
            }
        }
        return null;
    }

    private Obstacle getObstacle(CollisionResult closest) {
        Geometry g = closest.getGeometry();
        PHATCharacterControl cc = g.getParent().getControl(PHATCharacterControl.class);
        if (cc != null) {
            return new PHATCharacterObstacle(cc);
        }
        RigidBodyControl rbc = g.getControl(RigidBodyControl.class);
        if (rbc == null) {
            if (g.getParent().getName().equals("Geometries")) {
                rbc = g.getParent().getParent().getControl(RigidBodyControl.class);
            } else {
                rbc = g.getParent().getControl(RigidBodyControl.class);
            }
        }
        if (rbc != null) {
            return new RigidBodyObstacle(rbc);
        }

        return new FastStaticObstacle(closest.getContactPoint());
    }

    class FastStaticObstacle implements Obstacle {

        Vector3f pos;

        public FastStaticObstacle(Vector3f pos) {
            this.pos = pos;
        }

        @Override
        public Vector3f getVelocity() {
            return Vector3f.ZERO;
        }

        @Override
        public Vector3f getLocation() {
            return pos;
        }

        @Override
        public float getRadius() {
            return 0.1f;
        }
    }

    class RigidBodyObstacle implements Obstacle {

        RigidBodyControl rbc;

        public RigidBodyObstacle(RigidBodyControl rbc) {
            this.rbc = rbc;
        }

        @Override
        public Vector3f getVelocity() {
            return rbc.getLinearVelocity();
        }

        @Override
        public Vector3f getLocation() {
            return rbc.getPhysicsLocation();
        }

        @Override
        public float getRadius() {
            return 0.1f;
        }
    }

    class PHATCharacterObstacle implements Obstacle {

        PHATCharacterControl cc;

        public PHATCharacterObstacle(PHATCharacterControl cc) {
            this.cc = cc;
        }

        @Override
        public Vector3f getVelocity() {
            return cc.getVelocity();
        }

        @Override
        public Vector3f getLocation() {
            return cc.getLocation();
        }

        @Override
        public float getRadius() {
            return cc.getRadius();
        }
    }

    public float getFrecuency() {
        return frecuency;
    }

    public void setFrecuency(float frecuency) {
        this.frecuency = frecuency;
    }
}
