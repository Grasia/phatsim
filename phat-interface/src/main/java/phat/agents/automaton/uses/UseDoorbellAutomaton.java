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
public class UseDoorbellAutomaton extends SimpleState implements PHATCommandListener {

    boolean useDoorbellfinished;
    boolean buttonPushed = false;
    boolean fail = false;
    private String doorbellId;
    GoToCommand goCloseToDoorbell;
    OpenObjectCommand useDoorbell;

    public UseDoorbellAutomaton(Agent agent, String doorbellId) {
        super(agent, 0, "UseDoorbellAutomaton-" + doorbellId);
        this.doorbellId = doorbellId;
    }

    @Override
    public boolean isFinished(PHATInterface phatInterface) {        
        useDoorbellfinished = super.isFinished(phatInterface) || fail;
        if (useDoorbellfinished) {
            buttonPushed = true;
        }
        return buttonPushed;
    }

    @Override
    public void commandStateChanged(PHATCommand command) {
        System.out.println("commandStateChanged -> "+command.toString());
        if (command == goCloseToDoorbell
                && command.getState().equals(PHATCommand.State.Success)) {
            useDoorbell = new OpenObjectCommand(agent.getId(), doorbellId, this);
            agent.runCommand(useDoorbell);
        } else if (command == useDoorbell
                && command.getState().equals(PHATCommand.State.Success)) {
            System.out.println("Success!");
            buttonPushed = true;
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
        useWC();
    }

    private void useWC() {
        goCloseToDoorbell = new GoToCommand(agent.getId(), new Lazy<Vector3f>() {
            @Override
            public Vector3f getLazy() {
                Spatial targetSpatial = SpatialUtils.getSpatialById(
                        SpatialFactory.getRootNode(), doorbellId);
                return targetSpatial.getWorldTranslation();
            }
        }, this);
        goCloseToDoorbell.setMinDistance(0.5f);
        agent.runCommand(goCloseToDoorbell);
    }
}
