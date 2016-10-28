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
package phat.structures.houses;

import com.jme3.ai.navmesh.NavMesh;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.collision.CollisionResults;
import com.jme3.light.Light;
import com.jme3.light.PointLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import phat.util.PhysicsUtils;

import phat.util.SpatialFactory;
import phat.util.SpatialUtils;

/**
 *
 * @author Pablo
 */
public class House {

    Geometry geoNavMesh;
    NavMesh navMesh;
    String urlResource;
    String houseId;
    Node house;
    Node physicalEntities;
    Node logicalEntities;
    Node spatialCoordenates;
    Node visualStructure;
    Node physicsStructure;
    List<String> roomNames = new ArrayList<String>();
    Map<String, List<Light>> lights = new HashMap<>();
    Map<String, Spatial> spatials = new HashMap<>();
    Map<String, List<Vector3f>> roomBoundings = new HashMap<>();

    /**
     * Creates a house whose model is given by urlResource
     *
     * @param urlResource
     */
    public House(String houseId, String urlResource) {
        this.houseId = houseId;
        this.urlResource = urlResource;
    }

    /**
     * Build the house with physics properties and add it to rootNode (the
     * world).
     *
     * @param rootNode
     * @param assetManager
     * @param physicsSpace
     */
    public void build(Node rootNode, SimpleApplication app) {
        house = (Node) app.getAssetManager().loadModel(urlResource);
        house.setUserData("ID", houseId);
        house.setUserData("ROLE", "House");
        System.out.println("\n\nBuinding " + house.getName());
        physicalEntities = (Node) house.getChild("PhysicalEntities");

        Node physicalStructure = (Node) house.getChild("Structure");
        visualStructure = (Node) physicalStructure.getChild("Visual");
        physicsStructure = (Node) physicalStructure.getChild("Physics");
        logicalEntities = (Node) house.getChild("LogicalEntities");
        spatialCoordenates = (Node) logicalEntities
                .getChild("SpatialCoordenates");

        initSpatials();
        initLights(app);

        app.getStateManager().getState(BulletAppState.class).getPhysicsSpace().addAll(physicalEntities);
        app.getStateManager().getState(BulletAppState.class).getPhysicsSpace().addAll(physicsStructure);

        rootNode.attachChild(house);

        PhysicsUtils.updateLocationAndRotation(house);
    }

    public boolean isSpatialInHouse(Spatial spatial) {
        spatial.updateModelBound();

        CollisionResults c = new CollisionResults();
        Ray ray = new Ray(spatial.getWorldBound().getCenter(), Vector3f.UNIT_Y.negate());
        physicsStructure.collideWith(ray, c);

        return c.size() > 0;
    }

    public boolean isSpatialInRoom(Spatial spatial, String roomName) {
        List<Vector3f> points = roomBoundings.get(roomName);
        if (points != null) {
            Vector3f objCenter = spatial.getWorldBound().getCenter();
            return contains(objCenter, points);
        }
        return false;
    }

    public String getRoomNameLocation(String objID) {
        Spatial obj = SpatialUtils.getSpatialById(SpatialUtils.getRootNode(house), objID);
        if (obj != null) {
            return getRoomNameLocation(obj);
        }
        return null;
    }

    public String getRoomNameLocation(Spatial spatial) {
        for (String roomName : roomBoundings.keySet()) {
            if (isSpatialInRoom(spatial, roomName)) {
                return roomName;
            }
        }
        return null;
    }

    /**
     * https://www.ecse.rpi.edu/Homepages/wrf/Research/Short_Notes/pnpoly.html
     *
     * @param test
     * @param points
     * @return
     */
    private boolean contains(Vector3f test, List<Vector3f> points) {
        int i;
        int j;
        boolean result = false;
        for (i = 0, j = points.size() - 1; i < points.size(); j = i++) {
            if (((points.get(i).z > test.z) != (points.get(j).z > test.z))
                    && (test.x < (points.get(j).x - points.get(i).x)
                    * (test.z - points.get(i).z) / (points.get(j).z - points.get(i).z)
                    + points.get(i).x)) {
                result = !result;
            }
        }
        return result;
    }

    public boolean isObjInRoom(String objID, String roomName) {
        Spatial obj = SpatialUtils.getSpatialById(SpatialUtils.getRootNode(house), objID);
        if (obj != null) {
            return isSpatialInRoom(obj, roomName);
        }
        return false;
    }

    public Spatial getClosetLightSwitch(Spatial object, String roomName) {
        Spatial result = null;
        float minDist = Float.MAX_VALUE;
        Node cLights = getNode(roomName, "Lights");
        if(cLights != null) {
            for(Spatial s: cLights.getChildren()) {
                if(result == null) {
                    result = s;
                    minDist = object.getWorldTranslation().distance(s.getWorldTranslation());
                } else if(object.getWorldTranslation().distance(s.getWorldTranslation()) < minDist) {
                    result = s;
                    minDist = object.getWorldTranslation().distance(s.getWorldTranslation());
                }
            }
        }
        return result;
    }

