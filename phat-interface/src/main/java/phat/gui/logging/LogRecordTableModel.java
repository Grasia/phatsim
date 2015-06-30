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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogRecord;
import javax.swing.table.AbstractTableModel;
import phat.agents.automaton.Automaton;

/**
 *
 * @author pablo
 */
public class LogRecordTableModel extends AbstractTableModel {

    List<LogRecord> logRecords = new ArrayList<>();

    @Override
    public int getRowCount() {
        return logRecords.size();
    }

    @Override
    public int getColumnCount() {
        return 7;
    }

    @Override
    public String getColumnName(int col) {
        switch (col) {
            case 0:
                return "SimTime";
            case 1:
                return "Name";
            case 2:
                return "State";
            case 3:
                return "Action";
            case 4:
                return "Type";
            case 5:
                return "FinishCondition";
            case 6:
                return "Description";
        }
        return "null";
    }

    // {time, state, taskID, taskType}
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        LogRecord log = logRecords.get(rowIndex);
        if (log != null) {
            Object[] params = log.getParameters();
            switch (columnIndex) {
                case 0:
                    if (params != null) {
                        return Integer.parseInt((String) log.getParameters()[0]);
                    }
                    break;
                case 1:
                    return log.getLoggerName();
                case 2:
                    if (params != null) {
                        return params[1];
                    }
                    break;
                case 3:
                    if (params != null) {
                        return params[2];
                    }
                    break;
                case 4:
                    if (params != null) {
                        return params[3];
                    }
                    break;
                case 5:
                    if (params != null) {
                        Automaton aut = (Automaton) params[4];
                        return aut.getFinishCondition();
                    }
                    break;
                case 6:
                    return log.getMessage();
            }
        }
        return "";
    }

    public void add(LogRecord logRecord) {
        logRecords.add(logRecord);
        int lastRow = logRecords.size() - 1;
        fireTableRowsInserted(lastRow, lastRow);
    }
}
