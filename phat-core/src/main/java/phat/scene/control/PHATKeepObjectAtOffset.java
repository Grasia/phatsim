/*
 * Copyright (C) 2014 pablo <pabcampi@ucm.es>
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

package phat.scene.control;

import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author pablo <pabcampi@ucm.es>
 */
public class PHATKeepObjectAtOffset extends AbstractControl {
    Spatial target;
    Vector3f offset = new Vector3f(0f,0f,0f);

    Vector3f targetLoc = new Vector3f();
    Vector3f currentLoc = new Vector3f();
    Vector3f spatialLoc = new Vector3f();
    
    public PHATKeepObjectAtOffset(Spatial target) {
        this.target = target;
    }
    
    @Override
    protected void controlUpdate(float fps) {
        if(target != null) {
            targetLoc.set(target.getWorldTranslation()).addLocal(offset);
            spatial.setLocalTranslation(targetLoc);
            //currentLoc.set(spatial.getWorldTranslation());
            //spatial.getLocalTranslation().addLocal(targetLoc.subtractLocal(currentLoc));
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        
    }

    public Spatial getTarget() {
        return target;
    }

    public void setTarget(Spatial target) {
        this.target = target;
    }

    public Vector3f getOffset() {
        return offset;
    }

    public void setOffset(Vector3f offset) {
        this.offset = offset;
    }
}