    public void switchLights(String room, boolean on) {
        for (Light l : lights.get(room)) {
            if (on) {
                l.setColor(ColorRGBA.White);
                if (l instanceof PointLight) {
                    ((PointLight) l).setRadius(4f);
                    System.out.println("Position = " + ((PointLight) l).getPosition());
                    System.out.println("PointLight radius = " + ((PointLight) l).getRadius());
                }
            } else {
                l.setColor(l.getColor().mult(0f));
            }
        }
    }
    
    public boolean isLightOn(String room) {
        for (Light l : lights.get(room)) {
            return 0f != l.getColor().toVector3f().length();
        }
        return false;
    }
    
    private void initLights(SimpleApplication app) {
        for (String roomName : getRoomNames()) {
            Node clights = getNode(roomName, "Lights");
            if (clights != null) {
                System.out.println(roomName + " -> new PointLight()");

                PointLight pl = new PointLight();
                pl.setColor(ColorRGBA.Yellow);
                pl.setPosition(clights.getWorldTranslation());
                pl.setRadius(0.1f);
                add(roomName, pl);

                app.getRootNode().addLight(pl);

                /*PointLightShadowRenderer slsr = new PointLightShadowRenderer(SpatialFactory.getAssetManager(), SHADOWMAP_SIZE);
                slsr.setLight(pl);
                slsr.setShadowIntensity(0.6f);
                slsr.setEdgeFilteringMode(EdgeFilteringMode.Nearest);
                //dlsr.displayDebug();
                app.getViewPort().addProcessor(slsr);

                PointLightShadowFilter slsf = new PointLightShadowFilter(app.getAssetManager(), SHADOWMAP_SIZE);
                slsf.setLight(pl);
                slsf.setShadowIntensity(0.6f);
                slsf.setEdgeFilteringMode(EdgeFilteringMode.Nearest);
                slsf.setEnabled(true);

                FilterPostProcessor fpp = new FilterPostProcessor(app.getAssetManager());
                fpp.addFilter(slsf);*/

 /*SSAOFilter ssaoFilter = new SSAOFilter(12.940201f, 43.928635f, 0.32999992f, 0.6059958f);
                 fpp.addFilter(ssaoFilter);
                 new SSAOUI(app.getInputManager(), ssaoFilter);*/
                //app.getViewPort().addProcessor(fpp);
            }
        }
    }

    private void initSpatials() {
        for (Spatial sr : spatialCoordenates.getChildren()) {
            spatials.put(sr.getName(), sr);
            Node room = (Node) sr;
            roomNames.add(room.getName());
            for (Spatial sa : room.getChildren()) {
                if (sa.getName().equals("Perimeter")) {
                    Node perimeter = (Node) sa;
                    List<Vector3f> points = new ArrayList<>();
                    for (Spatial point : perimeter.getChildren()) {
                        points.add(point.getWorldTranslation());
                    }
                    roomBoundings.put(room.getName(), points);
                } else {
                    spatials.put(sa.getName(), sa);
                }
            }
        }
    }

    public void cleanup(PhysicsSpace physicsSpace) {
        roomNames.clear();
        spatials.clear();
        lights.clear();

        physicalEntities.removeFromParent();
        visualStructure.removeFromParent();

        physicsSpace.removeAll(physicalEntities);
        physicsSpace.removeAll(physicsStructure);
    }

    public void setHighPhysicsPrecision() {
        final float linear = 0.0001f;
        final float angular = 0.0001f;
        final float threshold = 0.0001f;
        final float radius = 1f;

        SceneGraphVisitor visitor = new SceneGraphVisitor() {
            @Override
            public void visit(Spatial spat) {
                RigidBodyControl rbc = spat.getControl(RigidBodyControl.class);
                if (rbc != null) {
                    rbc.setSleepingThresholds(linear, angular);
                    rbc.setCcdMotionThreshold(threshold);
                    rbc.setCcdSweptSphereRadius(radius);
                }
            }
        };
        physicalEntities.depthFirstTraversal(visitor);
    }

    public void createPointLight(String roomName, ColorRGBA color, float rad) {
        PointLight lamp_light = new PointLight();
        lamp_light.setColor(color);
        lamp_light.setRadius(rad);
        lamp_light
                .setPosition(new Vector3f(getCoordenates(roomName, "Lights")));
        house.addLight(lamp_light);
        add(roomName, lamp_light);
    }

    private void add(String roomName, Light light) {
        List<Light> list = lights.get(roomName);
        if (list == null) {
            list = new ArrayList();
            lights.put(roomName, list);
        }
        list.add(light);
    }

    public List<Light> getLights(String roomName) {
        return lights.get(roomName);
    }

    /*
     * public void switchLights(String roomName, boolean on) { for(Light l:
     * getLights(roomName)) { if(on) { rootNode.getLocalLightList(). } } }
     */
    public void showNavMesh(boolean enabled) {
        if (enabled) {
            if (geoNavMesh == null) {
                Geometry debugNavMesh = (Geometry) logicalEntities
                        .getChild("NavMesh");
                this.geoNavMesh = SpatialFactory.createShape("NavMesh",
                        debugNavMesh.getMesh(), ColorRGBA.Green);
                this.geoNavMesh.setLocalTranslation(this.geoNavMesh
                        .getLocalTranslation());
            }
            house.attachChild(geoNavMesh);
        } else if (geoNavMesh != null) {
            geoNavMesh.removeFromParent();
        }
    }

