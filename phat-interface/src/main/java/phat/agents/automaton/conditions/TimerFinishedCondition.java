/*
 * Copyright (C) 2014 Pablo Campillo-Sanchez <pabcampi@ucm.es>
 *
 * This software has been developed as part of the 
 * SociAAL project directed by Jorge J. Gomez Sanz
 * (http://grasia.fdi.ucm.es/sociaal)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package phat.agents.automaton.conditions;

import phat.agents.Agent;
import phat.agents.automaton.Automaton;
import phat.world.PHATCalendar;

public class TimerFinishedCondition extends AutomatonCondition {

    long seconds;
    long timeLeft;
    long secondsInterrupted = 0;
    PHATCalendar initialTime;
    PHATCalendar interruption;
    boolean init = false;

    public TimerFinishedCondition(int hours, int minutes, int seconds) {
        super();
        this.seconds = hours * 3600 + minutes * 60 + seconds;
    }

    @Override
    public boolean simpleEvaluation(Agent agent) {
        if (!init) {
            initialTime = (PHATCalendar) agent.getTime().clone();
            init = true;
        }
        long secs = initialTime.spentTimeTo(agent.getTime());
        timeLeft = secs - secondsInterrupted;
        secondsInterrupted = 0;
        return timeLeft >= seconds;
    }

    @Override
    public void automatonReset(Automaton automaton) {
        init = false;
    }
    
    public long getSeconds() {
        return seconds;
    }

    public void setSeconds(long seconds) {
        this.seconds = seconds;
    }

    public long getTimeLeft() {
        return timeLeft;
    }

    @Override
    public void automatonInterrupted(Automaton automaton) {
        interruption = (PHATCalendar) automaton.getAgent().getTime().clone();
    }

    @Override
    public void automatonResumed(Automaton automaton) {
        if (interruption != null) {
            secondsInterrupted += interruption.spentTimeTo(automaton.getAgent().getTime());
            interruption = null;
        }
    }
    
    @Override
    public String toString() {
        return "TimerFinishedCondition("+seconds+","+timeLeft+")";
    }
}
