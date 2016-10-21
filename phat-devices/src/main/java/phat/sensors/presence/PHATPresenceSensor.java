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
package phat.sensors.presence;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import phat.sensors.Sensor;
import phat.sensors.SensorListener;
import phat.util.SpatialFactory;
import phat.util.SpatialUtils;
import phat.world.PHATCalendar;

/**
 *
 * @author pablo
 */
public class PHATPresenceSensor extends Sensor {

    float hAngle = 180.0f;
    float vAngle = 30.0f;
    float distance = 10.0f;
    float frecuency = 1f;
    float angleStep = 10f;
    private float count = 0f;
    PresenceData presenceData;
    Spatial rootNode;
    CollisionResults collisionResults = new CollisionResults();
    Ray ray = new Ray();
    Vector3f rayDir = new Vector3f();
    Vector3f position;
    boolean debug = false;
    Node debugNode = new Node();
    
    PHATCalendar calendar;
    
    public PHATPresenceSensor(String id, PHATCalendar calendar) {
        super(id);
        this.calendar = calendar;
        presenceData = new PresenceData(0, false);
    }

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);

        if (spatial == null) {
            return;
        }
        presenceData.presence = false;
        rootNode = SpatialUtils.getRootNode(spatial);
        position = spatial.getWorldTranslation();
    }

    @Override
    protected void controlUpdate(float f) {
        count += f;
        if (count >= 1f / frecuency) {
            count = 0f;
            boolean last = presenceData.presence;
            detect();
            if (last != presenceData.presence) {
                presenceData.timestamp = calendar.getTimeInMillis();
                notifyListeners();
            }
        }
    }

    Quaternion rot = new Quaternion();
    
    private void detect() {
        ray.setOrigin(position);
        ray.setLimit(distance);
        
        presenceData.presence = false;
        
        debugNode.detachAllChildren();
        
        float hva = vAngle / 2f;
        float hha = hAngle / 2f;
        boolean detected = false;
        
        for (int vi = 0; vi < vAngle && !detected; vi += angleStep) {
            for (int hi = 0; hi < hAngle && !detected; hi += angleStep) {
                //rayDir.set(Vector3f.UNIT_Z);
                //spatial.getWorldRotation().multLocal(rayDir);
                rayDir.set(Vector3f.UNIT_Z);
                rot.fromAngles((vi-hva)*FastMath.DEG_TO_RAD, (hi-hha)*FastMath.DEG_TO_RAD, 0f);
                spatial.getWorldRotation().mult(rot, rot);
                rot.multLocal(rayDir);
                
                ray.setDirection(rayDir);
                collisionResults.clear();
                
                float longitud = distance;
                ColorRGBA colour = ColorRGBA.Blue;
                
                rootNode.collideWith(ray, collisionResults);
                if (collisionResults.size() > 0) {
                    CollisionResult cr = collisionResults.getClosestCollision();
                    if (cr != null && cr.getDistance() <= distance) {
                        longitud = cr.getDistance();
                        Spatial body = SpatialUtils.getParentSpatialWithRole(
                                cr.getGeometry(), "Body");
                        if(body != null) {
                            // detected!
                            colour = ColorRGBA.Red;
                            presenceData.presence = true;
                            detected = true;
                        }
                    }
                }
                if(debug) {
                    if(debugNode.getParent() == null) {
                        SpatialUtils.getRootNode(spatial).attachChild(debugNode);
                    }
                    debugNode.attachChild(
                        SpatialFactory.createArrow(rayDir.mult(longitud), 4f, colour).move(position));
                }
            }
        }
    }

    private void notifyListeners() {
        for (SensorListener al : listeners) {
            al.update(this, presenceData);
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        
    }

    public PresenceData getPresenceData() {
        return presenceData;
    }

    public float gethAngle() {
        return hAngle;
    }

    public void sethAngle(float hAngle) {
        this.hAngle = hAngle;
    }

    public float getvAngle() {
        return vAngle;
    }

    public void setvAngle(float vAngle) {
        this.vAngle = vAngle;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public float getFrecuency() {
        return frecuency;
    }

    public void setFrecuency(float frecuency) {
        this.frecuency = frecuency;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public float getAngleStep() {
        return angleStep;
    }

    public void setAngleStep(float angleStep) {
        this.angleStep = angleStep;
    }
}
