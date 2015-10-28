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
import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;

import phat.audio.listeners.PCSpeaker;
import phat.audio.listeners.XYRMSAudioChart;
import phat.sensors.microphone.MicrophoneControl;
import phat.util.PHATUtils;

/**
 *
 * @author pablo
 */
public class MultiAudioAppState extends PHATAudioAppState {

    PCSpeaker pcSpeaker;
    MicrophoneControl currentMicControl;
    private boolean showChart;
    private XYRMSAudioChart chart;
    
    @Override
    protected void createMic() {
        System.out.println("createArtificialMic..." + (audioRenderer instanceof MultiListener));
        removeMic();
        currentMicControl = micNode.getControl(MicrophoneControl.class);

        if (currentMicControl == null) {
            currentMicControl = new MicrophoneControl("MicroListening", 10000, audioRenderer);
        }
        if (pcSpeaker == null) {
            pcSpeaker = new PCSpeaker();
        }
        currentMicControl.add(pcSpeaker);

        if (showChart) {
            chart = new XYRMSAudioChart("Prueba");
            chart.showWindow();
            currentMicControl.add(chart);
        }

        micNode.addControl(currentMicControl);
        System.out.println("...createArtificialMic");
        pcSpeakerChanged = false;
    }

    @Override
    protected void removeMic() {
        if (currentMicControl != null) {
            currentMicControl.remove(pcSpeaker);
            if (chart != null) {
                chart.dispose();
                currentMicControl.remove(chart);
            }
            currentMicControl.getSpatial().removeControl(currentMicControl);
        }
    }


    public void setShowChart(boolean showChart) {
        this.showChart = showChart;
    }
}
