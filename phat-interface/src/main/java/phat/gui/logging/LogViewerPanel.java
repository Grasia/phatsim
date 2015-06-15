/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.gui.logging;

import java.awt.Dimension;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 *
 * @author pablo
 */
public class LogViewerPanel extends JPanel {

    public LogViewerPanel(LogRecordTableModel tableModel) {
        JTable table = new JTable(tableModel);
        table.setPreferredScrollableViewportSize(new Dimension(700, 700));
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);

        //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(table);

        //Add the scroll pane to this panel.
        add(scrollPane);
    }
    
}
