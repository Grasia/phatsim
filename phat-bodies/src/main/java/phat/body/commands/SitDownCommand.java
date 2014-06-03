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
package phat.body.commands;

import com.jme3.app.Application;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import java.util.logging.Level;

import phat.body.BodiesAppState;
import phat.body.control.animation.SitDownControl;
import phat.body.control.navigation.AutonomousControlListener;
import phat.body.control.navigation.navmesh.NavMeshMovementControl;
import phat.body.control.physics.PHATCharacterControl;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.commands.PHATCommand.State;
import phat.structures.houses.House;
import phat.structures.houses.HouseAppState;
import phat.util.Debug;
import phat.util.Lazy;
import phat.util.SpatialFactory;
import phat.util.SpatialUtils;
import sun.management.resources.agent;

/**
 *
 * @author pablo
 */
public class SitDownCommand extends PHATCommand implements AutonomousControlListener, PHATCommandListener {

    private String bodyId;
    private String placeId;
    BodiesAppState bodiesAppState;
    Spatial nearestSeat;
    Node body;

    public SitDownCommand(String bodyId, String placeId, PHATCommandListener listener) {
        super(listener);
        this.bodyId = bodyId;
        this.placeId = placeId;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }

    public SitDownCommand(String bodyId, String placeId) {
        this(bodyId, placeId, null);
    }

    public static Spatial getNearestSeat(Node placeToSeat, Spatial body) {
        Spatial result = null;
        if (placeToSeat.getChild("Seats") != null) {
            Node seats = (Node) placeToSeat.getChild("Seats");
            float minDistance = Float.MAX_VALUE;
            for (Spatial pts : seats.getChildren()) {
                float cd = pts.getWorldTranslation().distanceSquared(body.getWorldTranslation());
                if (cd < minDistance) {
                    minDistance = cd;
                    result = pts;
                }
            }
        }
        return result;
    }
    GoToCommand goToCommand;
    RotateTowardCommand rotateCommand;

    @Override
    public void runCommand(Application app) {
        bodiesAppState = app.getStateManager().getState(BodiesAppState.class);
        HouseAppState houseAppState = app.getStateManager().getState(HouseAppState.class);

        body = bodiesAppState.getAvailableBodies().get(bodyId);

        if (body != null && body.getParent() != null) {
            if (body.getControl(SitDownControl.class) != null) {
                setState(State.Success);
                return;
            }
            House house = houseAppState.getHouse(body);
            Spatial placeToSit = null;
            if(house != null) {
                placeToSit = SpatialUtils.getSpatialById(house.getRootNode(), placeId);
            } else {
                placeToSit = SpatialUtils.getSpatialById(bodiesAppState.getRootNode(), placeId);
            }
            if (placeToSit != null) {
                nearestSeat = getNearestSeat((Node) placeToSit, body);
                //sitDown();
                goToCommand = new GoToCommand(bodyId, new Lazy<Vector3f>() {
                    @Override
                    public Vector3f getLazy() {
                        Spatial access = ((Node)nearestSeat).getChild("Access");
                        if(access != null) {
                            return access.getWorldTranslation();
                        }
                        Vector3f loc = nearestSeat.getWorldTranslation();
                        Vector3f dir = nearestSeat.getWorldRotation().mult(Vector3f.UNIT_Z).normalize();
                        return loc.add(dir.mult(0.5f));
                    }
                }, this);
                goToCommand.setMinDistance(0.05f);
                bodiesAppState.runCommand(goToCommand);
                return;
            }
        }
        setState(State.Fail);
    }

    @Override
	public void interruptCommand(Application app) {
		BodiesAppState bodiesAppState = app.getStateManager().getState(
				BodiesAppState.class);

		Node body = bodiesAppState.getAvailableBodies().get(bodyId);

		if (body != null && body.getParent() != null) {
			if(goToCommand != null) {
				goToCommand.interruptCommand(app);
				return;
			} else if(rotateCommand != null) {
				rotateCommand.interruptCommand(null);
				return;
			}
		}
		setState(State.Fail);
	}
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + bodyId + ", " + placeId + ")";
    }

    @Override
    public void destinationReached(Vector3f destination) {
        setState(State.Success);
    }

    private void sitDown() {
        PHATCharacterControl cc = body.getControl(PHATCharacterControl.class);
        cc.setEnabled(false);        
        Vector3f dir = nearestSeat.getLocalRotation().mult(Vector3f.UNIT_Z).normalize();
        body.setLocalRotation(nearestSeat.getParent().getParent().getWorldRotation());
        SitDownControl sdc = new SitDownControl();
        sdc.setSeat(nearestSeat);
        body.addControl(sdc);
        setState(State.Success);
    }

    @Override
    public void commandStateChanged(PHATCommand command) {
        if (command.getState() == PHATCommand.State.Success) {
            if (command == goToCommand) {
                rotateCommand = new RotateTowardCommand(bodyId, placeId, this);
                rotateCommand.setOposite(true);
                bodiesAppState.runCommand(rotateCommand);
            } else if (command == rotateCommand) {
                sitDown();
            }
        }
    }
}
