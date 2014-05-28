/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.agents.automaton;

import com.jme3.math.Vector3f;

import phat.PHATInterface;
import phat.agents.Agent;
import phat.body.commands.GoToCommand;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.util.Lazy;

/**
 *
 * @author pablo
 */
public class MoveToLazyLocation extends SimpleState implements PHATCommandListener{
    GoToCommand goToCommand;
    boolean targetReached = false;
    
    public MoveToLazyLocation( Agent agent, int priority, String name, 
    		Lazy<Vector3f> destiny) {
        this(agent, priority, name, destiny, 1f);
    }
    
    public MoveToLazyLocation( Agent agent, int priority, String name, Lazy<Vector3f> destiny, float distance) {
        super(agent, priority, name);
        goToCommand = new GoToCommand(agent.getId(), destiny, this);
    }
        
    @Override
    public boolean isFinished(PHATInterface phatInterface) {
        return targetReached;
    }
    
    @Override
    public void commandStateChanged(PHATCommand command) {
        if (command == goToCommand
                && command.getState().equals(PHATCommand.State.Success)) {
            targetReached = true;
        }
    }

    @Override
    public void simpleNextState(PHATInterface phatInterface) {
        
    }

    @Override
    public void initState(PHATInterface phatInterface) {
        targetReached = false;
        agent.runCommand(goToCommand);
    }
}
