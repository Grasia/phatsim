package phat.agents.automaton;

import phat.agents.automaton.conditions.AutomatonCondition;
import phat.agents.automaton.conditions.EmptyCondition;

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
	
	public void setCondition(AutomatonCondition ac) {
		this.ac = ac;
	}
	
	public Automaton getTarget(){
		return target;
	}
	
	public boolean evaluate(){
		return ac.evaluate(target.getAgent());
	}
}
