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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import de.lessvoid.nifty.layout.manager.VerticalLayout;
import phat.gui.logging.control.LogViewerControlPanel;
import phat.gui.logging.control.PatternRowFilter;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;


public class PrettyLogViewerPanel extends JPanel {
	
	public static final int NumberOfVisibleLoggedActions = 2;
	// contains the last two entries for each agent
	Hashtable<String, Vector<LastActionView>> agentLastViews = new Hashtable<String, Vector<LastActionView>> ();
	Hashtable<String, JPanel> agentPanels = new Hashtable<String, JPanel> ();
	
    public PrettyLogViewerPanel(final LogRecordTableModel tableModel) {
    	final JPanel agentContent = new JPanel();
    	agentContent.setLayout(new BoxLayout(agentContent, BoxLayout.Y_AXIS));

    	tableModel.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent arg0) {
				
			 if (arg0.getType() == javax.swing.event.TableModelEvent.INSERT) {
				 for (int k = arg0.getFirstRow(); k <= arg0.getLastRow(); k++) {
					 String agent = tableModel.getValueAt(k, 2).toString();
					 String simtime = tableModel.getValueAt(k, 1).toString();
					 //removes the date from simtime string
					 simtime = simtime.split("-")[0];
					 String action = tableModel.getValueAt(k, 4).toString();
					 String type = tableModel.getValueAt(k, 5).toString();
					 String description = "";
					 if (tableModel.getValueAt(k, 7) != null)
						 description = tableModel.getValueAt(k, 7).toString();
					 String state = tableModel.getValueAt(k, 3).toString();
					 if (!agentLastViews.containsKey(agent)) {
						 agentLastViews.put(agent, new Vector<LastActionView>());
						 JPanel agentPanel = new JPanel(new FlowLayout());
						 //agentPanel.setPreferredSize(new Dimension(650, 100));
						 agentPanels.put(agent, agentPanel);
						 agentContent.add(agentPanels.get(agent));						 
					 }
					 if (type.equals("BActivity")) {
						 agentLastViews.get(agent).insertElementAt(new LastActionView(action, simtime, state, description), 0);
					 }
					 if (agentLastViews.get(agent).size() > NumberOfVisibleLoggedActions) {
						 agentLastViews.get(agent).removeElementAt(agentLastViews.get(agent).size() - 1);
					 }
				 };
				 SwingUtilities.invokeLater(new Runnable() {
					 public void run() {											
						 for (String agentName:agentPanels.keySet()) {
							 agentPanels.get(agentName).removeAll();
							 JLabel nameLabel = new JLabel(transformStringToHtml(agentName));
							 nameLabel.setVerticalAlignment(JLabel.TOP);
							 agentPanels.get(agentName).add(nameLabel);
							 Vector<LastActionView> toAdd = agentLastViews.get(agentName);
							 for (int k = 0; k < toAdd.size(); k++) {
								 if (k == 0) {
									 toAdd.elementAt(k).tellWhen("Now");
								 } else {									 
									 toAdd.elementAt(k).tellWhen("Before");
								 }
								 toAdd.elementAt(k).revalidate();
								 agentPanels.get(agentName).add(toAdd.elementAt(k));	
								 
							 }
							 agentPanels.get(agentName).revalidate();
						 }
						 SwingUtilities.invokeLater(new Runnable() {
							 public void run() {	
								 agentContent.revalidate();
								 agentContent.repaint();
								 
								 // forces a pack of the containing window, otherwise, it does not resize well, even with reinvalidate or invalidate
								 Container c= agentContent.getParent();
								 while (c.getParent()!=null) {
									 c=c.getParent();
								 }
								 ((JFrame)c).pack();

							 }
						 });
						
						 
					 }
				 });
				 
				 
				 
			 }
				
			}
    		
    	});

        setLayout(new BorderLayout());
        add(agentContent, BorderLayout.CENTER);
        JLabel titleLabel = new JLabel("<html><h1><font face=\"Ubuntu\">Last actions performed by actors:</font></h1><br><p><span style=\"background-color: #FFFF00\"><font color=\"black\" face=\"Ubuntu\">Yellow:</font></span><font face=\"Ubuntu\"> means finished</font><br/><span style=\"background-color: #008000\"><font color=\"black\" face=\"Ubuntu\">Green:</span><font face=\"Ubuntu\"> means started</font><br></p></html>");
		//titleLabel.setVerticalTextPosition(JLabel.TOP);
        add(titleLabel, BorderLayout.NORTH);

    }
    
    public static String transformStringToHtml(String strToTransform) {
        String ans = "<html><font face=\"Ubuntu\">";
        String br = "<br>";
        String[] lettersArr = strToTransform.split("");
        for (String letter : lettersArr) {
            ans += letter + br;
        }
        ans += "</font></html>";
        return ans;
    }

    public static void main(String args[]) {
    	JFrame jf=new JFrame();
    	jf.getContentPane().add(new LastActionView("uno", "dos",  "tres","cuatro"));
    	jf.pack();
    	jf.setVisible(true);
    	
    }
}
