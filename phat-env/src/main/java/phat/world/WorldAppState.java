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
package phat.world;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.font.BitmapFont;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.shadow.EdgeFilteringMode;
/*import com.jme3.shadow.DirectionalLightShadowFilter;
 import com.jme3.shadow.DirectionalLightShadowRenderer;
 import com.jme3.shadow.EdgeFilteringMode;
 import com.jme3.shadow.SpotLightShadowFilter;
 import com.jme3.shadow.SpotLightShadowRenderer;*/
import com.jme3.system.AppSettings;
import java.util.ArrayList;
import java.util.List;
import phat.util.SpatialFactory;
import phat.util.SpatialUtils;

/**
 * Represent the natural environment of the simulation, i.e., the ground, the
 * sky, the time and the light.
 *
 * @author pablo
 */
public class WorldAppState extends AbstractAppState {

    public enum LandType {

        Basic, Grass, TwoHouses
    }
    SimpleApplication app;
    AssetManager assetManager;
    BulletAppState bulletAppState;
    Node rootNode;
    AppSettings settings;
    BitmapFont guiFont;
    PHATCalendar calendar;          // Calendar that register the simulated time.
    boolean visibleCalendar = true;
    int year = 2013;
    int month = 1;
    int dayOfMonth = 1;
    int hour = 4;
    int minute = 0;
    int second = 0;
    boolean enableShadows = false;
    Spatial sky;
    DirectionalLight sun;
    AmbientLight ambientLight;
    LandType landType = LandType.Basic;
    ColorRGBA terrainColor = ColorRGBA.Brown;
    public static final int SHADOWMAP_SIZE = 1024;
    private DirectionalLightShadowRenderer dlsr;
    private DirectionalLightShadowFilter dlsf;

    private void initShadow() {
        Node world = (Node) rootNode.getChild("World");
        world.getChild("terrain").setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        //TangentBinormalGenerator.generate(world);

        dlsr = new DirectionalLightShadowRenderer(assetManager, SHADOWMAP_SIZE, 3);
        dlsr.setLight(sun);
        dlsr.setLambda(0.55f);
        dlsr.setShadowIntensity(0.6f);
        dlsr.setEdgeFilteringMode(EdgeFilteringMode.Nearest);
        //dlsr.displayDebug();
        app.getViewPort().addProcessor(dlsr);

        dlsf = new DirectionalLightShadowFilter(assetManager, SHADOWMAP_SIZE, 3);
        dlsf.setLight(sun);
        dlsf.setLambda(0.55f);
        dlsf.setShadowIntensity(0.6f);
        dlsf.setEdgeFilteringMode(EdgeFilteringMode.Nearest);
        dlsf.setEnabled(false);

        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        fpp.addFilter(dlsf);

        /*final SpotLightShadowRenderer slsr = new SpotLightShadowRenderer(assetManager, 512);
         slsr.setLight(spot);       
         slsr.setShadowIntensity(0.5f);
         slsr.setEdgeFilteringMode(EdgeFilteringMode.PCFPOISSON);   
         app.getViewPort().addProcessor(slsr);

         SpotLightShadowFilter slsf = new SpotLightShadowFilter(assetManager, 512);
         slsf.setLight(spot);    
         slsf.setShadowIntensity(0.5f);
         slsf.setEdgeFilteringMode(EdgeFilteringMode.PCFPOISSON);  
         slsf.setEnabled(false);
        
         fpp.addFilter(slsf);*/

        app.getViewPort().addProcessor(fpp);
    }

    private void initLight() {
        sun = new DirectionalLight();
        sun.setDirection(new Vector3f(0f, -1f, 0f));
        rootNode.addLight(sun);

        ambientLight = new AmbientLight();
        ambientLight.setColor(ColorRGBA.White.mult(0.5f));
        rootNode.addLight(ambientLight);
    }

    private void initSky() {
        Node world = (Node) rootNode.getChild("World");
        sky = world.getChild("Sky");
        app.getViewPort().setBackgroundColor(ColorRGBA.Cyan);
        if (sky != null) {
            sky.removeFromParent();
        }
        if (sky == null) {
            //Texture skyTexture = assetManager.loadTexture("Textures/Sky/SkySphere.png");
            //sky = SkyFactory.createSky(assetManager, skyTexture, true);
            //sky.rotate(0f, 0f, FastMath.PI);
            //sky = SkyFactory.createSky(assetManager, "Textures/Sky/Bright/BrightSky.dds", false);
            //sky = SkyFactory.createSky(assetManager, "Textures/Sky/Bright/FullskiesBlueClear03.dds", false);
            //rootNode.attachChild(sky);
        }
    }

