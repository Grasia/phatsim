/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.mason.utils;

import java.awt.Font;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

/**
 *
 * @author Pablo
 */
public class AlarmFrame extends JFrame {
    
    public static void main(String [] args) {
        AlarmFrame f = new AlarmFrame();
        f.setVisible(true);
    }
    
    public AlarmFrame() {
        setTitle("System Alarm");
        
        JTextArea ta = new JTextArea("Time out: "+"The patient has been on the floor of the bathroom for 5 minutes!");        
        Font font = new Font("Verdana", Font.BOLD, 12);
        ta.setFont(font);
        
        getContentPane().add(ta);
        pack();
        try {
            ImageIcon ii = new ImageIcon(new URL("http://www.xlab.tv/images/Icon-Danger.gif"));
            JOptionPane.showMessageDialog(this, "Time out: "+"The patient has been on the floor of the bathroom for 5 minutes!", "System Alarm",-1, ii);
        } catch (MalformedURLException ex) {
            Logger.getLogger(AlarmFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        //setSize(550, 80);
    }
}
