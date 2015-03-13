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
package phat.mason.agents;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Image;
import com.jme3.texture.Texture2D;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phat.agents.actors.BasicActor;
import phat.agents.actors.parkinson.HeadTremblingControl;
import phat.controls.animation.BasicCharacterAnimControl;
import phat.controls.animation.PatientAnimControl;
import phat.devices.smartphone.SmartPhoneFactory;
import phat.mason.PHATSimState;
import phat.mason.space.HouseAdapter;
import phat.mason.space.PhysicsObject;
import phat.mason.space.Util;
import phat.mobile.adm.AndroidVirtualDevice;
import phat.sensors.camera.CameraSensor;
import phat.sensors.microphone.MicrophoneControl;
import phat.server.PHATServerManager;
import phat.server.camera.TCPCameraSensorServer;
import phat.server.microphone.TCPAudioMicroServer;
import phat.util.SpatialFactory;
import sim.util.Double3D;

/**
 *
 * @author pablo
 */
public class ActorAdapter  {

    private PHATSimState state;
    private Map<String, PhysicsActor> actors = new HashMap<String, PhysicsActor>();
    private List<ActorCollisionListener> collisionListeners = new ArrayList<ActorCollisionListener>();

    PHATServerManager serverManager;
    public ActorAdapter(PHATSimState state) {
        this.state = state;
        serverManager = new PHATServerManager();
    }

    public PhysicsActor createPatient(String name,
            Double3D location) {
    	/*HouseAdapter houseAdapter = state.getMasonAppState().getHouseAdapter();
        BasicActor basicActor = NavActorFactory.createBasicActorWithNavigation(
                name,
                "Models/People/Elder/Elder.j3o",
                Util.get(location).add(0f, 0.5f, 0f),
                0.2f,
                0.5f,
                0.1f,
                PatientAnimControl.class,
                houseAdapter.getHouse().getRootNode());

        System.out.println("Patient: BasicActor created!!!");
        
        //Node smartphone1 = createSmartphone("Smartphone1", "emulator-5556", "CameraCapturePatientActivity");
        //rootNode.attachChild(smartphone1);
        
        //basicActor.pickUp(smartphone1, true);
        
        PhysicsActorImpl pai = new PhysicsActorImpl(basicActor, houseAdapter.getWorld());
        basicActor.getNode().addControl(pai);
        
        System.out.println("Patient: PhysicsActorImpl created!!!");
        
        houseAdapter.getWorld().setObjectLocation(pai, location);

        HeadTremblingControl headtc = new HeadTremblingControl();
        basicActor.getNode().addControl(headtc);
        
        actors.put(pai.getName(), pai);

        return pai;*/
        return null;
    }

    public PhysicsActor createRelative(String name,
            Double3D location) {
        /*
    	HouseAdapter houseAdapter = state.getMasonAppState().getHouseAdapter();
        BasicActor basicActor = NavActorFactory.createBasicActorWithNavigation(
                name,
                "Models/People/Male/Male.j3o",
                Util.get(location).add(0f, 0.5f, 0f),
                0.2f,
                1.0f,
                0.1f,
                BasicCharacterAnimControl.class,
                houseAdapter.getHouse().getRootNode());
        
        //Node smartphone1 = createSmartphone("Smartphone2", "emulator-5554", "CameraCaptureRelativeActivity");
        //rootNode.attachChild(smartphone1);
        
        //basicActor.pickUp(smartphone1, true);
        
        PhysicsActorImpl pai = new PhysicsActorImpl(basicActor, houseAdapter.getWorld());
        basicActor.getNode().addControl(pai);
        houseAdapter.getWorld().setObjectLocation(pai, location);

        actors.put(pai.getName(), pai);

        return pai;
        */
        return null;
    }
    
    private Node createSmartphone(String smartphoneId, Vector3f dimensions, String emulatorId, String activity) {
        Node smartphone = SmartPhoneFactory.createSmartphone(smartphoneId, dimensions);
        SmartPhoneFactory.enableCameraFacility(smartphone);
        
        CameraSensor cameraSensor = smartphone.getControl(CameraSensor.class);
        
        TCPCameraSensorServer cameraServer = serverManager.createAndStartCameraServer(smartphone.getName(), cameraSensor);
        cameraServer.setRate(0.1f);
        
        AndroidVirtualDevice avd = new AndroidVirtualDevice(smartphone.getName(), 
        		emulatorId, smartphone.getName());
        System.err.println("Despues.................");
        avd.sendConfigFileForService(serverManager.getIP(), serverManager.getPort());
        System.out.println("avd = " + avd);
        System.out.println("unlock()");
        //avd.unlock();
        System.out.println("startActivity");
        avd.startActivity("phat.android.apps", activity);
        //avd.startActivity("phat.android.app.mic", "MainActivity");
        // empieza a procesar audio
        //System.out.println("press start button!");
        //avd.tap(45, 193);

        return smartphone;
    }
    
    public Node createAudioSmartphone(String smartphoneId, String emulatorId) {
        Node smartphone = SmartPhoneFactory.createSmartphone(smartphoneId, new Vector3f(0.048f, 0.08f, 0.002f));
        
        smartphone.getControl(RigidBodyControl.class).setEnabled(false);
        
        smartphone.setLocalTranslation(5.1096067f, 1.0f, 3.361808f);
        
        SmartPhoneFactory.enableMicrophoneFacility(smartphone);
        MicrophoneControl micControl = smartphone.getControl(MicrophoneControl.class);
        TCPAudioMicroServer audioServer = serverManager.createAndStartAudioMicroServer(smartphone.getName(), micControl);
        AndroidVirtualDevice avd = new AndroidVirtualDevice(smartphone.getName(), 
        		emulatorId, smartphone.getName());
        System.err.println("Despues.................");
        avd.sendConfigFileForService(serverManager.getIP(), serverManager.getPort());
        System.out.println("avd = " + avd);
        System.out.println("unlock()");
        //avd.unlock();
        System.out.println("startActivity");
        avd.startActivity("phat.android.apps", "AudioLevelXYPlotActivity");
        //avd.startActivity("phat.android.app.mic", "MainActivity");
        // empieza a procesar audio
        //System.out.println("press start button!");
        //avd.tap(45, 193);
        SpatialFactory.getRootNode().attachChild(smartphone);

        return smartphone;
    }
    
    public PhysicsActor getActor(String name) {
        return actors.get(name);
    }

   
    public void collision(String obj1, String obj2) {
    	HouseAdapter houseAdapter = state.getMasonAppState().getHouseAdapter();
        if (obj1 != null && obj2 != null) {
            //System.out.println("collision = "+obj1+", "+obj2);
            PhysicsActor pa = getActor(obj1);
            if (pa == null) {
                pa = getActor(obj2);
                if (pa != null) {
                    PhysicsObject po = houseAdapter.getObject(obj1);
                    if (po != null) {
                        notifyCollision(pa, po);
                    }
                }
            } else {
                PhysicsObject po = houseAdapter.getObject(obj2);
                if (po != null) {
                    notifyCollision(pa, po);
                }
            }
        }
    }

    public void register(ActorCollisionListener actorCollisionListener) {
        collisionListeners.add(actorCollisionListener);
    }

    private void notifyCollision(PhysicsActor pa, PhysicsObject po) {
        for (ActorCollisionListener acl : collisionListeners) {
            acl.collision(pa, po);
        }
    }
}
