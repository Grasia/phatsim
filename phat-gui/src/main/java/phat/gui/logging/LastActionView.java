package phat.gui.logging;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import java.awt.Font;

public class LastActionView extends JPanel {

	private String fontName = "Ubuntu";
	private JLabel laction;
	public LastActionView(String action, String simtime, String state, String description) {
		this.setLayout(new GridBagLayout());		
		GridBagConstraints gbc = new GridBagConstraints();
		if (state.equals("STARTED") || state.equals("DEFAULT_STARTED")
				|| state.equals("DEFAULT"))
			this.setBackground(Color.GREEN);
		else
			this.setBackground(Color.YELLOW);
		JLabel laction = new JLabel(state + ": ");
		laction.setFont(new Font(fontName, Font.BOLD, 14));
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
		JLabel act = new JLabel(action);
		act.setFont(new Font(fontName, Font.BOLD, 15));
		this.add(act, gbc);
		JLabel lsimtime=new JLabel("Simtime: ");
		lsimtime.setFont(new Font(fontName, Font.BOLD, 14));
		 gbc.gridx=0;
		 gbc.gridy=2;
		 gbc.gridwidth=1;
		 gbc.gridheight=1;
		this.add(lsimtime, gbc);
		 gbc.gridx=1;
		 gbc.gridy=2;
		 gbc.gridwidth=1;
		 gbc.gridheight=1;
		 JLabel simt = new JLabel(simtime);
		 simt.setFont(new Font(fontName, Font.BOLD, 15));
		this.add(simt, gbc);
		 gbc.gridx=0;
		 gbc.gridy=3;
		 gbc.gridwidth=2;
		 //gbc.gridheight=1;
		// gbc.weighty=GridBagConstraints.REMAINDER;
		 gbc.weightx=GridBagConstraints.REMAINDER;
		 JTextArea ta=new JTextArea(description,8,25);
		 ta.setFont(new Font(fontName, Font.BOLD, 16));
		 ta.setEditable(false);
		 ta.setLineWrap(true);
		 ta.setWrapStyleWord(true);

		 this.add(ta, gbc);
		 //this.add(new JLabel(description), gbc);
		this.setName(action+":"+simtime);		
		
	}
};