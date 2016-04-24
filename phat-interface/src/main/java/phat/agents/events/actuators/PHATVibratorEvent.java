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
package phat.agents.events.actuators;

import phat.agents.events.*;
import phat.agents.Agent;

/**
 *
 * @author pablo
 */
public class PHATVibratorEvent extends PHATEvent {
    float minDistance = 1f;
    float volume = 1f;

    public PHATVibratorEvent(String id, VibratorEventSource eventSource) {
        super(id, eventSource);
    }
    
    @Override
    public boolean isPerceptible(Agent agent) {
        if(agent.getLocation() != null && agent.getLocation().distance(getEventSource().getLocation()) < minDistance) {
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
    
    public String getState() {
        return ((VibratorEventSource)getEventSource()).getState();
    }
}
