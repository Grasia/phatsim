package phat.config.impl;

import phat.audio.AudioAppState;
import phat.config.AudioConfigurator;

import com.aurellem.capture.AurellemSystemDelegate;
import com.aurellem.capture.IsoTimer;
import com.jme3.app.SimpleApplication;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeSystem;

public class AudioConfiguratorImpl implements AudioConfigurator {

    boolean multiAudioRenderer;
    AudioAppState audioAppState;

    public AudioConfiguratorImpl(AudioAppState audioAppState) {
        this.audioAppState = audioAppState;
    }

    @Override
    public void setMultiAudioRenderer(boolean multiAudioRenderer, SimpleApplication app) {
        this.multiAudioRenderer = multiAudioRenderer;

        if (multiAudioRenderer) {
            AppSettings s = new AppSettings(true);
            s.setAudioRenderer(AurellemSystemDelegate.SEND);
            JmeSystem.setSystemDelegate(new AurellemSystemDelegate());
            app.setSettings(s);
            app.setTimer(new IsoTimer(180f));
            org.lwjgl.input.Mouse.setGrabbed(false);
        } else {
            AppSettings s = new AppSettings(true);
            s.setAudioRenderer("LWJGL");
            JmeSystem.setSystemDelegate(new AurellemSystemDelegate());
            app.setSettings(s);
        }
    }

    public AudioAppState getAudioAppState() {
        return audioAppState;
    }
}
