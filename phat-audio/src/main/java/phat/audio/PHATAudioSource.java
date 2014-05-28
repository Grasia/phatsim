package phat.audio;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;

/**
 *
 * @author Pablo
 */
public class PHATAudioSource extends AudioNode {

    protected AssetManager assetManager;    
    protected Geometry rangeGeometry;

    protected boolean showRange = false;
    
    public PHATAudioSource(AssetManager assetManager, String resource, boolean stream) {
        super(assetManager, resource, stream);
        this.assetManager = assetManager;
    }
    
    public PHATAudioSource(AssetManager assetManager, String resource) {
        this(assetManager, resource, false);
    }

    private void createRangeSphere() {
        Sphere sphere = new Sphere(32, 32, getRefDistance()*2);
        rangeGeometry = new Geometry("Shiny rock", sphere);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", new ColorRGBA(1, 0, 0, 0.2f));        
        mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        rangeGeometry.setMaterial(mat);        
        rangeGeometry.setQueueBucket(Bucket.Transparent); 
    }

    private void updateRange(boolean show) {
        if (show) {
            if(rangeGeometry != null) {
                rangeGeometry.removeFromParent();
            }
            createRangeSphere();
            attachChild(rangeGeometry);
        } else {
            if(rangeGeometry != null) {
                rangeGeometry.removeFromParent();
            }
        }
    }
    
    public void setShowRange(boolean showRange) {
        this.showRange = showRange;
        if(!showRange) {
            updateRange(false);
        }
    }    
    
    @Override
    public void play() {
        if(showRange) {
            updateRange(true);
        }
        AudioFactory.getInstance().getAudioRenderer().playSource(this);
    }
    
    @Override
    public void playInstance() {
        if(showRange) {
            updateRange(true);
        }
        AudioFactory.getInstance().getAudioRenderer().playSourceInstance(this);
    }
        
    @Override
    public void stop() {
        if(showRange) {
            updateRange(false);
        }
        super.stop();
    }
    
    public boolean isShowRange() {
        return showRange;
    }
}
