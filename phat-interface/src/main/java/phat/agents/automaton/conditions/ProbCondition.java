package phat.agents.automaton.conditions;

import phat.agents.Agent;

public class ProbCondition implements AutomatonCondition {

    float prob;
    float value = -1f;
    
    public ProbCondition(float prob) {
        super();
        this.prob = prob;
    }

    private float getRandomValue(Agent agent) {
        if(value == -1f) {
            this.value = agent.getAgentsAppState().getPHAInterface().getRandom().nextFloat();
        }
        return value;
    }
    @Override
    public boolean evaluate(Agent agent) {
        return value <= prob;
    }
}
