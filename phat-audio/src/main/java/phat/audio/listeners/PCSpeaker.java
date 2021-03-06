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

import phat.sensors.Sensor;
import phat.sensors.SensorData;
import phat.sensors.SensorListener;
import phat.sensors.microphone.MicrophoneData;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import phat.sensors.microphone.MicrophoneControl;

/**
 * A listener class of MicrophoneSensor that sends the audio to the speakers.
 *
 * @TODO It is a really slow way to listen a scenario. Find other methods.
 * (OpenAL?)
 *
 * @author pablo
 */
public class PCSpeaker implements SensorListener {

    private boolean initialized = false;
    private SourceDataLine sourceDataLine;
    private float volume = 100.0f;
    private float gain = 6.0206f;

    public void init(AudioFormat format) {
        DataLine.Info dataLineInfo =
                new DataLine.Info(
                SourceDataLine.class,
                format);

        if (!AudioSystem.isLineSupported(dataLineInfo)) {
            System.out.println("EEEEEEEERRRRRRRRRROOOOOOOOORRRRR!!!!!!!!");
            return;
        }

        System.out.println("Format = " + dataLineInfo);
        try {
            for (Mixer.Info mi : AudioSystem.getMixerInfo()) {
                System.out.println("Encodig = " + mi.getName());
                System.out.println("\t" + mi.getDescription());
                System.out.println("\t" + mi.getVendor());
                System.out.println("\t" + mi.getVersion());
            }
            sourceDataLine = getSourceDataLine(dataLineInfo);

            System.out.println("Open");
            sourceDataLine.open(format);
            System.out.println("Start sourceDataLine = " + sourceDataLine.getLineInfo());
            sourceDataLine.start();
            setMaxGain();
            setMaxVolume();
        } catch (LineUnavailableException ex) {
            Logger.getLogger(PCSpeaker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void update(Sensor source, SensorData sd) {
        if (sd instanceof MicrophoneData) {
            MicrophoneData md = (MicrophoneData) sd;
            if (!initialized) {
                init(md.getAudioFormat());
                initialized = true;
            }
            //sourceDataLine.stop();
            //sourceDataLine.flush();
            sourceDataLine.write(md.getData(), 0, md.getData().length);
            //sourceDataLine.start();
            /*
             int available = sourceDataLine.available();
             int length = md.getData().length;
             if(available < length) {
             sourceDataLine.write(md.getData(), 0, available);
             } else {
             sourceDataLine.write(md.getData(), 0, md.getData().length);
             }*/
        }
    }

    private SourceDataLine getSourceDataLine(DataLine.Info dataLineInfo) {
        SourceDataLine sdl = null;
        for (Mixer.Info mi : AudioSystem.getMixerInfo()) {
            Mixer m = AudioSystem.getMixer(mi);
            if (m.isLineSupported(dataLineInfo)) {
                try {
                    Line line = m.getLine(dataLineInfo);
                    if (line instanceof SourceDataLine) {
                        Logger.getLogger(
                                PCSpeaker.class.getName()).log(Level.INFO, "Mixer={0}", m.getMixerInfo().getName());
                        sdl = (SourceDataLine) line;
                        break;
                    }
                } catch (LineUnavailableException ex) {
                    Logger.getLogger(PCSpeaker.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return sdl;
    }

    @Override
    public void cleanUp() {
        if (sourceDataLine != null) {
            sourceDataLine.drain();
            sourceDataLine.stop();
            sourceDataLine.close();
        }
    }

    public void setMaxGain() {
        if (isGainSupported()) {
            FloatControl gainControl = (FloatControl) sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN);
            this.gain = gainControl.getMaximum();
            gainControl.setValue(gain);
        }
    }
    
    public void setMinGain() {
        if (isGainSupported()) {
            FloatControl gainControl = (FloatControl) sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN);
            this.gain = gainControl.getMinimum();
            gainControl.setValue(gain);
        }
    }
    
    public boolean isGainSupported() {
        return sourceDataLine.isControlSupported(FloatControl.Type.MASTER_GAIN);
    }
    
    public void setGain(float gain) {
        this.gain = gain;
        if (isGainSupported()) {
            FloatControl gainControl = (FloatControl) sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN);
            System.out.println(gainControl);
            gainControl.setValue(gain);
        }
    }

    public void setMute(boolean mute) {
        if (sourceDataLine.isControlSupported(FloatControl.Type.VOLUME)) {
            FloatControl volumeControl = (FloatControl) sourceDataLine.getControl(FloatControl.Type.VOLUME);
            if (mute) {
                volumeControl.setValue(0f);
            } else {
                volumeControl.setValue(volume);
            }
        }
    }
    
    public boolean isVolumeSupported() {
        return sourceDataLine.isControlSupported(FloatControl.Type.VOLUME);
    }
    
    public void setMaxVolume() {        
        if (isVolumeSupported()) {
            FloatControl volumeControl = (FloatControl) sourceDataLine.getControl(FloatControl.Type.VOLUME);
            this.volume = volumeControl.getMaximum();
            volumeControl.setValue(volume);
        }
    }
    
    public void setMinVolume() {
        if (isVolumeSupported()) {
            FloatControl volumeControl = (FloatControl) sourceDataLine.getControl(FloatControl.Type.VOLUME);
            this.volume = volumeControl.getMinimum();
            volumeControl.setValue(volume);
        }
    }
    
    public void setVolume(float volume) {
        this.volume = volume;
        if (isVolumeSupported()) {
            FloatControl volumeControl = (FloatControl) sourceDataLine.getControl(FloatControl.Type.VOLUME);
            volumeControl.setValue(volume);
        }
    }
}