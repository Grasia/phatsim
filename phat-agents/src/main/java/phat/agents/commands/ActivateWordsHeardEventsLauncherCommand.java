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
package phat.agents.commands;

import com.jme3.app.Application;
import com.jme3.audio.AudioRenderer;
import com.jme3.scene.Node;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import phat.agents.AgentsAppState;
import phat.audio.filters.ResampleAudioFilter;
import phat.audio.filters.WhiteNoiseGenerator;
import phat.body.BodiesAppState;
import phat.body.commands.CreateBodyTypeCommand;
import phat.body.sensing.hearing.GrammarFacilitator;
import phat.body.sensing.hearing.HearingSense;
import phat.commands.PHATCommandListener;
import phat.sensors.microphone.MicrophoneControl;

public class ActivateWordsHeardEventsLauncherCommand extends PHATAgentCommand {
    String agentId;
    List<String> words;
    GrammarFacilitator grammarFacilitator;
    
    public ActivateWordsHeardEventsLauncherCommand(String agentId, PHATCommandListener listener) {
        super(listener);
        this.agentId = agentId;
    }

    @Override
    public void runCommand(Application app) {
        AgentsAppState agentsAppState = app.getStateManager().getState(AgentsAppState.class);
        BodiesAppState bodiesAppState = app.getStateManager().getState(BodiesAppState.class);
        
        Node body = bodiesAppState.getBody(agentId);
        if(body != null) {
            MicrophoneControl mc = body.getControl(MicrophoneControl.class);
            if(mc == null) {
                createHearingSense(body, app.getAudioRenderer());
            }
            agentsAppState.activateWoredsHeard(agentId);
            setState(State.Success);
            return;
        }
        setState(State.Fail);
    }
    
    public ActivateWordsHeardEventsLauncherCommand addWord(String word) {
        if(words == null) {
            words = new ArrayList<>();
        }
        words.add(word);
        return this;
    }
    
    private void createHearingSense(Node body, AudioRenderer audioRenderer) {
        logger.log(Level.INFO, "ActivateWordsHeardEventsLauncherCommand");
        String bodyId = body.getUserData("ID");
        try {
            MicrophoneControl mc = new MicrophoneControl(bodyId + "-Hearing", 10000, audioRenderer);
            // Adds filters to micrphone sensor
            ResampleAudioFilter resampleAudioFilter = new ResampleAudioFilter(16000); // Resamples the audio stream to 16000 Hz
            mc.addFilter(resampleAudioFilter);
            mc.addFilter(new WhiteNoiseGenerator(1)); // Adds white noise to audio
            // add Listeners to microphone sensor
            HearingSense hs = new HearingSense(bodyId);
            if(words != null && !words.isEmpty()) {
                for(String w: words) {
                    logger.log(Level.INFO, w);
                    hs.addWordToBeRecognized(w);
                }
            }
            if(grammarFacilitator != null) {
                hs.setGrammarFacilitator(grammarFacilitator);
            }
            hs.createRecognizer();
            mc.add(hs);
            
            //mc.add(new AudioSourceWaveFileWriter(new File("Prueba.wav")));
            //XYRMSAudioChart chart = new XYRMSAudioChart("Audio");
            //mc.add(chart);
            //chart.showWindow();
            body.addControl(mc);
        } catch (IOException ex) {
            Logger.getLogger(CreateBodyTypeCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setGrammarFacilitator(GrammarFacilitator grammarFacilitator) {
        this.grammarFacilitator = grammarFacilitator;
    }
    
    @Override
    public void interruptCommand(Application app) {
        setState(State.Interrupted);
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "()";
    }
}
