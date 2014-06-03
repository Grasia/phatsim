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

package phat.commands;

import com.jme3.app.Application;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pablo
 */
public abstract class PHATCommand {

    protected static final Logger logger = Logger.getLogger(PHATCommand.class.getName());
    PHATCommandListener listener;

    public enum Function {

        Run, Interrupt
    }
    Function function = Function.Run;

    public enum State {

        Waiting, Running, Interrupted, Success, Fail
    }
    State state = State.Waiting;

    public PHATCommand(PHATCommandListener listener) {
        this.listener = listener;
    }

    public void run(Application app) {
        if (function.equals(Function.Run) && state.equals(State.Waiting)) {
            setState(State.Running);
            logger.log(Level.INFO, "Running Command: {0}", new Object[]{this});
            runCommand(app);
        } else if (function.equals(Function.Interrupt) && state.equals(State.Running)) {
            logger.log(Level.INFO, "Interrupting Command: {0}", new Object[]{this});
            interruptCommand(app);
        }
    }

    public abstract void runCommand(Application app);

    public abstract void interruptCommand(Application app);

    public State getState() {
        return state;
    }

    protected void setState(PHATCommand.State state) {
        this.state = state;
        logger.log(Level.INFO, "Command {1} Finished: {0}", new Object[]{state.name(), toString()});
        if (listener != null) {
            listener.commandStateChanged(this);
        }
    }

    public Function getFunction() {
        return function;
    }

    public void setFunction(Function function) {
        this.function = function;
    }
}
