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
package phat.audio;

import com.aurellem.capture.IsoTimer;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.bullet.BulletAppState;
import com.jme3.scene.Node;
import java.io.File;

import phat.audio.listeners.PCSpeaker;
import phat.sensors.microphone.MicrophoneControl;

/**
 *
 * @author pablo
 */
public class AudioAppState extends AbstractAppState {

    SimpleApplication app;
    AssetManager assetManager;
    BulletAppState bulletAppState;
    Node rootNode;
    PCSpeaker pcSpeaker;
    MicrophoneControl currentMicControl;
    boolean artificialMic = false;

    @Override
    public void initialize(AppStateManager stateManager, Application application) {
        System.out.println("Inititalize " + getClass().getSimpleName());
        super.initialize(stateManager, application);
        this.app = (SimpleApplication) application;
        this.assetManager = application.getAssetManager();
        this.rootNode = app.getRootNode();
        
        checkAndCreatePath("./assets/Sounds/");
        assetManager.registerLocator("assets", FileLocator.class);
        AudioFactory.init(app.getAudioRenderer(), assetManager, rootNode);
    }
    
    private void checkAndCreatePath(String path) {
        File folder = new File(path);
        if(!folder.exists()) {
            folder.mkdirs();
        }
    }

    private void removePCSpeakerListener() {
        if (currentMicControl != null && pcSpeaker != null
                && currentMicControl.hasListener(pcSpeaker)) {
            currentMicControl.remove(pcSpeaker);
            if (artificialMic) {
                currentMicControl.getSpatial().removeControl(currentMicControl);
                artificialMic = false;
            }
        }
    }

    public void setPCSpeakerTo(Node node) {
        System.out.println("setPCSpeakerTo");
        removePCSpeakerListener();
        MicrophoneControl micControl = node.getControl(MicrophoneControl.class);

        if (micControl == null) {
            artificialMic = true;
            System.out.println("New microphone!");
            micControl = new MicrophoneControl("MicroListening", 10000, app.getAudioRenderer());
        }
        if (pcSpeaker == null) {
            System.out.println("New microphone!");
            pcSpeaker = new PCSpeaker();
        }
        micControl.add(pcSpeaker);
        node.addControl(micControl);
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
    }

    @Override
    public void cleanup() {
        removePCSpeakerListener();
        
        super.cleanup();
    }
}
