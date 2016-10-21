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
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.AbstractPhysicsControl;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;

import java.util.logging.Level;

import phat.body.BodiesAppState;
import phat.body.control.navigation.StraightMovementControl;
import phat.body.control.physics.ragdoll.SimulateTripOver;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.commands.PHATCommand.State;

/**
 *
 * @author pablo
 */
public class TripOverCommand extends PHATCommand {

    private String bodyId;

    public TripOverCommand() {
    }

    public TripOverCommand(String bodyId, PHATCommandListener listener) {
        super(listener);
        this.bodyId = bodyId;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }

    public TripOverCommand(String bodyId) {
        this(bodyId, null);
    }

    @Override
    public void runCommand(Application app) {
        BodiesAppState bodiesAppState = app.getStateManager().getState(BodiesAppState.class);
        BulletAppState bulletAppState = app.getStateManager().getState(BulletAppState.class);

        Node body = bodiesAppState.getBody(bodyId);

        if (body != null && body.getParent() != null) {
            printControls(body);
            SimulateTripOver sto = body.getControl(SimulateTripOver.class);
            if(sto == null) {
                System.out.println("SimulateTripOver");
                sto = new SimulateTripOver();          
                body.addControl(sto);
            }
            
            StraightMovementControl smc = body.getControl(StraightMovementControl.class);
            if (smc != null) {
                System.out.println("StraightMovementControl stopped!!");
                body.removeControl(smc);
            }
            
            sto.activate();

            printControls(body);

            //SimulateTripOver sto = new SimulateTripOver(body);
            //sto.activate();
            //krc.setEnabled(true);
            //PhysicsUtils.setHighPhysicsPrecision(app.getStateManager().getState(HouseAppState.class).getHouse().getRootNode());
            setState(State.Success);
            return;
        }
        setState(State.Fail);
    }

    public void printControls(Spatial spatial) {
        for (int i = 0; i < spatial.getNumControls(); i++) {
            Control c = spatial.getControl(i);
            System.out.print("\t-" + c.getClass().getSimpleName());
            if (c instanceof AbstractControl) {
                System.out.println(" " + ((AbstractControl) c).isEnabled());
            } else if (c instanceof AbstractPhysicsControl) {
                System.out.println(" " + ((AbstractPhysicsControl) c).isEnabled());
            } else {
                System.out.println("");
            }
        }
        System.out.println("");
    }

    @Override
    public void interruptCommand(Application app) {
        setState(State.Fail);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + bodyId + ")";
    }
}
