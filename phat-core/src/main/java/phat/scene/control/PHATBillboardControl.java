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

package phat.scene.control;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.BillboardControl;
import phat.util.SpatialUtils;

/**
 *
 * @author pablo
 */
public class PHATBillboardControl extends BillboardControl {
    Vector3f offset = new Vector3f(0f, 0.5f, 0f);
    Vector3f center = new Vector3f();
    Vector3f loc = new Vector3f();
    Spatial head;
    Quaternion rotation = new Quaternion();
    
    boolean updatedLoc = false;
        
    public void update(float fps) {
        super.update(fps);
        updateLocation(spatial);
    }

    float [] angles = new float[3];
    
    private void updateLocation(Spatial spatial) {
        if(head == null) {
            head = spatial.getParent().getParent().getChild("male/head/middle_aged");
        }
        loc.set(spatial.getParent().getWorldTranslation());
        center.set(SpatialUtils.getCenterBoinding(head));
        //System.out.println("Human Loc = "+loc+", head Loc = "+center);
        center.subtractLocal(loc);
        center.addLocal(offset);
        spatial.setLocalTranslation(center);
        
        rotation.set(spatial.getParent().getParent().getLocalRotation());
        rotation.toAngles(angles);
        angles[0] *= -1;
        angles[1] *= -1;
        angles[2] *= -1;
        rotation.fromAngles(angles);        
        
        spatial.getParent().setLocalRotation(rotation);
    }

    public Vector3f getOffset() {
        return offset;
    }

    public void setOffset(Vector3f offset) {
        this.offset = offset;
    }
}
