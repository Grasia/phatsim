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
package phat.devices.smartphone;

import com.jme3.audio.AudioRenderer;
import com.jme3.bullet.control.GhostControl;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import phat.sensors.microphone.MicrophoneControl;
//import es.um.ubiksim.sensors.microphone.MicrophoneControl;

/**
 *
 * @author Pablo
 */
public class SmartPhone extends Node {
    private AudioRenderer audioRenderer;
    
    //private AccelerometerControl accelerometerControl;
    private MicrophoneControl microphoneControl;
    
    private GhostControl ghostControl;
    
    //private RigidBodyControl rigidBodyControl;    
    
    public SmartPhone(String name, Spatial model) {
        super(name);
        
        attachChild(model);
        
        /*
        CollisionShape modelShape = CollisionShapeFactory.createBoxShape(geo);        
        ghostControl = new GhostControl(modelShape);
        addControl(ghostControl);
        getAppPhysicsSpace().add(ghostControl);*/
        
        /*
        RigidBodyControl rigidBodyControl = new RigidBodyControl(modelShape, 0f);
        rigidBodyControl.setKinematic(false);
        model.addControl(rigidBodyControl);
        getAppPhysicsSpace().add(rigidBodyControl);
        */
        
        /*accelerometerControl = new AccelerometerControl(this);        
        addControl(accelerometerControl);
        /*
        microphoneControl = new MicrophoneControl(this, 10000, audioRenderer);
        microphoneControl.start();*/
    }
    
    public void setMicro(MicrophoneControl micro) {
        this.microphoneControl = micro;
        addControl(micro);
    }
    
    public void activatePhysics(boolean activate) {
        /*if(activate) {
            model.removeControl(ghostControl);
            getAppPhysicsSpace().add(ghostControl);
        } else {
            model.addControl(ghostControl);
            getAppPhysicsSpace().remove(rigidBodyControl);
        }*/
    }
}
