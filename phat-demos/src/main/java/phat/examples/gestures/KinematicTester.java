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
package phat.examples.gestures;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import phat.agents.actors.ActorFactory;
import phat.util.Debug;
import phat.util.SimpleScenario;

/**
 *
 * @author pablo
 */
public class KinematicTester extends SimpleScenario {

    public static void main(String[] args) {
        KinematicTester app = new KinematicTester();
        //app.setDisplayFps(false);
        app.setShowSettings(false);
        
        //app.setPauseOnLostFocus(false);
        app.start();
    }
    
    Node model;
    Quaternion angular;
    
    @Override
    public void createTerrain() {
       Debug.enableDebugGrid(10,assetManager,rootNode);
       
       Vector3f direction = Vector3f.UNIT_X;
       
       ActorFactory.init(rootNode, assetManager, bulletAppState);
        model = ActorFactory.createActorModel("Patient", "Models/People/Elder/Elder.j3o", 0.9f);
        
        rootNode.attachChild(model);
        
        Quaternion q0 = new Quaternion(model.getLocalRotation());
        System.out.println("Rotation = "+q0);
        
        model.lookAt(direction, Vector3f.UNIT_Y);
        
        Quaternion q1 = model.getLocalRotation();
        System.out.println("Rotation = "+q0+" -> "+q1);
        
        angular = q1.subtract(q0);
        System.out.println("Angular = "+angular);
    }

    @Override
    public void simpleUpdate(float fps) {
        //System.out.println("\nAngular = "+angular);
        //System.out.println("Rotation = "+angular.mult(fps));
        //System.out.println("FinalRotation = "+model.getLocalRotation().add(angular.mult(fps)));
        
        //model.setLocalRotation(model.getLocalRotation().subtract(angular.mult(fps)));
        Vector3f direction = model.getLocalRotation().mult(Vector3f.UNIT_Z);
        //model.lookAt(angular.mult(fps).mult(direction), Vector3f.UNIT_Y);        
               
        
        float [] v = {FastMath.QUARTER_PI*fps, FastMath.QUARTER_PI*fps, FastMath.QUARTER_PI*fps};
        
        Quaternion q = new Quaternion();
        q = q.fromAngles(v);
        
        System.out.println("Rotation = "+q.mult(fps));
        model.rotate(angular.mult(fps));
    }
    
    @Override
    public void createOtherObjects() {
        
    }
    
}
