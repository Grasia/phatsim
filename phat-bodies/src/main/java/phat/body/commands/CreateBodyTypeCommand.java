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
import com.jme3.scene.Node;

import java.util.logging.Level;
import jme3tools.optimize.GeometryBatchFactory;

import phat.body.BodiesAppState;
import phat.body.BodyUtils;
import phat.body.control.animation.BasicCharacterAnimControl;
import phat.body.control.navigation.PersuitAndAvoidControl;
import phat.body.control.navigation.navmesh.NavMeshMovementControl;
import phat.body.control.physics.PHATCharacterControl;
import phat.bullet.control.ragdoll.BVHRagdollPreset;
import phat.commands.PHATCommand;
import phat.commands.PHATCommand.State;
import phat.body.control.animation.FootStepsControl;

/**
 *
 * @author pablo
 */
public class CreateBodyTypeCommand extends PHATCommand {

    private String bodyId;
    private String urlResource;

    public CreateBodyTypeCommand(String bodyId, String urlResource) {
        super(null);
        this.bodyId = bodyId;
        this.urlResource = urlResource;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }
    
    @Override
    public void runCommand(Application app) {
        BodiesAppState bodiesAppState = app.getStateManager().getState(BodiesAppState.class);
        Node body = (Node) bodiesAppState.getAssetManager().loadModel(urlResource);
        if(body.getName().contains("LP")) {
            body = (Node) body.getChild("Body");
            body.removeFromParent();
            body.setUserData("Speed", 0.5f);
        }
        body.setName(bodyId);
        body.setUserData("ID", bodyId);
        body.setUserData("ROLE", "Body");
        
        GeometryBatchFactory.optimize(body);
        
        PHATCharacterControl phatCharacterControl = body.getControl(PHATCharacterControl.class);
        if(phatCharacterControl == null) {
            phatCharacterControl = new PHATCharacterControl(0.2f, 1.7f, 80f);
            body.addControl(phatCharacterControl);
        }
        
        BasicCharacterAnimControl bcac = body.getControl(BasicCharacterAnimControl.class);
        if(bcac == null) {
            bcac = new BasicCharacterAnimControl();
            body.addControl(bcac);
        }
        
        NavMeshMovementControl navMesh = body.getControl(NavMeshMovementControl.class);
        if(navMesh == null) {
            navMesh = new NavMeshMovementControl();
            body.addControl(navMesh);
        }
        
        BVHRagdollPreset preset = new BVHRagdollPreset();
        KinematicRagdollControl krc = new KinematicRagdollControl(preset, 0.5f);        
        krc.setKinematicMode();   
        krc.setRootMass(10f);
        body.addControl(krc);
        krc.setEnabled(false);
        
        body.addControl(new FootStepsControl());
        //body.addControl(new RandomWalkControl());
        body.addControl(new PersuitAndAvoidControl());
        //body.addControl(new LookAtControl());
        
        bodiesAppState.addBody(bodyId, body);
        
        //PhysicsUtils.setHighPhysicsPrecision(body);
        //body.addControl(new VisionControl());
        
        BodyUtils.setBodyPosture(body, BodyUtils.BodyPosture.Standing);
        
        setState(State.Success);
    }
    @Override
	public void interruptCommand(Application app) {
		setState(State.Interrupted);
	}
    
    @Override
    public String toString() {
        return getClass().getSimpleName()+"("+bodyId+", "+urlResource+")";
    }
}
