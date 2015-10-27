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

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.audio.AudioRenderer;
import com.jme3.audio.Listener;
import com.jme3.scene.Node;
import phat.util.PHATUtils;

/**
 *
 * @author pablo
 */
public abstract class PHATAudioAppState extends AbstractAppState {

    SimpleApplication app;
    AssetManager assetManager;
    Node rootNode;
    Listener listener;
    
    AudioRenderer audioRenderer;
    boolean pcSpeakerChanged = false;
    Node micNode;

    @Override
    public void initialize(AppStateManager stateManager, Application application) {
        System.out.println("Inititalize " + getClass().getSimpleName());
        super.initialize(stateManager, application);
        this.app = (SimpleApplication) application;
        this.assetManager = application.getAssetManager();
        this.rootNode = app.getRootNode();
        this.audioRenderer = app.getAudioRenderer();
        this.listener = app.getListener();
        
        PHATUtils.checkAndCreatePath("./assets/Sounds/");
        assetManager.registerLocator("assets", FileLocator.class);
        AudioFactory.init(audioRenderer, assetManager, rootNode);
    }

    protected abstract void createMic();
    protected abstract void removeMic();

    public void setPCSpeakerTo(Node artificialMic) {
        System.out.println("setPCSpeakerTo... " + artificialMic);
        this.micNode = artificialMic;
        pcSpeakerChanged = true;
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);

        if (pcSpeakerChanged) {
            createMic();
        }
    }

    @Override
    public void cleanup() {
        removeMic();

        super.cleanup();
    }
}