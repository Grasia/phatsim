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
package phat.devices.actuators;

import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import phat.util.SpatialFactory;

/**
 *
 * @author pablo
 */
public class VibratorActuator extends Actuator {

    boolean debug = false;
    Node debugNode = new Node();
    
    enum FUNCTION {

        NONE, VIBRATE, PATTERN, CANCEL
    }
    long[] pattern;
    int repeat = -1;
    int cIndex = 0;
    
    FUNCTION cFunction = FUNCTION.NONE;
    long tTime = 0L;
    double cTime = 0L;
    boolean newFunction = false;

    public VibratorActuator(String id) {
        super(id);
    }

    public void vibrate(long milliseconds) {
        newFunction = true;
        cFunction = FUNCTION.VIBRATE;
        cTime = 0L;
        tTime = milliseconds;
    }

    public void vibrate(long[] pattern, int repeat) {
        newFunction = true;
        cFunction = FUNCTION.PATTERN;
        this.pattern = pattern;
        this.repeat = repeat;
        cTime = 0L;
    }

    public void cancel() {
        cFunction = FUNCTION.CANCEL;
    }
    
    @Override
    public void controlUpdate(float tpf) {
        if (newFunction) {
            stopVibrate();
            newFunction = false;
        }
        switch (cFunction) {
            case CANCEL:
                stopVibrate();
                pattern = null;
                repeat = -1;
                cIndex = 0;
                cState = STATE.OFF;
                cFunction = FUNCTION.NONE;
                tTime = 0L;
                cTime = 0L;
                newFunction = false;
                cFunction = FUNCTION.NONE;
                break;
            case VIBRATE:
                switch (cState) {
                    case ON:
                        cTime += (tpf*1000);
                        if (cTime >= tTime) {
                            cancel();
                        }
                        break;
                    case OFF:
                        startVibrate();
                        break;
                }
                break;
            case PATTERN:
                cTime += tpf;
                if (cTime >= pattern[cIndex]) {
                    cIndex++;
                    switch (cState) {
                        case ON:
                            stopVibrate();
                            break;
                        case OFF:
                            startVibrate();
                            break;
                    }
                    if (cIndex < pattern.length) {
                        cTime = pattern[cIndex];
                    } else {
                        if (repeat != -1) { // repeat
                            cIndex = repeat;
                            cTime = pattern[cIndex];
                        } else { // fin
                            cancel();
                        }
                    }
                }
                break;
        }
    }

    private void stopVibrate() {
        cState = STATE.OFF;
        notifyListeners();
    }

    private void startVibrate() {
        cState = STATE.ON;
        notifyListeners();
    }
    Geometry debugGeo = null;

    private Geometry getDebugGeo() {
        if (debugGeo == null) {
            debugGeo = SpatialFactory.createSphere(0.1f, new ColorRGBA(1f,0f,0f,0.2f), true);
        }
        debugGeo.setLocalTranslation(spatial.getWorldTranslation());
        return debugGeo;
    }

    @Override
    protected void notifyListeners() {
        super.notifyListeners();
        if (debug) {
            switch (cState) {
                case ON:
                    SpatialFactory.getRootNode().attachChild(getDebugGeo());
                    break;
                case OFF:
                    getDebugGeo().removeFromParent();
                    break;
            }
        }
    }

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }
}
