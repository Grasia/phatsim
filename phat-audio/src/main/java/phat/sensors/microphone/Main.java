package phat.sensors.microphone;

import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioBuffer;
import com.jme3.audio.AudioKey;
import com.jme3.audio.AudioNode;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.util.BufferUtils;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 *
 * @author normenhansen
 */
public class Main extends SimpleApplication {

    private float time = 0;
    private AudioNode audioSource;
    //private AudioStream audioStream;
    private AudioBuffer audioBuffer;

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        Box b = new Box(1, 1, 1);
        Geometry geom = new Geometry("Box", b);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        geom.setMaterial(mat);

        rootNode.attachChild(geom);

        //audioStream = new AudioStream();
        //audioStream.setupFormat(1, 16, 16000);
        audioBuffer = new AudioBuffer();
        audioBuffer.setupFormat(1, 16, 16000);

        // might return -1 if unknown
        float streamDuration = 1f;//computeStreamDuration();

        AudioInputStream audioInputStream;
        try {
            audioInputStream = AudioSystem.getAudioInputStream(new File("/home/pablo/tts_file.wav"));
            audioBuffer.updateData(readToBuffer(audioInputStream));
        } catch (UnsupportedAudioFileException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        audioSource = new AudioNode(audioBuffer, new AudioKey("Speaker", true, true));

    }

    private ByteBuffer readToBuffer(InputStream inputStream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        byte[] buf = new byte[512];
        int read = 0;

        while ((read = inputStream.read(buf, 0, buf.length)) > 0) {
            baos.write(buf, 0, read);
        }

        byte[] dataBytes = baos.toByteArray();
        ByteBuffer data = BufferUtils.createByteBuffer(dataBytes.length);
        data.put(dataBytes, 0, dataBytes.length).flip();

        inputStream.close();

        return data;
    }

    private float computeStreamDuration(int totalBytes, int numChannels, int sampleRate) {
        // 2 bytes(16bit) * channels * sampleRate
        int bytesPerSec = 2 * numChannels * sampleRate;

        // Don't know how many bytes are in input, pass MAX_VALUE
        //int totalBytes = getOggTotalBytes(Integer.MAX_VALUE);
        return (float) totalBytes / bytesPerSec;
    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
        time += tpf;
        if (time > 1f) {
            //audioBuffer.resetObject();
            audioSource.play();
            time = 0;
        }

    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
