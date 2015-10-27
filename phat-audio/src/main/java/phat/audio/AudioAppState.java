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
import com.aurellem.capture.audio.MultiListener;
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
import phat.audio.listeners.XYRMSAudioChart;
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
    Node artificialMic = null;
    boolean newArtificialMic = false;
    XYRMSAudioChart chart;
    boolean showChart = false;

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

        if (artificialMic != null) {
            createArtificialMic();
        }
    }

    private void createArtificialMic() {
        System.out.println("createArtificialMic..." + (app.getAudioRenderer() instanceof MultiListener));
        removePCSpeakerListener();
        currentMicControl = artificialMic.getControl(MicrophoneControl.class);

        Node camFollower = new Node("CamNode");
        // means that the Camera's transform is "copied" to the Transform of the Spatial.
                
        if (currentMicControl == null) {
            currentMicControl = new MicrophoneControl("MicroListening", 10000, app.getAudioRenderer());
        }
        if (pcSpeaker == null) {
            pcSpeaker = new PCSpeaker();
        }
        currentMicControl.add(pcSpeaker);

        if (showChart) {
            chart = new XYRMSAudioChart("Prueba");
            chart.showWindow();
            currentMicControl.add(chart);
        }

        artificialMic.addControl(currentMicControl);
        System.out.println("...createArtificialMic");
        newArtificialMic = false;
    }

    private void checkAndCreatePath(String path) {
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    private void removePCSpeakerListener() {
        if (currentMicControl != null) {
            currentMicControl.remove(pcSpeaker);
            if (chart != null) {
                chart.dispose();
                currentMicControl.remove(chart);
            }
            currentMicControl.getSpatial().removeControl(currentMicControl);
        }
    }

    public void setPCSpeakerTo(Node artificialMic) {
        System.out.println("setPCSpeakerTo... " + artificialMic);
        this.artificialMic = artificialMic;
        newArtificialMic = true;
    }

    public void setShowChart(boolean showChart) {
        this.showChart = showChart;
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);

        if (newArtificialMic) {
            createArtificialMic();
        }
    }

    @Override
    public void cleanup() {
        removePCSpeakerListener();

        super.cleanup();
    }
}
