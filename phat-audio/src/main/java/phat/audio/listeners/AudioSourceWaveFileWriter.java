/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.audio.listeners;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import org.tritonus.sampled.file.WaveAudioOutputStream;
import org.tritonus.share.sampled.file.TDataOutputStream;
import org.tritonus.share.sampled.file.TNonSeekableDataOutputStream;
import phat.sensors.Sensor;
import phat.sensors.SensorData;
import phat.sensors.SensorListener;
import phat.sensors.microphone.MicrophoneData;

/**
 * A listener class of MicrophoneSensor that save the MicrophoneData in a wav file
 * @author pablo
 */
public class AudioSourceWaveFileWriter implements SensorListener {

    public File targetFile;
    private WaveAudioOutputStream wao;
    private TDataOutputStream tos;
    private boolean initialized = false;

    public AudioSourceWaveFileWriter(File targetFile) throws FileNotFoundException {
        tos = new TNonSeekableDataOutputStream(
                new FileOutputStream(targetFile));
    }

    public void init(AudioFormat format) {
        wao = new WaveAudioOutputStream(format, AudioSystem.NOT_SPECIFIED, tos);
    }

    @Override
    public synchronized void update(Sensor source, SensorData sd) {
        if (sd instanceof MicrophoneData) {
            MicrophoneData md = (MicrophoneData) sd;
            if (!initialized) {
                init(md.getAudioFormat());
                initialized = true;
            }
            try {wao.write(md.getData(), 0, md.getData().length);}
		catch (IOException e) {e.printStackTrace();}
        }
    }

    @Override
    public void cleanUp() {
        try {
            wao.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
