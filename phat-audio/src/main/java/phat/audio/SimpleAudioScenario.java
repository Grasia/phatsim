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

import com.aurellem.capture.AurellemSystemDelegate;
import com.aurellem.capture.IsoTimer;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeSystem;
import phat.util.SimpleScenario;

/**
 *
 * @author pablo
 */
public abstract class SimpleAudioScenario extends SimpleScenario {

    public SimpleAudioScenario() {
        super();
        
        AppSettings settings = new AppSettings(true);
        settings.setAudioRenderer(AurellemSystemDelegate.SEND);
        JmeSystem.setSystemDelegate(new AurellemSystemDelegate());
        setSettings(settings);
    }
    @Override
    public void simpleInitApp() {
        _initAudio();
        initAudio();
        super.simpleInitApp();
    }
    
    private void _initAudio() {
        
        this.setTimer(new IsoTimer(60));
                
        org.lwjgl.input.Mouse.setGrabbed(false);
        //music = new AudioNode(assetManager, "Sound/Effects/Beep.ogg", false);
        assetManager.registerLocator("assets", FileLocator.class);

        AudioFactory.init(audioRenderer, assetManager, rootNode);
    }
    
    public abstract void initAudio();
}
