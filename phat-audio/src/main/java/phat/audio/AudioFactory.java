/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.audio;

import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioKey;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioRenderer;
import com.jme3.audio.LowPassFilter;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;
import phat.audio.textToSpeech.TextToSpeechManager;

/**
 *
 * @author Pablo
 */
public class AudioFactory {
    private static AudioFactory asf;
    
    public static AudioFactory getInstance() {
        return asf;
    }
    
    public static void init(AudioRenderer audioRender, AssetManager assetManager, Node rootNode) {
        if(asf == null) {
            asf = new AudioFactory();
        } else {
            return;
        }
        asf.assetManager = assetManager;
        asf.audioRenderer = audioRender;
        asf.rootNode = rootNode;
        
    }
    
    private AudioFactory() {  
    }
    
    
    private AssetManager assetManager;
    private AudioRenderer audioRenderer;
    private Node rootNode;
    
    public AudioRenderer getAudioRenderer() {
        return audioRenderer;
    }
    
    public Node getRootNode() {
        return rootNode;
    }
    
    public PHATAudioSource makeAudioSource(String name, String resource, Vector3f location) {
        return makeAudioSource(name, resource, location, false);
    }
    
    public PHATAudioSource makeAudioSource(String name, String resource, Vector3f location, boolean stream) {
        PHATAudioSource audioSource = new PHATAudioSource(assetManager, resource);
        audioSource.setPositional(true);
        audioSource.setName(name);
        audioSource.setLooping(true);
        audioSource.setLocalTranslation(location);
        audioSource.setVolume(1f);
        audioSource.setMaxDistance(Float.MAX_VALUE);        
        audioSource.setRefDistance(1f);    
        return audioSource;
    }
    
    /*public AudioSource makeAudioSource(String name, String resource, Vector3f location) {
        AudioNode audioNode = new AudioNode(assetManager, resource);
        audioNode.setChannel(0);
        audioNode.setPositional(true);
        audioNode.setName(name);
        audioNode.setLooping(true);
        audioNode.setLocalTranslation(location);
        audioNode.setVolume(0.03f);
        audioNode.setMaxDistance(100000000);        
        audioNode.setRefDistance(5f);
        audioNode.setTimeOffset((float)Math.random());
        AudioSource as = new AudioSource(audioNode, rootNode,assetManager);        
        return as;
    }*/
    
    public AudioSpeakerSource makeAudioSpeakerSource(String nodeName, String textToSpeech, Vector3f location) {
        AudioSpeakerSource audioNode = createAudioSpeakingNode(textToSpeech);
        audioNode.setPositional(true);                
        audioNode.setDirectional(false);
        /*
        audioNode.setInnerAngle(180f);
        audioNode.setOuterAngle(90f);
         */
        audioNode.setName(nodeName);
        audioNode.setLocalTranslation(location);
        audioNode.setVolume(1f);
        audioNode.setMaxDistance(Float.MAX_VALUE);        
        audioNode.setRefDistance(1f);
        //audioNode.setTimeOffset((float)Math.random());
        return audioNode;
    }
    
    private AudioSpeakerSource createAudioSpeakingNode(String text) {
        TextToSpeechManager ttsm = TextToSpeechManager.getInstance();        
        String filePath = ttsm.getFilePath(text);
        //AudioData ad = assetManager.loadAudio(new AudioKey(filePath));
        return new AudioSpeakerSource(assetManager, filePath);
    }
    
    public Geometry createRangeSphere(AudioNode audioNode) {
        Sphere sphere = new Sphere(32, 32, audioNode.getRefDistance()*6);
        Geometry rangeGeometry = new Geometry("Shiny rock", sphere);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", new ColorRGBA(1, 0, 0, 0.2f));        
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        rangeGeometry.setMaterial(mat);        
        rangeGeometry.setQueueBucket(RenderQueue.Bucket.Transparent); 
        return rangeGeometry;
    }
}
