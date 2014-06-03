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
package phat.mason.space;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.light.Light;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.jme3.scene.shape.Box;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import phat.audio.AudioFactory;
import phat.audio.PHATAudioSource;
import phat.devices.extractor.Extractor;
import phat.devices.extractor.ExtractorControl;
import phat.structures.houses.House;
import phat.util.SpatialFactory;
import sim.field.continuous.Continuous3D;

/**
 *
 * @author Pablo
 */
public class HouseAdapter {
    
    private House house;
    private PhysicsSpace physicsSpace;
    private Map<String, PhysicsObject> objects = new HashMap<String, PhysicsObject>();
    
    private Continuous3D world;
    
    private Extractor extractor;
    
    public HouseAdapter(PhysicsSpace physicsSpace, House house) {
        this.physicsSpace = physicsSpace;
        this.house = house;
    }
    
    public Continuous3D createSpace() {
        Vector3f dimensions = physicsSpace.getWorldMax().subtract(physicsSpace.getWorldMin());
        world = new Continuous3D(0.01f, dimensions.getX(), dimensions.getY(), dimensions.getZ());
        //world = new Continuous3D(0.01, 10.0, 10.0, 10.0);
        
        for(String roomName: house.getRoomNames()) {
            for(Spatial object: house.getObjectsIn(roomName)) {
                PhysicsObject po = PhysicsObjectFactory.createPhysicsObjectFrom(object, world);
                if(po != null) {
                    objects.put(po.getName(), po);
                    world.setObjectLocation(po, Util.get(PhysicsObjectFactory.getLocation(object)));
                }
            }
        }
        
        //createExtractor();
        //createAudioExtractor();
        
        createFloor(physicsSpace);
        
        return world;
    }
    
    private void createExtractor() {
        extractor = new Extractor();        
        extractor.setLocalTranslation(new Vector3f(2.8095884f, 1.9158201f, 8.075834f));
        extractor.getExtractorControl().switchTo(ExtractorControl.State.HIGH);
        extractor.getExtractorControl().showRange(true);        
        house.getRootNode().attachChild(extractor);
    }
    
    public void createAudioExtractor() {
        PHATAudioSource sound = AudioFactory.getInstance().makeAudioSource("ExtractorAudio", 
                "Sound/Devices/Extractor/extractor-pow3.ogg", Vector3f.ZERO);
        sound.setLooping(true);
        sound.setLocalTranslation(new Vector3f(2.8095884f, 1.9158201f, 8.075834f));

        house.getRootNode().attachChild(sound);
        //audioRenderer.playSource(music);
        sound.setPositional(true);
        sound.setVolume(5f);
        sound.setReverbEnabled(false);
        sound.setDirectional(false);
        sound.setMaxDistance(Float.MAX_VALUE);
        sound.setRefDistance(1f);
        //music.setRolloffFactor(1f);
        //music.setLooping(false);
        
        sound.setShowRange(true);
        sound.play();
    }
    public Extractor getExtractor() {
        return extractor;
    }
    
    public PhysicsObject getObject(String name) {
        return objects.get(name);
    }
    
    private Geometry createFloor(PhysicsSpace physicsSpace) {
        Geometry floor = SpatialFactory.createCube(new Vector3f(11f, 0.1f, 8f), ColorRGBA.Blue);
        CollisionShape wallShape = CollisionShapeFactory.createDynamicMeshShape(floor);
        // Associate a rigid body to the wall so that it is processed by the physical engine 
        // the object mass has to be greater than 0 so that gravity acts on it.
        RigidBodyControl wallBody = new RigidBodyControl(wallShape, 0f);
        floor.addControl(wallBody);
        wallBody.setEnabled(true);
        //wallBody.setFriction(0.5f);
        physicsSpace.add(wallBody);

        //((Node)house.getNode().getChild("PhysicalEntities")).attachChild(floor);

        wallBody.setPhysicsLocation(new Vector3f(0f, -0.11f, 0.5f));
        return floor;

    }
    
    public void switchLight(String roomName, boolean on) {
        Light light = house.getLights(roomName).get(0);
        if(light instanceof PointLight) {
            PointLight pl = (PointLight)light;
            if(on) {
                pl.setRadius(4f);
            } else {
                pl.setRadius(0f);
            }
        }
    }
    
    public House getHouse() {
        return house;
    }

    public Continuous3D getWorld() {
        return world;
    }
    
}
