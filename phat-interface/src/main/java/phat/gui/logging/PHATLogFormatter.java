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
package phat.gui.logging;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import phat.agents.automaton.Automaton;

/**
 *
 * @author pablo
 */
public class PHATLogFormatter extends Formatter {
    String separator = ":";
    String endLogMark = "\n";
    
    @Override
    public synchronized String format(LogRecord record) {
        return record.getParameters()[0]+separator+
                record.getLoggerName()+separator+
                record.getParameters()[1]+separator+
                record.getParameters()[2]+separator+
                record.getParameters()[3]+separator+
                ((Automaton)record.getParameters()[4]).getFinishCondition()+separator+
                record.getMessage()+endLogMark;
    }
}
