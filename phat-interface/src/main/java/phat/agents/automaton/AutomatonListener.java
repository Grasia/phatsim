package phat.agents.automaton;

public interface AutomatonListener {

    public void preInit(Automaton automaton);
    public void postInit(Automaton automaton);

    public void nextAutomaton(Automaton previousAutomaton, Automaton nextAutomaton);

    public void automatonFinished(Automaton automaton, boolean isSuccessful);
}
