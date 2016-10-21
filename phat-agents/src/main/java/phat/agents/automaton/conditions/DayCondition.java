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

import java.util.logging.Logger;
import phat.agents.Agent;
import phat.agents.automaton.Automaton;

public class DayCondition extends AutomatonCondition {

    private final static Logger logger = Logger.getLogger(SymptomCondition.class.getName()); 

    public enum DAY_OF_THE_WEEK {Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday};
    DAY_OF_THE_WEEK dayOfTheWeek;
    
    public DayCondition(DAY_OF_THE_WEEK dayOfTheWeek) {
        this.dayOfTheWeek = dayOfTheWeek;
    }

    /**
     * Return true if the agent has a symptom with the name symptomName 
     * and the same level of symptomLevel
     * or if the agent doesn't have the symptome and symptomLeve == "NONE",
     * in another case it returns false;
     * 
     * @param agent
     * @return 
     */
    @Override
    public boolean simpleEvaluation(Agent agent) {
        return dayOfTheWeek.ordinal() + 1 == agent.getTime().getDayOfWeek();
    }

    @Override
    public void automatonReset(Automaton automaton) {
    }
    
    @Override
    public void automatonInterrupted(Automaton automaton) {
    }

    @Override
    public void automatonResumed(Automaton automaton) {
    }
    
    @Override
    public String toString() {
        return "SymptomCondition("+dayOfTheWeek+")";
    }

    @AutoCondParam(name = "day")
    public DAY_OF_THE_WEEK getDayOfTheWeek() {
        return dayOfTheWeek;
    }
}
