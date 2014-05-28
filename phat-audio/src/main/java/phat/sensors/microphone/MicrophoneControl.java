/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
    private byte[] buffer;
    private int numBuffers = 100;
    private int bufferSize = 1470;
    private int indexBuffer = 0;
    private boolean initialized = false;
    //private List<AudioBuffer> bufferList;
    private int readIndex = 0;
    private int writeIndex = 0;
    private int numSamples;
    private int BYTE_ARRAY_OUTPUT_STREAM_SIZE;
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
        BYTE_ARRAY_OUTPUT_STREAM_SIZE = bufferSize;
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
            byte[] data = new byte[numSamples];
            audioSamples.get(data);

            MicrophoneData md = new MicrophoneData(data, format);
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