    public boolean isShowNavMesh() {
        return geoNavMesh != null && geoNavMesh.getParent() != null;
    }

    public List<String> getRoomNames() {
        return roomNames;
    }

    public Vector3f getCoordenates(String roomName) {
        if (roomNames.contains(roomName)) {
            Node room = (Node) spatialCoordenates.getChild(roomName);
            Spatial location = room.getChild("Center");
            if (location == null && !room.getChildren().isEmpty()) {
                location = room.getChild(0);
            }
            if (location != null) {
                return location.getWorldTranslation();
            }
        }
        return null;
    }

    private Node getNode(String roomName, String placeInRoom) {
        if (roomNames.contains(roomName)) {
            Node room = (Node) spatialCoordenates.getChild(roomName);
            for (Spatial place : room.getChildren()) {
                if (place.getName().equals(placeInRoom)) {
                    return (Node) place;
                }
            }
        }
        return null;
    }

    public Vector3f getCoordenates(String roomName, String placeInRoom) {
        if (roomNames.contains(roomName)) {
            Node room = (Node) spatialCoordenates.getChild(roomName);
            for (Spatial place : room.getChildren()) {
                if (place.getName().equals(placeInRoom)) {
                    return place.getWorldTranslation();
                }
            }
        }
        return null;
    }

    public Vector3f getCoordenatesOfSpaceById(String idSpace) {
        Spatial s = spatials.get(idSpace);
        if (s != null) {
            return s.getWorldTranslation();
        }
        return getFirstSubSpaceById(idSpace);
    }

    public Vector3f getFirstSubSpaceById(String idSpace) {
        for (String roomName : roomNames) {
            Node room = (Node) spatialCoordenates.getChild(roomName);
            for (Spatial place : room.getChildren()) {
                if (place.getName().equals(idSpace)) {
                    return place.getWorldTranslation();
                }
            }
        }
        return null;
    }

    public List<Spatial> getObjectsIn(String roomName) {
        List<Spatial> result = new ArrayList<Spatial>();
        Node room = (Node) getSpatialRoom(roomName);
        if (room != null) {
            result.addAll(room.getChildren());
        }
        return result;
    }

    public List<Spatial> getPhyObjecInRoom(String roomName) {
        Spatial room = physicalEntities.getChild(roomName);
        if (room != null && room instanceof Node) {
            return ((Node)room).getChildren();
        }
        return null;
    }

    public Quaternion getrRotationSpace(String idSpace) {
        Spatial s = spatials.get(idSpace);
        if (s != null) {
            return s.getWorldRotation();
        }
        return null;
    }

    public Spatial getSpatialRoom(String roomName) {
        return spatialCoordenates.getChild(roomName);
    }

    public Node getRootNode() {
        return house;
    }

    public NavMesh getNavMesh() {
        if (navMesh == null) {
            Geometry debugNavMesh = (Geometry) logicalEntities
                    .getChild("NavMesh");
            navMesh = new NavMesh(debugNavMesh.getMesh());
        }
        return navMesh;
    }

    public Geometry getNavMeshGeo() {
        return (Geometry) logicalEntities.getChild("NavMesh");
    }

    public String getRoomForObject(String name) {
        List<String> rooms = this.getRoomNames();
        String roomFound = null;
        Iterator<String> iterator = rooms.iterator();
        do {
            String roomName = iterator.next();
            if (this.getObjectsIn(roomName).contains(name)) {
                roomFound = roomName;
            }
        } while (roomFound == null && iterator.hasNext());
        return roomFound;
    }

    public List<Node> getPlaceToPutThings(String furnitureId) {
        List<Node> result = new ArrayList<>();
        Spatial furniture = SpatialUtils.getSpatialById(house, furnitureId);
        if (furniture != null && furniture instanceof Node) {
            Node furNode = (Node) furniture;
            if (furNode.getChild("Places") != null) {
                for (Spatial place : ((Node) furNode.getChild("Places")).getChildren()) {
                    if (((Node) place).getChildren().size() <= 0) {
                        result.add((Node) place);
                    }
                }
            }
        }
        return result;
    }

    public String getClosestPlaceToPutThings(Vector3f ori, String Furniture) {
        System.out.println("\n\n\ngetClosestPlaceToPutThings... " + Furniture + " -> " + ori);
        List<Node> places = getPlaceToPutThings(Furniture);
        System.out.println("places = " + places.size());
        if (!places.isEmpty()) {
            Node closest = null;
            float minDistance = Float.MAX_VALUE;
            for (Node n : places) {
                float d = n.getWorldTranslation().distance(ori);
                System.out.println(n + ": " + d);
                if (d < minDistance) {
                    minDistance = d;
                    closest = n;
                }
            }
            if (closest != null) {
                return closest.getName();
            }
        }
        return null;
    }
}
