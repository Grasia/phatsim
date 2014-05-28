package phat.agents.automaton;

import phat.PHATInterface;
import phat.agents.Agent;
import phat.body.commands.FallDownCommand;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;

/**
 *
 * @author Pablo
 */
public class FallAutomaton extends SimpleState implements PHATCommandListener {
    
    boolean fall = false;
    
    public FallAutomaton(Agent agent, String name){
        super(agent,0,name);

    }

    @Override
    public boolean isFinished(PHATInterface phatInterface) {
        return fall;
    }
    
    @Override
    public void simpleNextState(PHATInterface phatInterface) {
        if(!fall) {
            agent.runCommand(new FallDownCommand(agent.getId(), this));
        }
    }

    @Override
    public void initState(PHATInterface phatInterface) {
    }

    @Override
    public void commandStateChanged(PHATCommand command) {
        fall = true;
    }
}
