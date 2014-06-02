/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.agents.automaton.uses;

import phat.PHATInterface;
import phat.agents.Agent;
import phat.agents.automaton.SimpleState;
import phat.agents.automaton.conditions.TimerFinishedCondition;
import phat.body.commands.GoCloseToObjectCommand;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.config.DeviceConfigurator;
import phat.devices.commands.SwitchTVCommand;

/**
 *
 * @author Pablo
 */
public class SwitchTVAutomaton extends SimpleState implements PHATCommandListener {

    private String tvId;
    private boolean on;
    GoCloseToObjectCommand goCloseToObj;
    DeviceConfigurator deviceConfigurator;
    SwitchTVCommand switchTVCommandd;
    boolean done = false;

    public SwitchTVAutomaton(Agent agent, String tvId, boolean on) {
        super(agent, 0, "SwitchTVAutomaton");
        this.tvId = tvId;
        this.on = on;
    }

    @Override
    public void simpleNextState(PHATInterface phatInterface) {
    }

    @Override
    public boolean isFinished(PHATInterface phatInterface) {
        return done;
    }

    @Override
    public void commandStateChanged(PHATCommand command) {
        if (command == goCloseToObj
                && command.getState().equals(PHATCommand.State.Success)) {
            switchTVCommandd = new SwitchTVCommand(tvId, on, this);
            deviceConfigurator.runCommand(switchTVCommandd);            
            done = true;
        }
    }

    @Override
    public void initState(PHATInterface phatInterface) {
        goCloseToObj = new GoCloseToObjectCommand(agent.getId(), tvId, this);
        goCloseToObj.setMinDistance(0.1f);
        agent.runCommand(goCloseToObj);
        deviceConfigurator = phatInterface.getDevicesConfig();
    }
}
