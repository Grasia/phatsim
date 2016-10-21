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

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import phat.commands.PHATCommand;
import phat.util.SpatialFactory;
import phat.util.SpatialUtils;
import phat.world.WorldAppState;

/**
 *
 * @author pablo
 */
public class HouseAppState extends AbstractAppState {

    SimpleApplication app;
    AssetManager assetManager;
    BulletAppState bulletAppState;
    WorldAppState worldAppState;
    Node rootNode;
    Map<String, House> houses = new HashMap<>();
    HouseFactory.HouseType houseType = HouseFactory.HouseType.House3room2bath;
    boolean showNavMesh = false;
    ConcurrentLinkedQueue<PHATCommand> runningCommands = new ConcurrentLinkedQueue<>();
    ConcurrentLinkedQueue<PHATCommand> pendingCommands = new ConcurrentLinkedQueue<>();

    @Override
    public void initialize(AppStateManager stateManager, Application application) {
        System.out.println("Inititalize " + getClass().getSimpleName());
        super.initialize(stateManager, application);
        this.app = (SimpleApplication) application;
        this.assetManager = application.getAssetManager();
        this.rootNode = app.getRootNode();

        worldAppState = app.getStateManager().getState(WorldAppState.class);

        SpatialFactory.init(assetManager, rootNode);

        createPhysicsEngineAndAttachItToScene();
        //bulletAppState.setDebugEnabled(true); // to show the collision wireframes

        //setHouseType(houseType);
        setShowNavMesh(showNavMesh);
    }

    public boolean addHouse(String houseId, HouseFactory.HouseType ht) {
        Node place = worldAppState.getFirstHousePlacesFree();
        if (place != null) {
            House house = HouseFactory.createHouse(houseId, ht);
            house.build(place, app);
            houses.put(houseId, house);
            return true;
        }
        return false;
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);

        runningCommands.addAll(pendingCommands);
        pendingCommands.clear();
        for (PHATCommand bc : runningCommands) {
            bc.run(app);
        }
        runningCommands.clear();
    }

    public void runCommand(PHATCommand command) {
        pendingCommands.add(command);
    }

    public HouseFactory.HouseType getHouseType() {
        return houseType;
    }

    public void setShowNavMesh(boolean showNavMesh) {
        this.showNavMesh = showNavMesh;
        for (House house : houses.values()) {
            if (house != null) {
                house.showNavMesh(showNavMesh);
            }
        }
    }

    public boolean isShowNavMesh() {
        return showNavMesh;
    }

    @Override
    public void cleanup() {
        for (House house : houses.values()) {
            if (house != null) {
                house.cleanup(bulletAppState.getPhysicsSpace());
            }
        }

        super.cleanup();
    }

    public House getHouse(String id) {
        return houses.get(id);
    }

    private void createPhysicsEngineAndAttachItToScene() {
        this.bulletAppState = (BulletAppState) app.getStateManager().getState(BulletAppState.class);

        if (this.bulletAppState == null) {
            bulletAppState = new BulletAppState(); // physics engine based in jbullet
            bulletAppState.setThreadingType(BulletAppState.ThreadingType.PARALLEL);
            bulletAppState.setEnabled(true);
            app.getStateManager().attach(bulletAppState);
            //bulletAppState.getPhysicsSpace().setAccuracy(1 / 120f);
            //bulletAppState.getPhysicsSpace().enableDebug(assetManager); // to show the collision wireframes
        }

    }

    public Iterable<House> getHouses() {
        return houses.values();
    }

    public House getHouse(Spatial spatial) {
        Vector3f center = SpatialUtils.getCenterBoinding(spatial);
        for (House house : houses.values()) {
            Node node = house.getRootNode();
            /*node.updateModelBound();
            if(node.getWorldBound().contains(spatial.getWorldTranslation())) {
                return house;
            }*/
            Vector3f min = SpatialUtils.getMinBounding(node);
            Vector3f max = SpatialUtils.getMaxBounding(node);
            if (min.x <= center.x && min.y <= center.y && min.z <= center.z
                    && max.x >= center.x && max.y >= center.y && max.z >= center.z) {
                return house;
            }
        }/*
        Spatial parent = spatial.getParent();
        while (parent != null) {
            String role = parent.getUserData("ROLE");
            if (role != null && role.equals("House")) {
                String id = parent.getUserData("ID");
                if (id != null) {
                    return houses.get(id);
                }
            }
            parent = parent.getParent();
        }*/
        return null;
    }

    public String getRoomNameLocation(String objID) {
        if (!houses.isEmpty()) {
            return houses.get("House1").getRoomNameLocation(objID);
        }
        return null;
    }
}
