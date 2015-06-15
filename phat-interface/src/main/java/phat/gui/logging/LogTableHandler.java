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

import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 *
 * @author pablo
 */
public class LogTableHandler extends Handler {
    LogRecordTableModel tableModel;

    public LogTableHandler(LogRecordTableModel tableModel) {
        super();
        this.tableModel = tableModel;
    }
    
    @Override
    public void publish(LogRecord record) {
        /*if (!isLoggable(record)) {
            return;
        }
        System.out.println("\n\n\n***************************");
        System.out.println("publish LogRecord!!!!!!!!!!!!!");
        System.out.println("***************************\n\n\n");*/
        
        tableModel.add(record);
    }
 
 
    @Override
    public void flush() {
    }
 
 
    @Override
    public void close() throws SecurityException {
    }
}
