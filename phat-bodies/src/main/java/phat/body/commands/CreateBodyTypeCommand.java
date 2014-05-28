/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.body.commands;

import com.jme3.app.Application;
import com.jme3.bullet.control.KinematicRagdollControl;
import com.jme3.scene.Node;

import java.util.logging.Level;
import jme3tools.optimize.GeometryBatchFactory;

import phat.body.BodiesAppState;
import phat.body.control.animation.BasicCharacterAnimControl;
import phat.body.control.navigation.PersuitAndAvoidControl;
import phat.body.control.navigation.RandomWalkControl;
import phat.body.control.navigation.navmesh.NavMeshMovementControl;
import phat.body.control.physics.PHATCharacterControl;
import phat.body.control.physics.ragdoll.BVHRagdollPreset;
import phat.commands.PHATCommand;
import phat.commands.PHATCommand.State;

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
        
        //GeometryBatchFactory.optimize(body);
        
        PHATCharacterControl phatCharacterControl = body.getControl(PHATCharacterControl.class);
        if(phatCharacterControl == null) {
            phatCharacterControl = new PHATCharacterControl();
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
        
        /*BVHRagdollPreset preset = new BVHRagdollPreset();
        KinematicRagdollControl krc = new KinematicRagdollControl(preset, 0.5f);        
        krc.setKinematicMode();        
        body.addControl(krc);
        krc.setEnabled(false);*/
        
        //body.addControl(new RandomWalkControl());
        body.addControl(new PersuitAndAvoidControl());
        //body.addControl(new LookAtControl());
        
        bodiesAppState.getAvailableBodies().put(bodyId, body);
        
        //PhysicsUtils.setHighPhysicsPrecision(body);
        
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
