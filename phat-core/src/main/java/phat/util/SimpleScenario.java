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
package phat.util;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.light.AmbientLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;

/**
 * Craetes a basic scenario with physics engine activated, adds ligths and
 * sets the cammera in a default position and looking at the center of the 
 * scenario.
 * 
 * @author pablo
 */
public abstract class SimpleScenario extends SimpleApplication {
    boolean physicsDebugging = false;
    
    protected BulletAppState bulletAppState;
    
    @Override
    public void simpleInitApp() {        
        SpatialFactory.init(assetManager, rootNode);
        
        createPhysicsEngineAndAttachItToScene();
        createTerrain();
        createLight();
        createOtherObjects();
        createCameras();
    }
    
    public abstract void createTerrain();
    
    public abstract void createOtherObjects();
    
    protected void createCameras() {
        flyCam.setMoveSpeed(10f);
        flyCam.setDragToRotate(true);// to prevent mouse capture
        cam.setLocation(new Vector3f(7.0357456f, 11.175021f, 5.927986f));
        //cam.setRotation(new Quaternion(-0.3325067f, 0.6662985f, -0.44692048f, -0.49572945f));
        cam.lookAt(rootNode.getWorldBound().getCenter(), Vector3f.UNIT_Y);
    }
    
    protected void createPhysicsEngineAndAttachItToScene() {
        bulletAppState = new BulletAppState(); // physics engine based in jbullet
        bulletAppState.setThreadingType(BulletAppState.ThreadingType.PARALLEL);
        bulletAppState.setEnabled(true);        
        stateManager.attach(bulletAppState);
        
        //bulletAppState.getPhysicsSpace().setAccuracy(1 / 200f);
        if(physicsDebugging) {
            bulletAppState.getPhysicsSpace().enableDebug(assetManager);
        } // to show the collision wireframes
    }
    
    protected void createLight() {
        // We add light so we see the scene
        AmbientLight al = new AmbientLight();
        //al.setColor(ColorRGBA.White.mult(1.3f));
        al.setColor(ColorRGBA.White.mult(0.6f));
        rootNode.addLight(al);
    }

    public boolean isPhysicsDebugging() {
        return physicsDebugging;
    }

    public void setPhysicsDebugging(boolean physicsDebugging) {
        this.physicsDebugging = physicsDebugging;
    }    
}
