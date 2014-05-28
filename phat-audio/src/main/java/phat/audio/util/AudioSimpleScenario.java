/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
