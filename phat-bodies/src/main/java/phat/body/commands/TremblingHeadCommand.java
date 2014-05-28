/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.body.commands;

import com.jme3.app.Application;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.logging.Level;
import phat.body.BodiesAppState;
import phat.body.control.navigation.navmesh.NavMeshMovementControl;
import phat.body.control.parkinson.HeadTremblingControl;
import phat.body.control.physics.PHATCharacterControl;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.structures.houses.HouseAppState;

/**
 *
 * @author pablo
 */
public class TremblingHeadCommand extends PHATCommand {

    private String bodyId;
    
    private Boolean on;
    private Float minAngle;
    private Float maxAngle;
    private Float angular;
    
    public TremblingHeadCommand(String bodyId, Boolean on, PHATCommandListener listener) {
        super(listener);
        this.bodyId = bodyId;
        this.on = on;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }
    
    public TremblingHeadCommand(String bodyId, Boolean on) {
        this(bodyId, on, null);
    }
    
    @Override
    public void runCommand(Application app) {
        BodiesAppState bodiesAppState = app.getStateManager().getState(BodiesAppState.class);
        
        Node body = bodiesAppState.getAvailableBodies().get(bodyId);
        if(body != null) {
            if(on) {
                active(body);
            } else {
                desactive(body);
            }
        }
        setState(State.Success);
    }
    
    private void active(Node body) {
        HeadTremblingControl htc = body.getControl(HeadTremblingControl.class);
        if(htc == null) {
            htc = new HeadTremblingControl();
            if(minAngle != null) htc.setMinAngle(minAngle);
            if(maxAngle != null) htc.setMaxAngle(maxAngle);
            if(angular != null) htc.setAngular(angular);
            body.addControl(htc);
        }
    }
    
    private void desactive(Node body) {
        HeadTremblingControl htc = body.getControl(HeadTremblingControl.class);
        if(htc != null) {
            body.removeControl(htc);
        }
    }
    
    public Float getMinAngle() {
        return minAngle;
    }

    public void setMinAngle(Float minAngle) {
        this.minAngle = minAngle;
    }

    public Float getMaxAngle() {
        return maxAngle;
    }

    public void setMaxAngle(Float maxAngle) {
        this.maxAngle = maxAngle;
    }

    public Float getAngular() {
        return angular;
    }

    public void setAngular(Float angular) {
        this.angular = angular;
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName()+"("+bodyId+", "+on+")";
    }

	@Override
	public void interruptCommand(Application app) {
		setState(State.Fail);
	}
}