    /**
     * It simulates the sun movement depending on the time (calendar).
     *
     * @param tpf
     */
    private void updateLight(float tpf) {
        if (sun != null && calendar != null) {
            hour = calendar.getHourOfDay();
            minute = calendar.getMinute();

            float xAngle = 0f;
            float yAngle = 0f;
            float zAngle = 0f;
            if (12 > hour && hour >= 6) {
                yAngle = -(hour - 6f) * (1f / 6f);
                yAngle -= minute * (1f / (6f * 60f));
                xAngle = 1f + yAngle;
                zAngle = -(0.2f + yAngle * 0.8f);
            } else if (18 > hour && hour >= 12) {
                yAngle = -(18f - hour) * (1f / 6f);
                yAngle += minute * (1f / (6f * 60f));
                xAngle = -1f - yAngle;
                zAngle = -(0.2f + yAngle * 0.8f);
            }
            sun.getDirection().set(xAngle, yAngle, zAngle);
            //System.out.println(hour+":"+minute+" -> "+sun.getDirection());
            sun.getDirection().normalizeLocal();
        }
    }

    @Override
    public void initialize(AppStateManager stateManager, Application application) {
        System.out.println("Inititalize " + getClass().getSimpleName());
        super.initialize(stateManager, application);
        this.app = (SimpleApplication) application;
        this.assetManager = application.getAssetManager();
        this.rootNode = app.getRootNode();

        SpatialFactory.init(assetManager, rootNode);

        if (app.getContext() != null) {
            this.settings = app.getContext().getSettings();
        }

        createPhysicsEngineAndAttachItToScene();

        initCalendar();
        initLand();
        initHousePlaces();
        initLight();
        initSky();
        if (app.getContext() != null && enableShadows) {
            initShadow();
        }
    }

    private void initCalendar() {
        guiFont = assetManager.loadFont("Interface/Fonts/Console.fnt");
        if (calendar == null) {
            System.out.println("Create calendar!!!!");
            createCalendar();
        }
    }

    private Node createBasicLand() {
        Node world = (Node) assetManager.loadModel("Scenes/Lands/Sky.j3o");
        Spatial terrain = SpatialFactory.createCube(new Vector3f(100f, 0.1f, 100f), terrainColor);
        terrain.move(0f, -0.15f, 0f);
        terrain.setName("terrain");
        terrain.addControl(new RigidBodyControl(0f));
        world.attachChild(terrain);

        return world;
    }

    private void initLand() {
        Node world = null;
        if (landType.equals(LandType.Basic)) {
            world = createBasicLand();
        } else if (landType.equals(LandType.Grass)) {
            world = (Node) assetManager.loadModel("Scenes/Lands/BasicGrassLand.j3o");
        } else if (landType.equals(LandType.TwoHouses)) {
            world = (Node) assetManager.loadModel("Scenes/Lands/Land_2Houses.j3o");
            activatePhysics(world);
        }
        if (world != null) {
            this.bulletAppState.getPhysicsSpace().addAll(world.getChild("terrain"));
            rootNode.attachChild(world);
        }
    }
    List<Node> housePlaces = new ArrayList<>();

    private void initHousePlaces() {
        Node world = (Node) rootNode.getChild("World");
        Spatial houses = world.getChild("Houses");
        if (houses == null) {
            houses = new Node("Houses");
            Node house1 = new Node("House1");
            house1.setUserData("ID", "HousePlace1");
            house1.setUserData("ROLE", "HousePlace");
            ((Node) houses).attachChild(house1);
            world.attachChild(houses);
        }
        for (Spatial housePlace : ((Node) houses).getChildren()) {
            housePlaces.add((Node) housePlace);
        }
    }

    public List<Node> getHousePlaces() {
        return housePlaces;
    }

