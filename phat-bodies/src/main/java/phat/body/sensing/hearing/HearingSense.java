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
package phat.body.sensing.hearing;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import phat.commands.PHATCommand;

import phat.sensors.Sensor;
import phat.sensors.SensorData;
import phat.sensors.SensorListener;
import phat.sensors.microphone.MicrophoneData;

/**
 *
 * @author pablo
 */
public class HearingSense implements SensorListener {

    protected static final Logger logger = Logger.getLogger(PHATCommand.class.getName());
    PipedInputStream in;
    PipedOutputStream out;
    StreamSpeechRecognizer recognizer;
    AtomicBoolean writing = new AtomicBoolean(false);
    AtomicBoolean finished = new AtomicBoolean(false);
    Thread recognizerThread;
    MicrophoneData md;
    List<WordsHeardListener> listeners = new ArrayList<>();
    String bodyId;
    List<String> wordsToDict = new ArrayList<>();
    GrammarFacilitator grammarFacilitator;

    public HearingSense(String bodyId) throws IOException {
        logger.log(Level.INFO, "Hearing sense for body {0}", bodyId);
        this.bodyId = bodyId;

        out = new PipedOutputStream();
        in = new PipedInputStream();
        in.connect(out);

    }

    public void createRecognizer() {
        Configuration configuration = new Configuration();

        // Load model from the jar
        configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
        // You can also load model from folder
        // configuration.setAcousticModelPath("file:en-us");
        //configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
        if (wordsToDict.isEmpty()) {
            configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
        } else {
            configuration.setDictionaryPath(getOpptimizedDictionary());
        }
        if (grammarFacilitator != null) {
            configuration.setUseGrammar(true);
            configuration.setGrammarName(grammarFacilitator.getName());
            configuration.setGrammarPath(grammarFacilitator.getPath());
        }

        configuration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin");
        //configuration.setSampleRate(8000);
        try {
            Logger.getLogger("dictionary").setLevel(Level.OFF);
            recognizer = new StreamSpeechRecognizer(configuration);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public void setGrammarFacilitator(GrammarFacilitator grammarFacilitator) {
        this.grammarFacilitator = grammarFacilitator;
    }

    public void addWordToBeRecognized(String word) {
        wordsToDict.add(word);
    }

    private String getOpptimizedDictionary() {
        String result = "";
        try {
            InputStream is = getClass().getResourceAsStream("/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line;
            while ((line = br.readLine()) != null) {
                for (String word : wordsToDict) {
                    if (line.startsWith(word + " ")) {
                        result += line + "\n";
                    }
                }
            }
            br.close();

            FileWriter newDic = new FileWriter("optimized.dict");
            BufferedWriter bufWriter = new BufferedWriter(newDic);
            bufWriter.write(result);
            bufWriter.flush();
            bufWriter.close();
            newDic.close();
            return "optimized.dict";
        } catch (IOException ex) {
            Logger.getLogger(HearingSense.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private void start() {
        recognizerThread = new Thread() {
            @Override
            public void run() {
                recognizer.startRecognition(in);
                SpeechResult result;
                while ((result = recognizer.getResult()) != null && !finished.get()) {
                    String hypothesis = result.getHypothesis();
                    String[] words = hypothesis.split(" ");

                    for (WordsHeardListener l : listeners) {
                        l.notifyNewWordsHeard(bodyId, words);
                    }
                }
                recognizer.stopRecognition();
            }
        };
        recognizerThread.start();
    }
    boolean started = false;
    static int NUM_BUF = 4;
    int readIndex = -1;
    int writeIndex = 0;
    List<Buffer> buffers;

    private void writeNextBuffer(byte[] data) {
        Buffer buffer = buffers.get(writeIndex);
        buffer.lenght = data.length;
        System.arraycopy(data, 0, buffer.buf, 0, data.length);
        writeIndex = (writeIndex + 1) % NUM_BUF;
    }

    private Buffer getNextBuffer() {
        readIndex = (readIndex + 1) % NUM_BUF;
        return buffers.get(readIndex);
    }

    class Buffer {

        byte[] buf;
        int lenght;

        public Buffer(byte[] buf) {
            this.buf = buf;
        }
    }

    @Override
    public void update(Sensor source, SensorData sd) {
        if (sd instanceof MicrophoneData) {
            md = (MicrophoneData) sd;
            if (!started) {
                buffers = new ArrayList<>();
                int lenght = md.getData().length + 2;
                for (int i = 0; i < NUM_BUF; i++) {
                    buffers.add(new Buffer(new byte[lenght]));
                }
                writer.start();
                start();
                started = true;
            }
            writeNextBuffer(md.getData());
            writing.set(true);
        }
    }
    Thread writer = new Thread() {
        @Override
        public void run() {
            while (!finished.get()) {
                if (writing.getAndSet(false)) {
                    try {
                        Buffer buffer = getNextBuffer();
                        out.write(buffer.buf, 0, buffer.lenght);
                    } catch (IOException ex) {
                        Logger.getLogger(HearingSense.class.getName()).log(Level.SEVERE, null, ex);
                        break;
                    }
                }
            }
        }
    };

    @Override
    public void cleanUp() {
        finished.set(true);
    }

    public void add(WordsHeardListener wordsHeardListener) {
        listeners.add(wordsHeardListener);
    }
}
