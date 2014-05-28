/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.agents.automaton;

import phat.PHATInterface;
import phat.agents.Agent;
import phat.body.commands.GoCloseToBodyCommand;
import phat.body.commands.GoToSpaceCommand;
import phat.body.commands.SetBodyInHouseSpaceCommand;
import phat.body.commands.SetSpeedDisplacemenetCommand;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;

/**
 *
 * @author pablo
 */
public class MoveToBodyLocAutomaton extends SimpleState implements PHATCommandListener {

    String destinyBodyName;
    PHATCommand moveToBodyCommand;
    boolean destinyReached = false;
    float speed = -1f;
    
    public MoveToBodyLocAutomaton(Agent agent, String name, String destinyBodyName) {
        this(agent, name, destinyBodyName, 1f);
    }

    @Override
    public void interrupt() {
        super.interrupt();
        moveToBodyCommand.setFunction(PHATCommand.Function.Interrupt);
        agent.runCommand(moveToBodyCommand);
        setFinished(true);
    }

    public MoveToBodyLocAutomaton(Agent agent, String name, String destinyBodyName, float distance) {
        super(agent, 0, name);
        this.destinyBodyName = destinyBodyName;
    }

    @Override
    public boolean isFinished(PHATInterface phatInterface) {
        return super.isFinished(phatInterface) || destinyReached;
    }

    @Override
    public void commandStateChanged(PHATCommand command) {
        if (command == moveToBodyCommand) {
            if (command.getState().equals(PHATCommand.State.Success)
                    || command.getState().equals(PHATCommand.State.Interrupted)) {
                destinyReached = true;
            }
        }
    }

    @Override
    public void simpleNextState(PHATInterface phatInterface) {
    }

    @Override
    public void initState(PHATInterface phatInterface) {
        if(speed > 0) {
            agent.runCommand(new SetSpeedDisplacemenetCommand(agent.getId(), speed));
        }
        if (agent.isInTheWorld()) {
            moveToBodyCommand = new GoCloseToBodyCommand(agent.getId(), destinyBodyName, this);
        } else {
            moveToBodyCommand = new SetBodyInHouseSpaceCommand(agent.getId(), "House1", "WorldEntry1", this);
        }
        agent.runCommand(moveToBodyCommand);
    }

    public float getSpeed() {
        return speed;
    }

    public MoveToBodyLocAutomaton setSpeed(float speed) {
        this.speed = speed;
        return this;
    }
}
