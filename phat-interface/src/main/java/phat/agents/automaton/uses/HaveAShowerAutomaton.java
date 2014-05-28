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
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.util.Lazy;
import phat.util.SpatialFactory;
import phat.util.SpatialUtils;

/**
 *
 * @author pablo
 */
public class HaveAShowerAutomaton extends SimpleState implements PHATCommandListener {

    boolean haveShowerfinished;
    boolean tapClosed = false;
    boolean fail = false;
    private String showerId;
    GoToCommand goIntoShower;
    CloseObjectCommand closeObjectCommand;

    public HaveAShowerAutomaton(Agent agent, String showerId) {
        super(agent, 0, "HaveAShowerAutomaton-" + showerId);
        this.showerId = showerId;
    }

    @Override
    public boolean isFinished(PHATInterface phatInterface) {
        if (tapClosed) {
            return true;
        }
        haveShowerfinished = super.isFinished(phatInterface) || fail;
        if (haveShowerfinished) {
            closeObjectCommand = new CloseObjectCommand(agent.getId(), showerId, this);
            agent.runCommand(closeObjectCommand);
            return false;
        }
        return haveShowerfinished;
    }

    @Override
    public void interrupt() {
        super.interrupt();
        goIntoShower.setFunction(PHATCommand.Function.Interrupt);
        agent.runCommand(goIntoShower);
        tapClosed = true;
        setFinished(true);
    }
    
    @Override
    public void commandStateChanged(PHATCommand command) {
        if (command == goIntoShower
                && command.getState().equals(PHATCommand.State.Success)) {
            agent.runCommand(new OpenObjectCommand(agent.getId(), showerId));
        } else if (command == closeObjectCommand && command.getState().equals(PHATCommand.State.Success)) {
            tapClosed = true;            
        }
        if (command.getState().equals(PHATCommand.State.Fail)) {
            fail = true;
        }
    }

    @Override
    public void simpleNextState(PHATInterface phatInterface) {
    }

    @Override
    public void initState(PHATInterface phatInterface) {
        haveAShower();
    }

    private void haveAShower() {
        goIntoShower = new GoToCommand(agent.getId(), new Lazy<Vector3f>() {
            @Override
            public Vector3f getLazy() {
                Spatial targetSpatial = SpatialUtils.getSpatialById(
                        SpatialFactory.getRootNode(), showerId);
                return targetSpatial.getWorldTranslation();
            }
        }, this);
        goIntoShower.setMinDistance(0.05f);
        agent.runCommand(goIntoShower);
    }
}
