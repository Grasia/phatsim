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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This class contains objects that human is seeing using @see VisionControl.
 * A map of listeners with ids are notified when new object are in visual field
 * and when they leave the visual field.
 *
 * @author pablo <pabcampi@ucm.es>
 */
public class VisibleObjectManager {

    Map<String, VisibleObjectsListener> listeners;
    Map<String, VisibleObjInfo> objects; // <Id, Object>

    public VisibleObjectManager() {
        objects = new HashMap<>();
        listeners = new HashMap<>();
    }

    void update(String id, Spatial spatial, Vector3f origin, Vector3f targetPos) {
        VisibleObjInfo voi = get(id);
        if (voi == null) {
            voi = new VisibleObjInfo(id, spatial, origin, targetPos);
            add(id, voi);
        } else {
            voi.getOrigin().set(origin);
            voi.getTargetPos().set(targetPos);
        }
    }

    private void notifyVisibleObjToListeners(VisibleObjInfo objInfo) {
        for (VisibleObjectsListener l : listeners.values()) {
            l.visible(objInfo, this);
        }
    }

    private void notifyNoVisibleObjToListeners(VisibleObjInfo objInfo) {
        for (VisibleObjectsListener l : listeners.values()) {
            l.noVisible(objInfo, this);
        }
    }

    public void add(String id, VisibleObjInfo objInfo) {
        if (get(id) == null) {
            objects.put(id, objInfo);
            notifyVisibleObjToListeners(objInfo);
        }
    }

    public void remove(String id) {
        VisibleObjInfo objInfo = get(id);
        if (objInfo != null) {
            objects.remove(id);
            notifyNoVisibleObjToListeners(objInfo);
        }
    }

    public VisibleObjInfo get(String id) {
        return objects.get(id);
    }

    public Set<String> getIds() {
        return objects.keySet();
    }

    public void addListener(VisibleObjectsListener listener) {
        if (listeners.get(listener.getId()) == null) {
            listeners.put(listener.getId(), listener);
        }
    }

    public void removeListener(String listenerId) {
        listeners.remove(listenerId);
    }
}
