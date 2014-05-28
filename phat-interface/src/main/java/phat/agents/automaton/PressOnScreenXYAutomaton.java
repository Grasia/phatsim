/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.agents.automaton;

import phat.PHATInterface;
import phat.agents.Agent;
import phat.agents.automaton.conditions.TimerFinishedCondition;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.devices.commands.PressOnScreen;

/**
 *
 * @author Pablo
 */
public class PressOnScreenXYAutomaton extends SimpleState implements PHATCommandListener {

    private String smartphoneId;
    private int x;
    private int y;

    PressOnScreen pressOnScreenCommand;
    boolean done = false;
    
    public PressOnScreenXYAutomaton(Agent agent, String name, String smartphoneId, int x, int y) {
        super(agent, 0, name);
        this.smartphoneId = smartphoneId;
        this.x = x;
        this.y = y;
    }

    @Override
    public void simpleNextState(PHATInterface phatInterface) {
        
    }

    @Override
    public boolean isFinished(PHATInterface phatInterface) {
        return super.isFinished(phatInterface) && done;
    }
    
    @Override
    public void commandStateChanged(PHATCommand command) {
        if (command == pressOnScreenCommand
                && command.getState().equals(PHATCommand.State.Success)) {
            done = true;
        }
    }

    @Override
    public void initState(PHATInterface phatInterface) {
        setFinishCondition(new TimerFinishedCondition(0, 0, 1));
        pressOnScreenCommand = new PressOnScreen(smartphoneId, x, y, this);
        phatInterface.getDevicesConfig().runCommand(pressOnScreenCommand);
    }
}
