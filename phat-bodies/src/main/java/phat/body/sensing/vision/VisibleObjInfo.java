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

package phat.body.sensing.vision;

import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 * It contains information about objects viewed by the human using @see VisionControl.
 * These objects are stored and updated by @see VisibleObjectManager.
 * 
 * @author pablo <pabcampi@ucm.es>
 */
public class VisibleObjInfo implements Cloneable {
    String id;
    Spatial spatial;
    Vector3f originPos = new Vector3f();
    Vector3f targetPos = new Vector3f();

    public VisibleObjInfo(String id, Spatial spatial, Vector3f origin, Vector3f targetPos) {
        this.id = id;
        this.spatial = spatial;
        this.originPos.set(origin);
        this.targetPos.set(targetPos);
    }

    public float distance() {
        return originPos.distance(targetPos);
    }
    
    public String getId() {
        return id;
    }

    public Spatial getSpatial() {
        return spatial;
    }

    public Vector3f getOrigin() {
        return originPos;
    }

    public Vector3f getTargetPos() {
        return targetPos;
    }
    
    @Override
    public Object clone() {
        return new VisibleObjInfo(id, spatial, originPos, targetPos);
    }
}
