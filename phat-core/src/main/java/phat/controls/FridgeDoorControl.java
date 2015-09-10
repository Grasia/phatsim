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
package phat.controls;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import java.io.IOException;

/**
 *
 * @author pablo
 */
public class FridgeDoorControl extends AbstractControl {

    public static enum STATE {
        OPEN, CLOSE
    };
    FridgeDoorControl.STATE state = FridgeDoorControl.STATE.CLOSE;
    boolean stateChanged = false;
    float angularSpeed = FastMath.QUARTER_PI;
    float openingAngle = FastMath.HALF_PI * 0.7f;

    @Override
    protected void controlUpdate(float tpf) {
        if (stateChanged) {
            spatial.getLocalRotation().toAngles(angles);
            float rot = tpf * angularSpeed;
            if (state.equals(FridgeDoorControl.STATE.CLOSE)) {
                angles[2] -= rot;
                if (angles[2] < 0f) {
                    angles[2] = 0f;
                    stateChanged = false;
                }
                spatial.setLocalRotation(new Quaternion(angles));
            } else if (state.equals(FridgeDoorControl.STATE.OPEN)) {
                angles[2] += rot;
                if (angles[2] > openingAngle) {
                    angles[2] = openingAngle;
                    stateChanged = false;
                }
                spatial.setLocalRotation(new Quaternion(angles));
            }
        }
    }
    float[] angles = new float[3];

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    @Override
    public Control cloneForSpatial(Spatial spatial) {
        final FridgeDoorControl control = new FridgeDoorControl();
        /* Optional: use setters to copy userdata into the cloned control */
        // control.setIndex(i); // example
        control.setSpatial(spatial);
        control.setAngularSpeed(angularSpeed);
        control.setOpeningAngle(openingAngle);
        return control;
    }

    public FridgeDoorControl.STATE getState() {
        return state;
    }

    public void setState(FridgeDoorControl.STATE state) {
        if (state.compareTo(this.state) != 0) {
            this.state = state;
            stateChanged = true;
        }
    }

    public float getAngularSpeed() {
        return angularSpeed;
    }

    /**
     * Sets angular speed of the door in radians
     * 
     * @param angularSpeed (in radians)
     */
    public void setAngularSpeed(float angularSpeed) {
        this.angularSpeed = angularSpeed;
    }

    public float getOpeningAngle() {
        return openingAngle;
    }

    /**
     * Sets how much the door will be open in radians
     * 
     * @param openingAngle (in radians)
     */
    public void setOpeningAngle(float openingAngle) {
        this.openingAngle = openingAngle;
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule ic = im.getCapsule(this);
        state = ic.readEnum("state", FridgeDoorControl.STATE.class, FridgeDoorControl.STATE.CLOSE);
        angularSpeed = ic.readFloat("angularSpeed", FastMath.HALF_PI * 0.7f);
        openingAngle = ic.readFloat("openingAngle", FastMath.HALF_PI * 0.7f);
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule oc = ex.getCapsule(this);
        oc.write(state, "state", FridgeDoorControl.STATE.CLOSE);
        oc.write(angularSpeed, "angularSpeed", FastMath.QUARTER_PI);
        oc.write(openingAngle, "openingAngle", FastMath.HALF_PI * 0.7f);
    }
}