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

import com.aurellem.capture.audio.MultiListener;
import com.aurellem.capture.audio.SoundProcessor;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioParam;
import com.jme3.audio.AudioRenderer;
import com.jme3.audio.AudioSource;
import com.jme3.audio.Environment;
import com.jme3.audio.Filter;
import com.jme3.audio.Listener;
import com.jme3.audio.ListenerParam;

/**
 *
 * @author pablo
 */
public class PHATAudioRenderer implements AudioRenderer, MultiListener {
    AudioRenderer audioRenderer;

    public PHATAudioRenderer(AudioRenderer audioRenderer) {
        this.audioRenderer = audioRenderer;
    }

    @Override
    public void setListener(Listener ll) {
        audioRenderer.setListener(ll);
    }

    @Override
    public void setEnvironment(Environment e) {
        audioRenderer.setEnvironment(e);
    }

    @Override
    public void playSourceInstance(AudioSource as) {
        changeAudio(as);
        audioRenderer.playSourceInstance(as);
    }

    @Override
    public void playSource(AudioSource as) {
        changeAudio(as);
        audioRenderer.playSource(as);
    }
    
    private void changeAudio(AudioSource as) {
        if(as instanceof AudioNode) {
            AudioNode an = (AudioNode) as;
            an.setRefDistance(0.1f);
            an.setMaxDistance(1000f);
        }
        audioRenderer.playSourceInstance(as);
    }

    @Override
    public void pauseSource(AudioSource as) {
        audioRenderer.pauseSource(as);
    }

    @Override
    public void stopSource(AudioSource as) {
        audioRenderer.stopSource(as);
    }

    @Override
    public void updateSourceParam(AudioSource as, AudioParam ap) {
        audioRenderer.updateSourceParam(as, ap);
    }

    @Override
    public void updateListenerParam(Listener ll, ListenerParam lp) {
        audioRenderer.updateListenerParam(ll, lp);
    }

    @Override
    public void deleteFilter(Filter filter) {
        audioRenderer.deleteFilter(filter);
    }

    @Override
    public void deleteAudioData(AudioData ad) {
        audioRenderer.deleteAudioData(ad);
    }

    @Override
    public void initialize() {
        audioRenderer.initialize();
    }

    @Override
    public void update(float f) {
        audioRenderer.update(f);
    }

    @Override
    public void cleanup() {
        audioRenderer.cleanup();
    }

    @Override
    public void addListener(Listener ll) {
        ((MultiListener)audioRenderer).addListener(ll);
    }

    @Override
    public void registerSoundProcessor(Listener ll, SoundProcessor sp) {
        ((MultiListener)audioRenderer).registerSoundProcessor(ll, sp);
    }

    @Override
    public void registerSoundProcessor(SoundProcessor sp) {
        ((MultiListener)audioRenderer).registerSoundProcessor(sp);
    }
    
    
}
