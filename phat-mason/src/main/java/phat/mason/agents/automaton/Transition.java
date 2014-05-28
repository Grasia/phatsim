package phat.mason.agents.automaton;

public class Transition {

	private AutomatonCondition ac=null;
	private Automaton target=null;
	
	public Transition(AutomatonCondition ac, Automaton target)  {
		this.ac=ac;
		this.target=target;
	}
	
	public Transition(Automaton target)  {
		this.ac=new EmptyCondition();
		this.target=target;
	}
	
	public Automaton getTarget(){
		return target;
	}
	
	public boolean evaluate(){
		return ac.evaluate();
	}
}
