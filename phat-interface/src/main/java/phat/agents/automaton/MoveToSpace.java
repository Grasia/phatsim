/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.agents.automaton;

import phat.PHATInterface;
import phat.agents.Agent;
import phat.body.commands.GoToSpaceCommand;
import phat.body.commands.SetBodyInHouseSpaceCommand;
import phat.body.commands.SetSpeedDisplacemenetCommand;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;

/**
 *
 * @author pablo
 */
public class MoveToSpace extends SimpleState implements PHATCommandListener {

    String destinyName;
    PHATCommand goToSpaceCommand;
    boolean destinyReached = false;
    float speed = -1f;
    
    public MoveToSpace(Agent agent, String name, String destinyName) {
        this(agent, name, destinyName, 1f);
    }

    @Override
    public void interrupt() {
        super.interrupt();
        goToSpaceCommand.setFunction(PHATCommand.Function.Interrupt);
        agent.runCommand(goToSpaceCommand);
        setFinished(true);
    }

    public MoveToSpace(Agent agent, String name, String destinyName, float distance) {
        super(agent, 0, name);
        this.destinyName = destinyName;
    }

    @Override
    public boolean isFinished(PHATInterface phatInterface) {
        return super.isFinished(phatInterface) || destinyReached;
    }

    @Override
    public void commandStateChanged(PHATCommand command) {
        if (command == goToSpaceCommand) {
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
            goToSpaceCommand = new GoToSpaceCommand(agent.getId(), destinyName, this);
        } else {
            goToSpaceCommand = new SetBodyInHouseSpaceCommand(agent.getId(), "House1", "WorldEntry1", this);
        }
        agent.runCommand(goToSpaceCommand);
    }

    public float getSpeed() {
        return speed;
    }

    public MoveToSpace setSpeed(float speed) {
        this.speed = speed;
        return this;
    }
}
