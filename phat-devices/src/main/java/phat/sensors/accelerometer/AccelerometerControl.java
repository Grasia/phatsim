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
package phat.sensors.accelerometer;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;

import phat.sensors.Sensor;
import phat.sensors.SensorListener;

/**
 * Simulated accelerometer that calculates x,y and z accelerations of the
 * attached Spatial.
 *
 * @author Pablo
 */
public class AccelerometerControl extends Sensor {

    boolean added;
    float accuracy = 1 / 30f;
    float time = 0f;
    RigidBodyControl rbc;
    private float maxAccelerationRange = 40.0f;
    private Vector3f lastLocation;
    private Vector3f lastVelocity = new Vector3f(0f, 0f, 0f);
    private Quaternion lastRotation;
    Vector3f acceleration = new Vector3f();
    Vector3f currentVelocity = new Vector3f();
    Vector3f currentLocation = new Vector3f();
    Vector3f displacement = new Vector3f();
    Vector3f lastAcc = new Vector3f();

    public enum AMode {

        GRAVITY_MODE, ACCELEROMETER_MODE
    }
    AMode mode = AMode.GRAVITY_MODE;

    public AccelerometerControl(String id) {
        super(id);

        lastLocation = new Vector3f();
        lastRotation = new Quaternion();
    }

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);

        if (spatial == null) {
            return;
        }
        rbc = spatial.getControl(RigidBodyControl.class);
        lastLocation.set(getLocation());
        lastRotation.set(getRotation());
    }

    private Vector3f getLocation() {
        if (rbc != null && rbc.isEnabled()) {
            return rbc.getPhysicsLocation();
        } else {
            return spatial.getWorldTranslation();
        }
    }

    private Quaternion getRotation() {
        if (rbc != null && rbc.isEnabled()) {
            return rbc.getPhysicsRotation();
        } else {
            return spatial.getWorldRotation();
        }
    }

    private Quaternion desfase(Quaternion ini, Quaternion end) {
        Quaternion result = null;
        Vector3f endAngles = new Vector3f();
        end.toAngleAxis(endAngles);
        Vector3f iniAngles = new Vector3f();
        ini.toAngleAxis(iniAngles);
        Vector3f angles = endAngles.subtract(iniAngles);
        float[] buffer = new float[3];
        result = new Quaternion(angles.toArray(buffer));
        return result;
    }

    private void updateIncrementalListeners(float time, Vector3f accelerations) {
        AccelerationData ad = new AccelerationData(
                time,
                accelerations.getX(),
                accelerations.getY(),
                accelerations.getZ());
        Object[] listenersArray = listeners.toArray();
        for (int k=0;k<listenersArray.length;k++){
        	((SensorListener)listenersArray[k]).update(this, ad);
        }
        
    }

    private float filter(float a) {
        float max = maxAccelerationRange / 2.0f;
        if (a > max) {
            a = max;
        } else if (a < -max) {
            a = -max;
        }
        return a;
    }

    @Override
    protected void controlUpdate(float tpf) {
        time += tpf;
        if(time > accuracy) {            
            calculateAccelerations(time);
            time = 0f;
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    @Override
    public Control cloneForSpatial(Spatial spatial) {
        return null;
    }
    
    float[] angles = new float[3];

    private void printAngles(Quaternion q) {
        q.toAngles(angles);
        System.out.println(
                "x=" + Math.round(FastMath.RAD_TO_DEG * angles[0])
                + "; y=" + Math.round(FastMath.RAD_TO_DEG * angles[1])
                + "; z=" + Math.round(FastMath.RAD_TO_DEG * angles[2]));
    }
    Vector3f gravity = new Vector3f();
    
    Vector3f abs = new Vector3f();
    private void calculateAccelerations(float time) {
        Quaternion rot = getRotation();
        currentLocation.set(getLocation());
        lastLocation.subtract(currentLocation, currentVelocity);
        //currentVelocity.multLocal(0.5f);
        currentVelocity.divideLocal(time);

        calculateGravity(rot);

        switch (mode) {
            case GRAVITY_MODE:
                updateIncrementalListeners(time, gravity);
                break;
            case ACCELEROMETER_MODE:
                currentVelocity.subtract(lastVelocity, acceleration);
                acceleration.divideLocal(time);
                rot.mult(acceleration, acceleration);
                acceleration.negateLocal();

                //lastAcc.add(acceleration, acceleration);
                //acceleration.divideLocal(2f);
                
                acceleration.addLocal(gravity);
                if(acceleration.length() > 15f) {
                    acceleration.set(lastAcc);
                }
                //abs.setX(acceleration.length());
                updateIncrementalListeners(time, acceleration);
                break;
        }

        lastLocation.set(currentLocation);
        lastVelocity.set(currentVelocity);
        lastAcc.set(acceleration);
    }

    private void calculateGravity(Quaternion rot) {
        gravity.set(0.0f, 9.8f, 0.0f);
        float x = gravity.dot(rot.getRotationColumn(0));
        float y = gravity.dot(rot.getRotationColumn(1));
        float z = gravity.dot(rot.getRotationColumn(2));
        gravity.set(x, y, z);
    }

    private boolean normalValue(Vector3f acc) {
        float threshold = 15f;
        if(FastMath.abs(acc.x) > threshold ||
                FastMath.abs(acc.y) > threshold ||
                FastMath.abs(acc.z) > threshold) {
            return false;
        }
        return true;
    }
    // remove pointed values
    private void filter(Vector3f acc) {
        float threshold = 15f;
        if (acc.x > threshold) {
            acc.x = threshold;
        } else if(acc.x < -threshold) {
            acc.x = -threshold;
        }
        if (acc.y > threshold) {
            acc.y = threshold;
        } else if(acc.y < -threshold) {
            acc.y = -threshold;
        }
        if (acc.z > threshold) {
            acc.z = threshold;
        } else if(acc.z < -threshold) {
            acc.z = -threshold;
        }
    }

    public AMode getMode() {
        return mode;
    }

    public void setMode(AMode mode) {
        this.mode = mode;
    }
}