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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import phat.agents.Agent;
import phat.agents.HumanAgent;
import phat.agents.automaton.Automaton;
import phat.agents.automaton.SayAutomaton;

public class SomeoneSayCondition extends AutomatonCondition {

    private final static Logger logger = Logger.getLogger(SomeoneSayCondition.class.getName());
    private String sentence;
    private String humanAgentSourceId;

    public SomeoneSayCondition(String sentence) {
        this.sentence = sentence;
    }
    private List<HumanAgent> agentList;

    private List<HumanAgent> getList(Agent agent) {
        if (agentList == null) {
            agentList = new ArrayList<>();
            if (humanAgentSourceId != null) {
                Agent a = agent.getAgentsAppState().getAgent(humanAgentSourceId);
                if(a != null && a instanceof HumanAgent) {
                    agentList.add((HumanAgent)a);
                }
            } else {
                for (String aid : agent.getAgentsAppState().getAgentIds()) {
                    Agent a = agent.getAgentsAppState().getAgent(aid);
                    if (a instanceof HumanAgent) {
                        agentList.add((HumanAgent) a);
                    }
                }
            }
        }
        return agentList;
    }

    /**
     *
     * @param agent
     * @return
     */
    @Override
    public boolean simpleEvaluation(Agent agent) {
        getList(agent);
        for (HumanAgent ha : agentList) {
            if (ha.getCurrentAction() instanceof SayAutomaton) {
                SayAutomaton sa = (SayAutomaton) ha.getCurrentAction();
                if (sa.getText().toLowerCase().equals(sentence.toLowerCase())) {
                    return true;
                }
            }
        }
        return false;
    }

    public SomeoneSayCondition setHumanAgentSourceId(String humanAgentSourceId) {
        this.humanAgentSourceId = humanAgentSourceId;
        return this;
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
        return "SomeoneSayCondition(" + sentence + ")";
    }
}
