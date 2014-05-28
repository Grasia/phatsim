package phat.audio.textToSpeech;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import com.sun.speech.freetts.audio.SingleFileAudioPlayer;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat;

/**
 * 
 */
public class TextToSpeechManager {

    private static TextToSpeechManager textToSpeechManager = null;

    // PCM_SIGNED 44100.0 Hz, 16 bit, mono, 2 bytes/frame, little-endian
    private static AudioFormat audioFormat = new AudioFormat(44100,16,1,true,false);
    
    public static TextToSpeechManager getInstance() {
        if (textToSpeechManager == null) {
            textToSpeechManager = new TextToSpeechManager();
        }
        return textToSpeechManager;
    }
    // Por cada frase que se quiera decir hay una referencia al fichero
    private Map<String, String> files;
    private int idCounter = 0;      // Identificador por cada fichero de voz que se crea

    private TextToSpeechManager() {
        files = new HashMap<String, String>();
    }

    public String getFilePath(String text) {
        String filePath = files.get(text);
        String prefix = "./assets/";
        if (filePath == null) {
            String fileName = "voiceText-" + idCounter;
            String extension = ".wav";
            filePath = prefix+"Sounds/" + fileName;
            checkAndCreatePath("./assets/Sounds/");
            if (speak(text, "./assets/Sounds/", fileName)) {
                filePath = "Sounds/" + fileName+extension;
                files.put(text, filePath);
                idCounter++;
                return filePath;
            }
        }
        return filePath;
    }
    
    private void checkAndCreatePath(String path) {
        File folder = new File(path);
        if(!folder.exists()) {
            folder.mkdirs();
        }
    }

    /**
     * Example of how to list all the known voices.
     */
    public static void listAllVoices() {
        System.out.println();
        System.out.println("All voices available:");
        VoiceManager voiceManager = VoiceManager.getInstance();
        Voice[] voices = voiceManager.getVoices();
        for (int i = 0; i < voices.length; i++) {
            System.out.println("    " + voices[i].getName()
                    + " (" + voices[i].getDomain() + " domain)");
        }
    }

    private boolean speak(String text, String path, String file) {
        try {
            System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
            SingleFileAudioPlayer objPlayer = null;
            String voiceName = "kevin16";
            VoiceManager voiceManager = VoiceManager.getInstance();                 
            Voice objVoice = voiceManager.getVoice(voiceName);
            if (objVoice == null) {
                System.err.println("Cannot find a voice named " + voiceName + ".  Please specify a different voice.");
                return false;
            }
            objVoice.allocate();
            objPlayer = new SingleFileAudioPlayer(path + file, Type.WAVE); 
            objPlayer.setAudioFormat(audioFormat);
            objVoice.setAudioPlayer(objPlayer);
            objVoice.speak(text);
            objVoice.deallocate();
            objPlayer.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void main(String[] args) {
        TextToSpeechManager ttsm = TextToSpeechManager.getInstance();
        ttsm.getFilePath("Hello Monkey Engine!");
    }
}
