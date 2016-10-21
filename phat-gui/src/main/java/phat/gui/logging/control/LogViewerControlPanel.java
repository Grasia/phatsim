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
package phat.gui.logging.control;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import phat.agents.AgentsAppState;
import phat.agents.automaton.Automaton;
import phat.gui.logging.LogRecordTableModel;

/**
 *
 * @author pablo
 */
public class LogViewerControlPanel extends JPanel {

    LogRecordTableModel tableModel;
    JTable table;
    AgentsAppState agentsAppState;
    TableRowSorter<LogRecordTableModel> sorter;

    public LogViewerControlPanel(JTable table) {
        this.tableModel = (LogRecordTableModel) table.getModel();
        this.table = table;
        this.agentsAppState = tableModel.getAgentsAppState();

        setBorder(new TitledBorder("Filtering Controls"));
        createStateChecks();

    }
    ItemListener updateTable = new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
            System.out.println("Selected: " + ((JCheckBoxMenuItem) e.getItem()).getText());
            sorter.allRowsChanged();
        }
    };

    private void createStateChecks() {
        CheckBoxMenuItemRowFilter filter = new CheckBoxMenuItemRowFilter();

        List<String> agentsOptions = new ArrayList<>();
        for (String id : agentsAppState.getAgentIds()) {
            agentsOptions.add(id);
        }
        createGroupFiltering("Agents", filter, 2, agentsOptions);

        List<String> stateOptions = new ArrayList<>();
        for (Automaton.STATE state : Automaton.STATE.values()) {
            stateOptions.add(state.name());
        }
        createGroupFiltering("States", filter, 3, stateOptions);

        this.sorter = new TableRowSorter<>(tableModel);
        sorter.setRowFilter(filter);
        table.setRowSorter(sorter);
    }

    private void createGroupFiltering(String name, CheckBoxMenuItemRowFilter filter, int index, List<String> options) {
        final JPopupMenu menu = new JPopupMenu();
        List<JCheckBoxMenuItem> cBoxes = new ArrayList<>();

        // States
        filter.setCheckBoxItemList(index, cBoxes);

        for (String op: options) {
            JCheckBoxMenuItem cbmi = new JCheckBoxMenuItem(op);
            cbmi.addItemListener(updateTable);
            menu.add(cbmi);
            cBoxes.add(cbmi);
        }

        final JButton button = new JButton();
        button.setAction(new AbstractAction(name) {
            @Override
            public void actionPerformed(ActionEvent e) {
                menu.show(button, 0, button.getHeight());
            }
        });
        add(button);
    }
}
