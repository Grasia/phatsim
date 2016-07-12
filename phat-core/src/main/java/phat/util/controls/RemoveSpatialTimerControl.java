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
package phat.util.controls;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 * The spatial is removed after a given time
 *
 * @author Pablo
 */
public class RemoveSpatialTimerControl extends AbstractControl {
    private final float seconds;
    private float timer;
    
    public RemoveSpatialTimerControl(float seconds) {
        this.seconds = seconds;
        this.timer = seconds;
    }
    
    @Override
    protected void controlUpdate(float f) {
        timer -= f;
        if(timer <= 0) {
            getSpatial().removeFromParent();
        }
    }
    
    public void reset() {
        this.timer = seconds;
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        
    }
    
}
