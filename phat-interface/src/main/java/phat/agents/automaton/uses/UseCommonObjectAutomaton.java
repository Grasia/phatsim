/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.agents.automaton.uses;

import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

import phat.PHATInterface;
import phat.agents.Agent;
import phat.agents.automaton.SimpleState;
import phat.body.commands.AlignWithCommand;
import phat.body.commands.CloseObjectCommand;
import phat.body.commands.GoCloseToObjectCommand;
import phat.body.commands.GoToCommand;
import phat.body.commands.OpenObjectCommand;
import phat.body.commands.SitDownCommand;
import phat.body.commands.StandUpCommand;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.util.Lazy;
import phat.util.SpatialFactory;
import phat.util.SpatialUtils;

/**
 *
 * @author pablo
 */
public class UseCommonObjectAutomaton extends SimpleState implements PHATCommandListener {

    boolean useObjfinished;
    boolean tapClosed = false;
    boolean fail = false;
    private String objId;
    GoCloseToObjectCommand goCloseToObj;

    public UseCommonObjectAutomaton(Agent agent, String objId) {
        super(agent, 0, "UseWCAutomaton-" + objId);
        this.objId = objId;
    }

    @Override
    public boolean isFinished(PHATInterface phatInterface) {
        if (tapClosed) {
            return true;
        }
        useObjfinished = super.isFinished(phatInterface) || fail;
        if (useObjfinished) {
            System.out.println("FINISHED!!!!");
            agent.runCommand(new CloseObjectCommand(agent.getId(), objId));
            tapClosed = true;
        }
        return useObjfinished;
    }

    @Override
    public void commandStateChanged(PHATCommand command) {
        System.out.println(command.getClass().getSimpleName()+": Command State = "+command.getState().name());
        if (command == goCloseToObj && command.getState().equals(PHATCommand.State.Success)) {
            agent.runCommand(new AlignWithCommand(agent.getId(), objId));
            agent.runCommand(new OpenObjectCommand(agent.getId(), objId));
        }
        if (command.getState().equals(PHATCommand.State.Fail)) {
            fail = true;
        }
    }
    
    @Override
    public void interrupt() {
    	super.interrupt();
        if(goCloseToObj != null && goCloseToObj.getState().equals(PHATCommand.State.Running)) {
            goCloseToObj.setFunction(PHATCommand.Function.Interrupt);
            agent.runCommand(goCloseToObj);
        }
        agent.runCommand(new CloseObjectCommand(agent.getId(), objId));
        tapClosed = true;
            
    	setFinished(true);
    }

    @Override
    public void simpleNextState(PHATInterface phatInterface) {
    }

    @Override
    public void initState(PHATInterface phatInterface) {
        goToUse(objId);
    }

    private void goToUse(final String obj) {
        goCloseToObj = new GoCloseToObjectCommand(agent.getId(), obj, this);
        goCloseToObj.setMinDistance(0.1f);
        agent.runCommand(goCloseToObj);
    }
}
