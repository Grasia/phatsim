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
package phat.sensors.microphone;

import com.aurellem.capture.audio.MultiListener;
import com.aurellem.capture.audio.SoundProcessor;
import com.jme3.audio.AudioRenderer;
import com.jme3.audio.Listener;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import javax.sound.sampled.AudioFormat;
import phat.sensors.Sensor;
import phat.sensors.SensorData;
import phat.sensors.SensorListener;

/**
 *
 * @author Pablo
 */
public class MicrophoneControl extends Sensor implements SoundProcessor {

    Control c;
    
    //private List<AudioBuffer> bufferList;
    private int readIndex = 0;
    private int writeIndex = 0;
    private final int BUFFER_SIZE= 1470;
    private final int NUM_BUFFERS = 2;
    private int buf_i = 0;
    private byte[][] buffer = new byte[NUM_BUFFERS][BUFFER_SIZE];
    private AudioRenderer audioRenderer;
    private Listener listener = new Listener();
    private List<NotifyTask<Void>> callables = new ArrayList<NotifyTask<Void>>();

    /*
    @Override
    public void add(SensorListener sl) {
        super.add(sl);

        callables.add(new NotifyTask(this, sl));
    }*/

    public MicrophoneControl(String name, int bufferSize, AudioRenderer audioRenderer) {
        super(name);
        System.out.println("MicrophoneControl created!!");
        this.audioRenderer = audioRenderer;
        //buffer = new byte[bufferSize];
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (!enabled) {
            readIndex = 0;
            writeIndex = 0;
        }
    }

    private void concurrentNotification(final MicrophoneData md) {
        ForkJoinPool pool = new ForkJoinPool();

        for (NotifyTask n : callables) {
            n.setMicrophoneData(md);
        }

        pool.invokeAll(callables);
    }
    
    private void serialNotification(final Sensor s, MicrophoneData md) {
        for (SensorListener sl : listeners) {
            sl.update(s, md);
        }
    }

    class NotifyTask<Void> implements Callable<Void> {

        SensorListener sensorListener;
        Sensor sensor;
        MicrophoneData sensorData;

        public NotifyTask(Sensor sensor, SensorListener sl) {
            this.sensorListener = sl;
            this.sensor = sensor;
        }

        public void setMicrophoneData(MicrophoneData sensorData) {
            this.sensorData = sensorData;
        }

        @Override
        public Void call() throws Exception {
            sensorListener.update(sensor, sensorData);
            return null;
        }
    }
    
    @Override
    public void process(ByteBuffer audioSamples, int numSamples, AudioFormat format) {
        
        if (enabled) {
            audioSamples.clear();
            int numBytes = (numSamples > BUFFER_SIZE) ? BUFFER_SIZE : numSamples;
            audioSamples.get(buffer[buf_i], 0, numBytes);
            
            MicrophoneData md = new MicrophoneData(buffer[buf_i], format);
            //concurrentNotification(md);
            serialNotification(MicrophoneControl.this, md);

            audioSamples.clear();
            /*
             new Thread() {
             public void run() {
             for(SensorListener sl: listeners)
             sl.update(MicrophoneControl.this, md);
             }
             }.start();*/
            buf_i = (buf_i+1) % NUM_BUFFERS;
        }
    }

    public synchronized void updateReadIndex(int readedBytes) {
        readIndex += readedBytes;
    }

    public synchronized boolean areDiferents() {
        return readIndex != writeIndex;
    }

    @Override
    public Control cloneForSpatial(Spatial spatial) {
        return null;
    }
    
    private boolean init = false;

    private void init() {
        if (audioRenderer instanceof MultiListener) {
            MultiListener rf = (MultiListener) audioRenderer;
            rf.addListener(listener);
            rf.registerSoundProcessor(listener, this);
        }
        init = true;
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (!init) {
            init();
        }
        if (listener != null) {
            listener.setLocation(spatial.getWorldTranslation());
            listener.setRotation(spatial.getWorldRotation());
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    @Override
    public void cleanup() {
        super.cleanUp();
        buffer = null;
        listener = null;
    }
}