    public Node getFirstHousePlacesFree() {
        for (Node housePlace : housePlaces) {
            if (housePlace.getChildren().isEmpty()) {
                return housePlace;
            }
        }
        return null;
    }

    private void activatePhysics(Node rootNode) {
        List<Spatial> trees = SpatialUtils.getSpatialsByRole(rootNode, "Tree");
        for (Spatial tree : trees) {
            Spatial trunk = ((Node) tree).getChild("Trunk");
            if (trunk != null) {
                MeshCollisionShape level_shape = new MeshCollisionShape(((Geometry) trunk).getMesh());
                RigidBodyControl rbc = new RigidBodyControl(level_shape, 0f);
                rbc.setPhysicsLocation(trunk.getWorldTranslation());
                //rbc.setPhysicsRotation(Quaternion.ZERO);
                ((Node) tree).getChild("Trunk").addControl(rbc);
                this.bulletAppState.getPhysicsSpace().add(trunk);
            }
        }
    }

    public void setVisibleCalendar(boolean visibleCalendar) {
        this.visibleCalendar = visibleCalendar;
    }

    public boolean isVisibleCalendar() {
        return this.visibleCalendar;
    }
    private float milliCounter = 0f;
    private int speedFactor = 1;

    @Override
    public void update(float tpf) {
        super.update(tpf);

        tpf *= speedFactor;

        milliCounter += tpf;
        long totalMillis = 0;
        while (milliCounter > 1f) {
            milliCounter -= 1f;
            totalMillis += 1000;
        }
        calendar.setTimeInMillis(calendar.getTimeInMillis() + totalMillis);

        updateLight(tpf);
    }

    @Override
    public void cleanup() {
        rootNode.removeLight(sun);
        rootNode.removeLight(ambientLight);

        if (sky != null) {
            sky.removeFromParent();
        }

        super.cleanup();
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

    public int getSpeedFactor() {
        return speedFactor;
    }

    public void setSpeedFactor(int speedFactor) {
        this.speedFactor = speedFactor;
    }

    public PHATCalendar getCalendar() {
        return calendar;
    }

    private void createCalendar() {
        calendar = new PHATCalendar(year, month, dayOfMonth, hour, minute, second);
    }

    /**
     * It sets the simulation time.
     *
     * @param year
     * @param month
     * @param dayOfMonth
     * @param hour
     * @param minute
     * @param second
     */
    public void setCalendar(int year, int month, int dayOfMonth, int hour, int minute, int second) {
        this.year = year;
        this.month = month;
        this.dayOfMonth = dayOfMonth;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }

    public void setYear(int year) {
        this.year = year;
        createCalendar();
    }

    public int getYear() {
        this.year = calendar.getYear();
        return this.year;
    }

    public void setMonth(int month) {
        this.month = month;
        createCalendar();
    }

    public int getMonth() {
        this.month = calendar.getMonth();
        return this.month;
    }

    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
        createCalendar();
    }

    public int getDayOfMonth() {
        this.dayOfMonth = calendar.getDayOfMonth();
        return this.dayOfMonth;
    }

    public void setHour(int hour) {
        this.hour = hour;
        createCalendar();
    }

    public int getHour() {
        this.hour = calendar.getHourOfDay();
        return this.hour;
    }

    public void setMinute(int minute) {
        this.minute = minute;
        createCalendar();
    }

    public int getMinute() {
        this.minute = calendar.getMinute();
        return this.minute;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
        createCalendar();
    }

    public Node getWorldNode() {
        return (Node) rootNode.getChild("World");
    }

    public Node getTerrain() {
        return (Node) getWorldNode().getChild("terrain");
    }

    public Vector3f getWorldMax() {
        return SpatialUtils.getMaxBounding(rootNode);
    }

    public Vector3f getWorldMin() {
        return SpatialUtils.getMinBounding(rootNode);
    }

    public boolean isEnableShadows() {
        return this.enableShadows;
    }

    public void setEnableShadows(boolean enabled) {
        this.enableShadows = enabled;
    }

    public LandType getLandType() {
        return landType;
    }

    public void setLandType(LandType landType) {
        this.landType = landType;
    }

    public ColorRGBA getTerrainColor() {
        return terrainColor;
    }

    public void setTerrainColor(ColorRGBA terrainColor) {
        this.terrainColor = terrainColor;
    }
}