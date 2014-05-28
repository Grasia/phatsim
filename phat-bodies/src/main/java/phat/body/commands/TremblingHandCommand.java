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
import phat.body.control.parkinson.LeftHandTremblingControl;
import phat.body.control.parkinson.RightHandTremblingControl;
import phat.body.control.physics.PHATCharacterControl;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.structures.houses.HouseAppState;

/**
 *
 * @author pablo
 */
public class TremblingHandCommand extends PHATCommand {

    private String bodyId;
    private Boolean on;
    private Boolean left;
    private Float minAngle;
    private Float maxAngle;
    private Float angular;

    public TremblingHandCommand(String bodyId, Boolean on, Boolean left, PHATCommandListener listener) {
        super(listener);
        this.bodyId = bodyId;
        this.on = on;
        this.left = left;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }

    public TremblingHandCommand(String bodyId, Boolean on, Boolean left) {
        this(bodyId, on, left, null);
    }

    @Override
    public void runCommand(Application app) {
        BodiesAppState bodiesAppState = app.getStateManager().getState(BodiesAppState.class);

        Node body = bodiesAppState.getAvailableBodies().get(bodyId);
        if (body != null) {
            if (on) {
                active(body);
            } else {
                desactive(body);
            }
        }
        setState(State.Success);
    }

    @Override
	public void interruptCommand(Application app) {
        setState(State.Fail);
	}
    
    private void active(Node body) {
        if (left) {
            LeftHandTremblingControl htc = body.getControl(LeftHandTremblingControl.class);
            if (htc == null) {
                htc = new LeftHandTremblingControl();
                if (minAngle != null) {
                    htc.setMinAngle(minAngle);
                }
                if (maxAngle != null) {
                    htc.setMaxAngle(maxAngle);
                }
                if (angular != null) {
                    htc.setAngular(angular);
                }
                body.addControl(htc);
            }
        } else {
            RightHandTremblingControl htc = body.getControl(RightHandTremblingControl.class);
            if (htc == null) {
                htc = new RightHandTremblingControl();
                if (minAngle != null) {
                    htc.setMinAngle(minAngle);
                }
                if (maxAngle != null) {
                    htc.setMaxAngle(maxAngle);
                }
                if (angular != null) {
                    htc.setAngular(angular);
                }
                System.out.println("RightRightRightRightRightRightRightRight");
                body.addControl(htc);
            }
        }
    }

    private void desactive(Node body) {
        if (left) {
            LeftHandTremblingControl lhtc = body.getControl(LeftHandTremblingControl.class);
            if (lhtc != null) {
                body.removeControl(lhtc);
            }
        } else {
            RightHandTremblingControl lhtc = body.getControl(RightHandTremblingControl.class);
            if (lhtc != null) {
                body.removeControl(lhtc);
            }
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
        return getClass().getSimpleName() + "(" + bodyId + ", on=" + on + ", left=" + left + ")";
    }
}
