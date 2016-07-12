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
package phat.audio.filters;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.sound.sampled.AudioFormat;
import phat.sensors.SensorData;
import phat.sensors.SensorDataFilter;
import phat.sensors.microphone.MicrophoneData;

/**
 *
 * @author pablo
 */
public class ResampleAudioFilter implements SensorDataFilter {

    Random random = new Random();
    int targetSampleRate;
    int bufferIndex = -1;
    final static int NUM_OF_BUFFERS = 2;
    List<byte[]> buffers;
    byte[] tempBuf;
    double acumTime = 0f;

    public ResampleAudioFilter(int targetSampleRate) {
        this.targetSampleRate = targetSampleRate;

        buffers = new ArrayList<byte[]>();
        for (int i = 0; i < NUM_OF_BUFFERS; i++) {
            buffers.add(null);
        }
    }

    private int getBufferSize(MicrophoneData microphoneData) {
        int numSamples = (microphoneData.getData().length / microphoneData.getAudioFormat().getFrameSize());
        float period = 1f / microphoneData.getAudioFormat().getFrameRate();
        double totalTime = acumTime + period * numSamples;
        int size = ((int) (totalTime * targetSampleRate)) * microphoneData.getAudioFormat().getFrameSize();
        return size;
    }

    private byte[] nextBuffer(int bufSize) {
        bufferIndex = (bufferIndex + 1) % NUM_OF_BUFFERS;

        byte[] buf = buffers.get(bufferIndex);

        if (buf == null || buf.length != bufSize) {
            buf = new byte[bufSize];
            buffers.set(bufferIndex, buf);
        }

        return buf;
    }

    private byte[] createTempBuffer(int bufSize) {
        if (tempBuf == null || tempBuf.length != bufSize) {
            tempBuf = new byte[bufSize];
        }
        return tempBuf;
    }

    @Override
    public SensorData filter(SensorData sensorData) {
        if (sensorData instanceof MicrophoneData) {
            MicrophoneData microphoneData = (MicrophoneData) sensorData;

            float oriPeriod = (1f / microphoneData.getAudioFormat().getFrameRate());
            float targetPeriod = 1f / targetSampleRate;

            int bufSize = getBufferSize(microphoneData);
            byte[] bufResult = nextBuffer(bufSize);

            int totalSize = microphoneData.getData().length;
            byte[] data = createTempBuffer(totalSize);
            System.arraycopy(microphoneData.getData(), 0, data, 0, totalSize);

            // resample to new sample rate
            int frameSize = microphoneData.getAudioFormat().getFrameSize();
            int iBuf = 0;
            for (int i = 0; i < data.length; i += frameSize) {
                acumTime += oriPeriod;
                if (acumTime >= targetPeriod) {
                    for (int j = 0; j < frameSize; j++) {
                        bufResult[iBuf++] = data[i + j];
                    }
                    acumTime -= targetPeriod;
                }
            }

            MicrophoneData result = new MicrophoneData(bufResult, new AudioFormat(targetSampleRate,
                    microphoneData.getAudioFormat().getSampleSizeInBits(),
                    microphoneData.getAudioFormat().getChannels(),
                    microphoneData.getAudioFormat().getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED),
                    microphoneData.getAudioFormat().isBigEndian()));
            return result;
        }
        return sensorData;
    }
}
