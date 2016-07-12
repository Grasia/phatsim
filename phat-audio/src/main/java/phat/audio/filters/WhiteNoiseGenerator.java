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

import java.util.Random;
import phat.sensors.SensorData;
import phat.sensors.SensorDataFilter;
import phat.sensors.microphone.MicrophoneData;

/**
 *
 * @author pablo
 */
public class WhiteNoiseGenerator implements SensorDataFilter {

    Random random = new Random();
    int amplitude = 1;

    public WhiteNoiseGenerator(int amplitude) {
        this.amplitude = amplitude;
    }

    @Override
    public SensorData filter(SensorData sensorData) {
        return process(sensorData);
    }

    private SensorData process(SensorData sensorData) {
        if (sensorData instanceof MicrophoneData) {
            MicrophoneData md = (MicrophoneData) sensorData;

            byte[] data = md.getData();
            for (int i = 0; i < data.length; i+=2) {
                short value = (short) ((data[i+1] << 8) | (data[i] & 0x00FF));
                if (random.nextBoolean()) {
                    value += amplitude;
                } else {
                    value -= amplitude;
                }
                data[i] = (byte) (value & 0x00FF);
                data[i+1] = (byte) (value >> 8);
            }
        }
        return sensorData;
    }
}
