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
