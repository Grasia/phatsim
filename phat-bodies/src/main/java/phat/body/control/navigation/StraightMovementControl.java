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
package phat.body.control.navigation;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import com.jme3.scene.debug.Arrow;
import java.io.IOException;
import phat.body.control.physics.PHATCharacterControl;
import phat.util.SpatialFactory;

/**
 *
 * @author Pablo
 */
public class StraightMovementControl extends AbstractControl implements AutonomousMovementControl {

    private PHATCharacterControl characterControl;
    private Vector3f targetLocation = new Vector3f();
    private Vector3f characterLocation = new Vector3f();
    private Vector3f directionVector = new Vector3f();
    private Vector3f distanceToTarget = new Vector3f();
    private float minDistance;
    private boolean showPath = true;
    private Geometry pathGeo;
    private float maxTurnForce = 2f;

    public StraightMovementControl() {
        this(0.1f);
    }

    public StraightMovementControl(float minDistance) {
        super();
        this.minDistance = minDistance;
    }

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);

        if (spatial == null) {
            hidePath();
        } else {
            PersuitAndAvoidControl c = spatial.getControl(PersuitAndAvoidControl.class);
            if (c != null) {
                spatial.removeControl(c);
                spatial.addControl(c);
            }
        }
    }

    private PHATCharacterControl getCharacterControl() {
        if (characterControl == null) {
            characterControl = spatial.getControl(PHATCharacterControl.class);
        }
        return characterControl;
    }
    boolean stop = false;

    public void stop() {
        stop = true;
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (getCharacterControl().isEnabled()) {
            if (pathGeo == null && showPath) {
                showPath();
            }
            characterLocation.set(getLocation());
            characterLocation.y = 0;

            directionVector.set(targetLocation);
            directionVector.subtractLocal(characterLocation);

            targetLocation.subtract(characterLocation, distanceToTarget);

            if (getDistanceToTarget() < minDistance || stop) {
                characterControl.setWalkDirection(Vector3f.ZERO);
                spatial.removeControl(this);
                return;
            }

            move(tpf);
            jumpIfNecessary(tpf);
        } else {
            hidePath();
            spatial.removeControl(this);
        }
    }
    
    Vector3f currentDir = new Vector3f();
    Vector3f targetDir = new Vector3f();
    Vector3f diff = new Vector3f();
    Vector3f effectDir = new Vector3f();
    Vector3f aux = new Vector3f();

    private void move(float tpf) {
        currentDir.set(getCharacterControl().getViewDirection());
        currentDir.setY(0f);
        targetDir.set(directionVector);
        targetDir.setY(0f).normalizeLocal();
        targetDir.subtract(currentDir, diff);

        diff.mult(tpf * maxTurnForce, aux);
        currentDir.add(aux, aux);
        aux.normalizeLocal();
        effectDir.set(aux);
        characterControl.setViewDirection(effectDir);
        effectDir.mult(getSpeed(diff, distanceToTarget), aux);
        characterControl.setWalkDirection(aux);
    }

    Vector3f lastLocation = new Vector3f();
    float timeToJump = 1f;
    
    private void jumpIfNecessary(float tpf) {
        if (characterLocation.distance(lastLocation) < 0.001f
                && effectDir.length() != 0.2f) {
            //setGravity(new Vector3f(0f, 1f, 0f));
            timeToJump -= tpf;
            if (timeToJump <= 0f) {
                //setGravity(new Vector3f(0f, -0.1f, 0f));
                System.out.println("JUMP!!");
                spatial.getControl(PHATCharacterControl.class).jump();
                timeToJump = 1f;
            }
        } else {
            timeToJump = 1f;
        }
        lastLocation.set(characterLocation);
    }

    private float getSpeed(Vector3f angleDiff, Vector3f distanceToTarget) {
        float speed = 0.1f;
        if (angleDiff.length() < 0.5f) {
            speed = getSpeed() * (1f - (angleDiff.length() / 0.5f));
            if (distanceToTarget.length() < 0.5f) {
                speed = (speed + getSpeed() * (distanceToTarget.length() * 2f)) / 2f;
            }
        }
        return speed;
    }
    /*private void move(float tpf) {
     Vector3f modelForwardDir = spatial.getWorldRotation().mult(Vector3f.UNIT_Z);
     directionVector.normalizeLocal();
     characterControl.setViewDirection(directionVector);
     directionVector.multLocal(getSpeed() * tpf);
     characterControl.setWalkDirection(modelForwardDir.mult(getSpeed()));
     }*/

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    @Override
    public Control cloneForSpatial(Spatial sptl) {
        StraightMovementControl smc = new StraightMovementControl();
        smc.setMinDistance(minDistance);
        smc.setSpatial(sptl);
        return smc;
    }

    @Override
    //TODO check if the target location is reachable
    public boolean aimAt(Vector3f location) {
        return true;
    }

    @Override
    public boolean moveTo(Vector3f location) {
        if (spatial != null) {
            targetLocation.set(location);
            targetLocation.y = 0f;
            characterLocation.set(getLocation());
            directionVector.set(targetLocation);
            directionVector.subtractLocal(characterLocation);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isMoving() {
        return !characterControl.getWalkDirection().equals(Vector3f.ZERO);
    }

    @Override
    public Vector3f getTargetLocation() {
        return targetLocation;
    }

    @Override
    public Vector3f getLocation() {
        if (spatial != null) {
            if (characterControl != null) {
                return characterControl.getLocation();
            } else {
                return spatial.getWorldTranslation();
            }
        }
        return null;
    }

    @Override
    public Vector3f getAimDirection() {
        return directionVector;
    }

    @Override
    public float getSpeed() {
        if (spatial != null) {
            Float spatialSpeed = (Float) spatial.getUserData("Speed");
            if (spatialSpeed != null) {
                return spatialSpeed;
            }
        }
        return 0.5f;
    }

    @Override
    public float getDistanceToTarget() {
        return distanceToTarget.length();
    }

    public float getMinDistance() {
        return minDistance;
    }

    @Override
    public void setMinDistance(float minDistance) {
        this.minDistance = minDistance;
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule oc = ex.getCapsule(this);
        oc.write(minDistance, "minDistance", 0.1f);

    }

    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule ic = im.getCapsule(this);
        minDistance = ic.readFloat("minDistance", 0.1f);
    }

    public boolean isShowPath() {
        return showPath;
    }

    public void setShowPath(boolean showPath) {
        this.showPath = showPath;
    }

    private void showPath() {
        Arrow arrow = new Arrow(targetLocation.subtract(getLocation()));
        arrow.setLineWidth(7); // make arrow thicker                            
        pathGeo = SpatialFactory.createShape("StraightPath", arrow, ColorRGBA.Red);
        pathGeo.setLocalTranslation(getLocation().add(0f, 0.1f, 0f));
        spatial.getParent().attachChild(pathGeo);
    }

    private void hidePath() {
        if (pathGeo != null) {
            pathGeo.removeFromParent();
            pathGeo = null;
        }
    }
}
