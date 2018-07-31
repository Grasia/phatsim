package phat.gui.logging;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class LastActionView extends JPanel {

	private JLabel laction;
	public LastActionView(String action, String simtime,String state, String description) {
		this.setLayout(new GridBagLayout());		
		GridBagConstraints gbc=new GridBagConstraints();
		if (state.equals("STARTED"))
			this.setBackground(Color.GREEN);
		else 
			this.setBackground(Color.YELLOW);
		JLabel laction=new JLabel("Action:");
		 gbc.gridx=0;
		 gbc.gridy=1;
		 gbc.anchor=GridBagConstraints.WEST;
		 gbc.gridwidth=1;
		 gbc.gridheight=1;
		this.add(laction, gbc);
		 gbc.gridx=1;
		 gbc.gridy=1;
		 gbc.gridwidth=1;
		 gbc.gridheight=1;
		this.add(new JLabel(action), gbc);
		JLabel lsimtime=new JLabel("Simtime:");
		 gbc.gridx=0;
		 gbc.gridy=2;
		 gbc.gridwidth=1;
		 gbc.gridheight=1;
		this.add(lsimtime, gbc);
		 gbc.gridx=1;
		 gbc.gridy=2;
		 gbc.gridwidth=1;
		 gbc.gridheight=1;
		this.add(new JLabel(simtime), gbc);
		 gbc.gridx=0;
		 gbc.gridy=3;
		 gbc.gridwidth=2;
		 //gbc.gridheight=1;
		// gbc.weighty=GridBagConstraints.REMAINDER;
		 gbc.weightx=GridBagConstraints.REMAINDER;
		 JTextArea ta=new JTextArea(description,15,25);
		 ta.setEditable(false);
		 ta.setLineWrap(true);
		 ta.setWrapStyleWord(true);

		 this.add(ta, gbc);
		 //this.add(new JLabel(description), gbc);
		this.setName(action+":"+simtime);		
		
	}
	public void tellWhen(String when) {		
		
		GridBagConstraints gbc=new GridBagConstraints();
		if (laction!=null)
		this.remove(laction);
		laction=new JLabel(when);
		 gbc.gridx=0;
		 gbc.gridy=0;
		 gbc.anchor=GridBagConstraints.WEST;
		 gbc.gridwidth=2;
		 gbc.gridheight=1;
		this.add(laction, gbc);
	}
	
};