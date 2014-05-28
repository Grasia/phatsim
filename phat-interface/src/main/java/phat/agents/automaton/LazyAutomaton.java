/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.agents.automaton;

import phat.PHATInterface;
import phat.agents.Agent;
import phat.body.commands.StandUpCommand;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.util.Lazy;

/**
 *
 * @author pablo
 */
public class LazyAutomaton extends SimpleState {
    Lazy<Automaton> lazyAutomaton;
    boolean finished;
    
    public LazyAutomaton( Agent agent, Lazy<Automaton> lazyAutomaton) {
        super(agent, 0, "LazyAutomaton");
        this.lazyAutomaton = lazyAutomaton;
    }
    
    @Override
    public boolean isFinished(PHATInterface phatInterface) {
        return lazyAutomaton.getLazy().isFinished(phatInterface);
    }

    @Override
    public void simpleNextState(PHATInterface phatInterface) {
        lazyAutomaton.getLazy().nextState(phatInterface);
    }

    @Override
    public void initState(PHATInterface phatInterface) {
        lazyAutomaton.getLazy().initState(phatInterface);
    }
}
