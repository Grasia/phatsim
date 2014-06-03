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
