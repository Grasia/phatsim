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
import com.jme3.bullet.control.KinematicRagdollControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.logging.Level;
import phat.body.BodiesAppState;
import phat.body.BodyUtils;
import static phat.body.commands.SitDownCommand.AVAILABLE_SEAT_KEY;
import static phat.body.commands.SitDownCommand.PLACE_ID_KEY;
import phat.body.control.animation.AnimFinishedListener;
import phat.body.control.animation.BasicCharacterAnimControl;
import phat.body.control.animation.SitDownControl;
import phat.body.control.navigation.StraightMovementControl;
import phat.body.control.physics.PHATCharacterControl;
import phat.commands.PHATCommParam;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandAnn;
import phat.commands.PHATCommandListener;
import phat.structures.houses.House;
import phat.structures.houses.HouseAppState;
import phat.util.SpatialUtils;

/**
 *
 * @author pablo
 */
@PHATCommandAnn(name="StandUp", type="body", debug = false)
public class StandUpCommand extends PHATCommand implements AnimFinishedListener {

    private String bodyId;
    private Node body;
    private HouseAppState houseAppState;
    BodiesAppState bodiesAppState;
    Spatial seat;

    public StandUpCommand() {
    }

    public StandUpCommand(String bodyId, PHATCommandListener listener) {
        super(listener);
        this.bodyId = bodyId;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }

    public StandUpCommand(String bodyId) {
        this(bodyId, null);
    }
    KinematicRagdollControl krc;
    PHATCharacterControl cc;

    @Override
    public void runCommand(Application app) {
        bodiesAppState = app.getStateManager().getState(BodiesAppState.class);
        houseAppState = app.getStateManager().getState(HouseAppState.class);

        body = bodiesAppState.getBody(bodyId);

        if (body != null && body.getParent() != null) {
            krc = body.getControl(KinematicRagdollControl.class);
            cc = body.getControl(PHATCharacterControl.class);
            StraightMovementControl smc = body.getControl(StraightMovementControl.class);
            
            if (BodyUtils.isBodyPosture(body, BodyUtils.BodyPosture.Sitting)) {
                // Character is seat in a chair or something like that
                SitDownControl sdc = body.getControl(SitDownControl.class);
                sdc.standUp();
                BodyUtils.setBodyPosture(body, BodyUtils.BodyPosture.Standing);
                setAvailable();
                setState(PHATCommand.State.Success);
            } else if (BodyUtils.isBodyPosture(body, BodyUtils.BodyPosture.Falling)) {
                BasicCharacterAnimControl bcac = body.getControl(BasicCharacterAnimControl.class);
                bcac.setEnabled(true);
                bcac.standUpAnimation(this);
                krc.blendToKinematicMode(1f);
            } else if (BodyUtils.isBodyPosture(body, BodyUtils.BodyPosture.Lying)) {
                Vector3f accessLoc = body.getParent().getChild("Access").getWorldTranslation();
                body.removeFromParent();
                bodiesAppState.getBodiesNode().attachChild(body);
                body.setLocalTranslation(accessLoc);
                cc.setEnabled(true);
                BodyUtils.setBodyPosture(body, BodyUtils.BodyPosture.Standing);
                setState(PHATCommand.State.Success);
            } else {
                setState(PHATCommand.State.Success);
            }
        }
    }

    private void setAvailable() {
        String placeId = body.getUserData(PLACE_ID_KEY);
        Spatial place = getNearestSeat(placeId, body);
        place.setUserData(SitDownCommand.AVAILABLE_SEAT_KEY, true);
        seat = place;
        body.setUserData(PLACE_ID_KEY, null);
    }

    public Spatial getNearestSeat(String placeId, Spatial body) {
        Spatial result = null;
        Node placeToSit = null;
        House house = houseAppState.getHouse(body);
        if (house != null) {
            placeToSit = (Node) SpatialUtils.getSpatialById(house.getRootNode(), placeId);
        } else {
            placeToSit = (Node) SpatialUtils.getSpatialById(bodiesAppState.getRootNode(), placeId);
        }
        if (placeToSit != null) {
            if (placeToSit.getChild("Seats") != null) {
                Node seats = (Node) placeToSit.getChild("Seats");
                float minDistance = Float.MAX_VALUE;
                for (Spatial pts : seats.getChildren()) {
                    float cd = pts.getWorldTranslation().distanceSquared(body.getWorldTranslation());
                    if (cd < minDistance) {
                        minDistance = cd;
                        result = pts;
                    }
                }
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + bodyId + ")";
    }

    @Override
    public void animFinished(BasicCharacterAnimControl.AnimName animationName) {
        krc.setEnabled(false);
        cc.setEnabled(true);
        cc.setWalkDirection(Vector3f.ZERO);
        cc.setViewDirection(body.getLocalRotation().getRotationColumn(2));
        BodyUtils.setBodyPosture(body, BodyUtils.BodyPosture.Standing);
        if(seat != null) {
            seat.setUserData(AVAILABLE_SEAT_KEY, false);
        }
        setState(State.Success);
    }

    @Override
    public void interruptCommand(Application app) {
        setState(State.Fail);
    }

    @PHATCommParam(mandatory=true, order=1)
    public void setBodyId(String bodyId) {
        this.bodyId = bodyId;
    }
}