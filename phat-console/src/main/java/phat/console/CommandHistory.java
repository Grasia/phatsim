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
package phat.console;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author pablo
 */
public class CommandHistory {

    private final List<String> commandHistory;
    private int commandIndex;

    public CommandHistory() {
        commandHistory = new ArrayList<>();
        commandIndex = -1;
    }

    public void addCommand(String newCommand) {
        if (newCommand.equals("")) {
            return;
        }
        if (commandHistory.isEmpty() || !commandHistory.get(0).equals(newCommand)) {
            commandHistory.add(0, newCommand);
        }
        commandIndex = -1;
    }

    public String nextCommand() {
        if (commandIndex - 1 >= 0) {
            commandIndex--;
            return commandHistory.get(commandIndex);
        }
        commandIndex = -1;
        return null;
    }

    public String previousCommand() {
        if (commandIndex + 1 < commandHistory.size()) {
            commandIndex++;
            return commandHistory.get(commandIndex);
        }
        return null;
    }
}
