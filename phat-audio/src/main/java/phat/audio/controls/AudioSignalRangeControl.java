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
