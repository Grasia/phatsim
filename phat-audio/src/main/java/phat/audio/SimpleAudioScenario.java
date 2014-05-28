/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
