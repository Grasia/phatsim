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
package phat.agents.hsm;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author pablo
 */
public abstract class HFSM {
    public enum State {Initiation, Running, Paused, Restarted, Finished}
    public enum Result {Undeterminated, Fail, Success}
    
    State state = State.Initiation;
    Result result = Result.Undeterminated;
    
    HFSM parent = null;
    
    public void proccess() {
        switch(state) {
            case Finished:
                finished();
                break;
            case Initiation:
                init();
                break;
            case Running:
                run();
                break;
            case Paused:
                pause();
                break;
            case Restarted:
                restarted();
                break;
        }
    }
    
    public abstract void init();
    
    public abstract void run();
    
    public abstract void pause();
    
    public abstract void restarted();
    
    public abstract void finished();
    
    public State getState() {
        return state;
    }
    
    public void setState(State state) {
        this.state = state;
    }
    
    public Result getResult() {
        return result;
    }
    
    public HFSM getParent() {
        return parent;
    }
}
