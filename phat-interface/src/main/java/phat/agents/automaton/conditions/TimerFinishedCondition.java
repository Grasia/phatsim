package phat.agents.automaton.conditions;

import phat.agents.Agent;
import phat.world.PHATCalendar;

public class TimerFinishedCondition implements AutomatonCondition {
	long seconds;
        PHATCalendar initialTime;
	boolean init = false;
        
	public TimerFinishedCondition(int hours, int minutes, int seconds) {
		super();
		this.seconds = hours*3600 + minutes*60 + seconds;
	}

	@Override
	public boolean evaluate(Agent agent) {
            if(!init) {
                initialTime = (PHATCalendar) agent.getTime().clone();
                init = true;
            }
            long secs = initialTime.spentTimeTo(agent.getTime());
            return secs >= seconds;
	}
        
        public long getSeconds() {
            return seconds;
        }
        
        public void setSeconds(long seconds) {
            this.seconds = seconds;
        }

}
