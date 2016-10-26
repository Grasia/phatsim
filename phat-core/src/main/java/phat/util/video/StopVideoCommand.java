/*
 * Copyright (C) 2016 Pablo Campillo-Sanchez <pabcampi@ucm.es>
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
package phat.util.video;

import java.io.File;
import java.util.logging.Logger;

import com.jme3.app.Application;

import phat.commands.PHATCommand;

/**
 *
 * @author Rafael Pax
 *
 */
public class StopVideoCommand extends PHATCommand {

    private final File output;

    public StopVideoCommand(File output) {
        super(null);
        this.output = output;
    }

    @Override
    public void runCommand(Application app) {
        Logger.getLogger(getClass().getName()).info("Video recording stopped");
        VideoRenderAppState videoAppState = app.getStateManager()
                .getState(VideoRenderAppState.class);
        if (videoAppState != null) {
            app.getStateManager().detach(videoAppState);
        }
        setState(State.Success);
    }

    @Override
    public void interruptCommand(Application app) {   
    }
}
