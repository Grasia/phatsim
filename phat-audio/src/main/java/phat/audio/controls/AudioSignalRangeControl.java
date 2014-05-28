package phat.audio.controls;

import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioSource;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;

/**
 *
 * @author Pablo
 */
public class AudioSignalRangeControl extends AbstractControl {

    public AudioSignalRangeControl() {
        super();
    }
    
    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        spatial.setCullHint(Spatial.CullHint.Always);
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        AudioNode audioNode = (AudioNode) spatial.getParent();
        if(audioNode.getStatus() == AudioSource.Status.Playing && spatial.getCullHint() == Spatial.CullHint.Always) {
            spatial.setCullHint(Spatial.CullHint.Dynamic);
        } else if(audioNode.getStatus() == AudioSource.Status.Stopped && spatial.getCullHint() == Spatial.CullHint.Dynamic) {
            spatial.setCullHint(Spatial.CullHint.Always);
        }
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        
    }

    @Override
    public Control cloneForSpatial(Spatial spatial) {
        AudioSignalRangeControl asrc = new AudioSignalRangeControl();
        asrc.setSpatial(spatial);
        return asrc;
    }
}
