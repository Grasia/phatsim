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
import phat.body.commands.CloseObjectCommand;
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
public class UseWCAutomaton extends SimpleState implements PHATCommandListener {

    boolean useWCfinished;
    boolean tapClosed = false;
    boolean fail = false;
    private String wcId;
    SitDownCommand sitDownCommand;

    public UseWCAutomaton(Agent agent, String wcId) {
        super(agent, 0, "UseWCAutomaton-" + wcId);
        this.wcId = wcId;
    }

    @Override
    public boolean isFinished(PHATInterface phatInterface) {
        if(tapClosed)
            return true;
        useWCfinished = super.isFinished(phatInterface) || fail;
        if (useWCfinished) {
            agent.runCommand(new StandUpCommand(agent.getId()));
            agent.runCommand(new OpenObjectCommand(agent.getId(), wcId));
            tapClosed = true;
        }
        return useWCfinished;
    }

    @Override
    public void commandStateChanged(PHATCommand command) {
        if (command.getState().equals(PHATCommand.State.Fail)) {
            fail = true;
        }
    }

    @Override
    public void simpleNextState(PHATInterface phatInterface) {
        
    }

    @Override
    public void initState(PHATInterface phatInterface) {
        useWC();
    }

    private void useWC() {
        sitDownCommand = new SitDownCommand(agent.getId(), wcId);
        agent.runCommand(sitDownCommand);
    }
}
