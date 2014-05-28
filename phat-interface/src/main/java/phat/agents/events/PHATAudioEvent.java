/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.agents.events;

import phat.agents.Agent;
import phat.agents.automaton.Automaton;

/**
 *
 * @author pablo
 */
public class PHATAudioEvent extends PHATEvent {
    float minDistance = 10f;
    float volume = 1f;

    public PHATAudioEvent(String id, EventSource eventSource) {
        super(id, eventSource);
    }
    
    @Override
    public boolean isPerceptible(Agent agent) {
        if(agent.getLocation().distance(getEventSource().getLocation()) < minDistance) {
            return true;
        }
        return false;
    }

    public float getMinDistance() {
        return minDistance;
    }

    public void setMinDistance(float minDistance) {
        this.minDistance = minDistance;
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }
}
