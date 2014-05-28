/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.controls.movements;

import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import java.io.IOException;

/**
 *
 * @author Pablo
 */
public class StraightMovementControl extends AbstractControl implements AutonomousMovementControl {

    private CharacterControl characterControl;
    private Vector3f targetLocation = new Vector3f();
    private Vector3f characterLocation = new Vector3f();
    private Vector3f directionVector = new Vector3f();
    private float minDistance = 0.0f;

    public StraightMovementControl() {
        super();
    }

    public StraightMovementControl(float minDistance) {
        super();
        this.minDistance = minDistance;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);        
    }
    
    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        characterControl = spatial.getControl(CharacterControl.class);
        if (characterControl == null) {
            throw new RuntimeException(getClass() + " has a model called " + spatial.getName() + " which needs a CharacterControl!");
        }
    }

    @Override
    protected void controlUpdate(float tpf) {
        characterControl.getPhysicsLocation(characterLocation);
        characterLocation.y = 0;

        directionVector.set(targetLocation);
        directionVector.subtractLocal(characterLocation);
        directionVector.y = 0;

        if (getDistanceToTarget() < minDistance) {
            characterControl.setWalkDirection(Vector3f.ZERO);
            //notifyDestinationReached(targetLocation);
            return;
        }

        move(tpf);
    }

    private void move(float tpf) {
        directionVector.y = 0;
        directionVector.normalizeLocal();
        characterControl.setViewDirection(directionVector);
        directionVector.multLocal(getSpeed() * tpf);
        characterControl.setWalkDirection(directionVector);
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    @Override
    public Control cloneForSpatial(Spatial sptl) {
        StraightMovementControl smc = new StraightMovementControl();
        return smc;
    }

    @Override
    public boolean aimAt(Vector3f direction) {
        return true;
    }

    @Override
    public boolean moveTo(Vector3f location) {
        targetLocation.set(location);
        characterControl.getPhysicsLocation(characterLocation);
        directionVector.set(targetLocation);
        directionVector.subtractLocal(characterLocation);
        return true;
    }

    @Override
    public boolean isMoving() {
        return true;
    }

    @Override
    public Vector3f getTargetLocation() {
        return targetLocation;
    }

    @Override
    public Vector3f getLocation() {
        return characterControl.getPhysicsLocation();
    }

    @Override
    public Vector3f getAimDirection() {
        return directionVector;
    }

    @Override
    public float getSpeed() {
        Float spatialSpeed = (Float) spatial.getUserData("Speed");
        if (spatialSpeed != null) {
            return spatialSpeed;
        }
        return 0f;
    }

    @Override
    public float getDistanceToTarget() {
        return getTargetLocation().subtract(getLocation()).setY(0).length();
    }

    public float getMinDistance() {
        return minDistance;
    }

    public void setMinDistance(float minDistance) {
        this.minDistance = minDistance;
    }
}
