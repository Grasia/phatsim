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
package phat.sensors.microphone;

import com.jme3.audio.Listener;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 * Microphone for simple AudioRenderer (No multilistener)
 * 
 * @author Pablo
 */
public class SingleMicrophoneControl extends AbstractControl {
    Listener listener;
    
    public SingleMicrophoneControl(Listener listener) {
        super();
        this.listener = listener;
    }


    @Override
    protected void controlUpdate(float tpf) {
        if (listener != null) {
            listener.setLocation(spatial.getWorldTranslation());
            listener.setRotation(spatial.getWorldRotation());
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}
