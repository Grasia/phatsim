package phat.agents.automaton;

public interface AutomatonListener {

    public void automatonInitialized(Automaton automaton);

    public void nextAutomaton(Automaton previousAutomaton, Automaton nextAutomaton);

    public void automatonFinished(Automaton automaton, boolean isSuccessful);
}
