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
package phat.audio.util;

import com.aurellem.capture.AurellemSystemDelegate;
import com.aurellem.capture.IsoTimer;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeSystem;
import phat.audio.AudioFactory;
import phat.util.SimpleScenario;

/**
 *
 * @author pablo
 */
public abstract class AudioSimpleScenario extends SimpleScenario {
    protected IsoTimer isoTimer = new IsoTimer(60);
    
    public AudioSimpleScenario() {
        super();
        
        AppSettings s = new AppSettings(true);
        s.setAudioRenderer(AurellemSystemDelegate.SEND);
        JmeSystem.setSystemDelegate(new AurellemSystemDelegate());
        setSettings(s);
        
        setShowSettings(false);
        setPauseOnLostFocus(false);
    }
    
    @Override
    public void simpleInitApp() {
        initAudio();
        
        super.simpleInitApp();
        
        createAudio();
    }
    
    private void initAudio() {        
        setTimer(isoTimer);
        org.lwjgl.input.Mouse.setGrabbed(false);
        
        assetManager.registerLocator("assets", FileLocator.class);
        AudioFactory.init(audioRenderer, assetManager, rootNode);
    }
    
    protected abstract void createAudio();
}
