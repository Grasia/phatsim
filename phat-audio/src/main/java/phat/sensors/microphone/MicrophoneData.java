/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.sensors.microphone;

import javax.sound.sampled.AudioFormat;
import phat.sensors.SensorData;

/**
 *
 * @author pablo
 */
public class MicrophoneData implements SensorData {
    private byte[] data;
    private AudioFormat audioFormat;

    public MicrophoneData(byte[] data, AudioFormat af) {
        this.data = data;
        this.audioFormat = af;
    }

    public byte[] getData() {
        return data;
    }

    public AudioFormat getAudioFormat() {
        return audioFormat;
    }
    
}
