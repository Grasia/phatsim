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
package phat.console;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.text.DefaultCaret;

/**
 *
 * @author pablo
 */
public class ConsoleGUI extends OutputStream {

    JTextField commandTextField;
    JScrollPane outScrollPane;
    
    PrintStream consoleOut;
    ResponseParser responseParser;

    ConsoleControl control;
    
    public ConsoleGUI(ConsoleControl control) {
        this.control = control;
        responseParser = new ResponseParserImpl();

        JFrame frame = new JFrame();
        frame.add(new JLabel(" Outout"), BorderLayout.NORTH);

        JTextArea ta = new JTextArea();
        ta.setEditable(false);
        Font font = new Font(Font.MONOSPACED, Font.PLAIN, 12);
        ta.setFont(font);
        ConsoleGUI taos = new ConsoleGUI(ta, 60);
        consoleOut = new PrintStream(taos);
        //System.setOut(ps);
        //System.setErr(ps);
        DefaultCaret caret = (DefaultCaret) ta.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        outScrollPane = new JScrollPane(ta);
        outScrollPane.setSize(600, 400);
        frame.add(outScrollPane);

        commandTextField = new JTextField();
        commandTextField.setSize(600, 15);
        commandTextField.addKeyListener(control);
        frame.add(commandTextField, BorderLayout.SOUTH);
        
        frame.addWindowListener(control.getWindowsAdapter());
        
        frame.setSize(600, 400);
        frame.setVisible(true);
    }

    public void writeNewLine(String line) {
        consoleOut.println(line);
    }
    
    public String getCommand() {
        return commandTextField.getText();
    }
    
    public void setCommand(String command) {
        commandTextField.setText(command);
    }
    
// *************************************************************************************************
// INSTANCE MEMBERS
// *************************************************************************************************
    private byte[] oneByte;                                                    // array for write(int val);
    private Appender appender;                                                   // most recent action

    public ConsoleGUI(JTextArea txtara) {
        this(txtara, 1000);
    }

    public ConsoleGUI(JTextArea txtara, int maxlin) {
        if (maxlin < 1) {
            throw new IllegalArgumentException("TextAreaOutputStream maximum lines must be positive (value=" + maxlin + ")");
        }
        oneByte = new byte[1];
        appender = new Appender(txtara, maxlin);
    }

    /**
     * Clear the current console text area.
     */
    public synchronized void clear() {
        if (appender != null) {
            appender.clear();
        }
    }

    public synchronized void close() {
        appender = null;
    }

    public synchronized void flush() {
    }

    public synchronized void write(int val) {
        oneByte[0] = (byte) val;
        write(oneByte, 0, 1);
    }

    public synchronized void write(byte[] ba) {
        write(ba, 0, ba.length);
    }

    public synchronized void write(byte[] ba, int str, int len) {
        if (appender != null) {
            appender.append(bytesToString(ba, str, len));
        }
    }

    static private String bytesToString(byte[] ba, int str, int len) {
        try {
            return new String(ba, str, len, "UTF-8");
        } catch (UnsupportedEncodingException thr) {
            return new String(ba, str, len);
        } // all JVMs are required to support UTF-8
    }

// *************************************************************************************************
// STATIC MEMBERS
// *************************************************************************************************
    static class Appender
            implements Runnable {

        private final JTextArea textArea;
        private final int maxLines;                                                   // maximum lines allowed in text area
        private final LinkedList<Integer> lengths;                                                    // length of lines within text area
        private final List<String> values;                                                     // values waiting to be appended

        private int curLength;                                                  // length of current line
        private boolean clear;
        private boolean queue;

        Appender(JTextArea txtara, int maxlin) {
            textArea = txtara;
            maxLines = maxlin;
            lengths = new LinkedList<Integer>();
            values = new ArrayList<String>();

            curLength = 0;
            clear = false;
            queue = true;
        }

        synchronized void append(String val) {
            values.add(val);
            if (queue) {
                queue = false;
                EventQueue.invokeLater(this);
            }
        }

        synchronized void clear() {
            clear = true;
            curLength = 0;
            lengths.clear();
            values.clear();
            if (queue) {
                queue = false;
                EventQueue.invokeLater(this);
            }
        }

        // MUST BE THE ONLY METHOD THAT TOUCHES textArea!
        public synchronized void run() {
            if (clear) {
                textArea.setText("");
            }
            for (String val : values) {
                curLength += val.length();
                if (val.endsWith(EOL1) || val.endsWith(EOL2)) {
                    if (lengths.size() >= maxLines) {
                        textArea.replaceRange("", 0, lengths.removeFirst());
                    }
                    lengths.addLast(curLength);
                    curLength = 0;
                }
                textArea.append(val);
            }
            values.clear();
            clear = false;
            queue = true;
        }

        static private final String EOL1 = "\n";
        static private final String EOL2 = System.getProperty("line.separator", EOL1);
    }

}
/* END PUBLIC CLASS */
