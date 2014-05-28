/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
