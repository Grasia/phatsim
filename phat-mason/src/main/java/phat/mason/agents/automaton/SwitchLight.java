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
package phat.mason.agents.automaton;

import phat.mason.PHATSimState;
import phat.mason.agents.Agent;
import sim.engine.SimState;

/**
 *
 * @author pablo
 */
public class SwitchLight extends SimpleState {
    private String roomName;
    private boolean on;
    private boolean done;
    
    public SwitchLight( Agent agent, int priority, int duration, String name, String roomName, boolean on) {
        super(agent, priority, duration, name);
        this.roomName = roomName;
        this.on = on;
        this.done = false;
    }
    
    @Override
    public void nextState(SimState state) {
        PHATSimState phatSimState = (PHATSimState)state;
        
        System.out.println("SwitchLight on!!!!!!!!!!!");
        phatSimState.getMasonAppState().getHouseAdapter().switchLight(roomName, on);
        
        done = true;
    }
    
    @Override
    public boolean isFinished(SimState simState) {
        return done;
    }
}
