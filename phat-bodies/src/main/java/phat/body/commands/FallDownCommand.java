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

import com.jme3.animation.Bone;
import com.jme3.animation.SkeletonControl;
import com.jme3.app.Application;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.KinematicRagdollControl;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import java.util.logging.Level;

import phat.body.BodiesAppState;
import phat.body.BodyUtils;
import static phat.body.commands.SitDownCommand.PLACE_ID_KEY;
import phat.body.control.animation.BasicCharacterAnimControl;
import phat.body.control.animation.SitDownControl;
import phat.body.control.navigation.StraightMovementControl;
import phat.body.control.physics.PHATCharacterControl;
import phat.body.control.physics.ragdoll.BVHRagdollPreset;
import phat.commands.PHATCommParam;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.commands.PHATCommand.State;
import phat.commands.PHATCommandAnn;
import phat.structures.houses.House;
import phat.structures.houses.HouseAppState;
import phat.util.SpatialUtils;

/**
 *
 * @author pablo
 */
@PHATCommandAnn(name="FallDown", type="body", debug = false)
public class FallDownCommand extends PHATCommand {

    private String bodyId;
    HouseAppState houseAppState;
    BodiesAppState bodiesAppState;

    public FallDownCommand() {
    }

    public FallDownCommand(String bodyId, PHATCommandListener listener) {
        super(listener);
        this.bodyId = bodyId;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }

    public FallDownCommand(String bodyId) {
        this(bodyId, null);
    }

    @Override
    public void runCommand(Application app) {
        BulletAppState bulletAppState = app.getStateManager().getState(BulletAppState.class);

        bodiesAppState = app.getStateManager().getState(BodiesAppState.class);
        houseAppState = app.getStateManager().getState(HouseAppState.class);
        Node body = bodiesAppState.getBody(bodyId);

        if (body != null && body.getParent() != null) {
            if (BodyUtils.isBodyPosture(body, BodyUtils.BodyPosture.Sitting)) {
                // Character is seat in a chair or something like that
                SitDownControl sdc = body.getControl(SitDownControl.class);
                //sdc.standUp();
                body.removeControl(sdc);
                /*PHATCharacterControl cc = body.getControl(PHATCharacterControl.class);
                 cc.setEnabled(true);
                 BasicCharacterAnimControl bcac = body.getControl(BasicCharacterAnimControl.class);
                 bcac.setEnabled(true);*/
                BodyUtils.setBodyPosture(body, BodyUtils.BodyPosture.Standing);
                setAvailable(body);
            }
            KinematicRagdollControl krc = body.getControl(KinematicRagdollControl.class);
            if (krc == null) {
                BVHRagdollPreset preset = new BVHRagdollPreset();
                krc = new KinematicRagdollControl(preset, 0.5f);
                body.addControl(krc);
                bulletAppState.getPhysicsSpace().add(krc);
            }
            
            PHATCharacterControl cc = body.getControl(PHATCharacterControl.class);
            cc.setEnabled(false);
            //body.getControl(BasicCharacterAnimControl.class).setEnabled(false);

            StraightMovementControl smc = body.getControl(StraightMovementControl.class);
            if (smc != null) {
                body.removeControl(smc);
            }

            BasicCharacterAnimControl bcac = body.getControl(BasicCharacterAnimControl.class);
            if (bcac != null) {
                bcac.setManualAnimation(null, null);
            }

            if (!krc.isEnabled()) {
                krc.setEnabled(true);
            }
            
            SkeletonControl sc = body.getControl(SkeletonControl.class);
            for (int i = 0; i < sc.getSkeleton().getBoneCount(); i++) {
                Bone b = sc.getSkeleton().getBone(i);
                //System.out.println("bone name = "+b.getName());
                PhysicsRigidBody rbc = krc.getBoneRigidBody(b.getName());
                if (rbc != null) {
                    //System.out.println(b.getName()+" => "+rbc.getMass());
                    rbc.setFriction(0.8f);
                }
            }
            krc.setRagdollMode();
            
            SpatialUtils.printControls(body);

            BodyUtils.setBodyPosture(body, BodyUtils.BodyPosture.Falling);
            
            //bulletAppState.getPhysicsSpace().addTickListener(new PushChestFoward(body));
            //SimulateTripOver sto = new SimulateTripOver(body);
            //sto.activate();
            //krc.setEnabled(true);
            //PhysicsUtils.setHighPhysicsPrecision(app.getStateManager().getState(HouseAppState.class).getHouse().getRootNode());
            setState(State.Success);
            return;
        }
        setState(State.Fail);
    }

    private void setAvailable(Node body) {
        String placeId = body.getUserData(PLACE_ID_KEY);
        Spatial place = getNearestSeat(placeId, body);
        place.setUserData(SitDownCommand.AVAILABLE_SEAT_KEY, true);
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
    public void interruptCommand(Application app) {
        setState(State.Fail);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + bodyId + ")";
    }

    public String getBodyId() {
        return bodyId;
    }

    @PHATCommParam(mandatory=true, order=1)
    public void setBodyId(String bodyId) {
        this.bodyId = bodyId;
    }
}
