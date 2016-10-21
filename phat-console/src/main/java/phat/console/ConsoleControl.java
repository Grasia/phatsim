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

import com.thetransactioncompany.jsonrpc2.JSONRPC2ParseException;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pablo
 */
public class ConsoleControl extends KeyAdapter {

    private Map<String, JSONRPC2Request> reqHistory = new HashMap<>();

    private String ip = "192.168.0.250";
    private int port = 12345;
    Properties props = new Properties();
    private Socket socket;

    private ReadResults readResults;
    private CommandSender commandSender;
    private final CommandParser commandParser;
    private final ResponseParser responseParser;
    private final CommandHistory commandHistory;

    ConsoleGUI gui;

    public ConsoleControl() {
        commandParser = new CommandParserImpl();
        responseParser = new ResponseParserImpl();
        commandHistory = new CommandHistory();
    }

    public void run() {
        if (readyToConnect()) {
            try {
                socket = new Socket(ip, port);
                gui.writeNewLine("Connedted!");
                new Thread(readResults = new ReadResults()).start();
                new Thread(commandSender = new CommandSender()).start();
            } catch (IOException ex) {
                Logger.getLogger(ConsoleControl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public boolean readyToConnect() {
        File file = new File("config.properties");
        if (!file.exists()) {
            gui.writeNewLine("\"config.properties\" is not found!");
            gui.writeNewLine("ip=<ip>");
            gui.writeNewLine("port=44123");

            return false;
        } else {
            try (InputStream in = new FileInputStream(file)) {
                props.load(in);

                ip = props.getProperty("ip");
                if (ip == null) {
                    gui.writeNewLine("ip=<ip> property has not been set in \"config.properties\" file");
                    return false;
                }
                String portString = props.getProperty("port");
                if (portString == null) {
                    gui.writeNewLine("port=44123 property has not been set in \"config.properties\" file");
                    return false;
                }
                port = Integer.valueOf(portString);
            } catch (IOException ex) {
                Logger.getLogger(ConsoleControl.class.getName()).log(Level.SEVERE, null, ex);
                gui.writeNewLine("\"configi.properties\" is not found!");
                return false;
            }
        }
        return true;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ENTER:
                String newCommand = gui.getCommand();
                commandHistory.addCommand(newCommand);

                JSONRPC2Request req = commandParser.getJsonRequest(newCommand);
                if (req != null) {
                    reqHistory.put(String.valueOf(req.getID()), req);
                    gui.writeNewLine("");
                    commandSender.notifySend(req.toJSONString());
                }
                gui.setCommand("");
                break;
            case KeyEvent.VK_UP:
                gui.setCommand(commandHistory.previousCommand());
                break;
            case KeyEvent.VK_DOWN:
                gui.setCommand(commandHistory.nextCommand());
                break;
        }
    }

    private void setGui(ConsoleGUI gui) {
        this.gui = gui;
    }

    public java.awt.event.WindowAdapter getWindowsAdapter() {
        return new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                commandSender.listening = false;
                readResults.listening = false;
                
                try {
                    socket.close();
                } catch (IOException ex) {
                    Logger.getLogger(ConsoleControl.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                System.exit(0);
            }
        };
    }

    class ReadResults implements Runnable {

        boolean listening = true;

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(
                            new InputStreamReader(socket.getInputStream()));) {
                while (listening) {
                    System.out.println("ReadResponses...");
                    String line = in.readLine();
                    System.out.println(line);
                    if (line == null) {
                        listening = false;
                        continue;
                    }
                    try {
                        JSONRPC2Response response = JSONRPC2Response.parse(line);
                        String id = String.valueOf(response.getID());
                        JSONRPC2Request request = reqHistory.get(id);

                        gui.writeNewLine(responseParser.getFormattedString(request, response));
                    } catch (JSONRPC2ParseException ex) {

                    }
                }
            } catch (IOException e) {
                System.err.println("Couldn't get I/O for the connection to "
                        + ip);
                System.exit(1);
            }
        }
    }

    class CommandSender implements Runnable {

        boolean listening = true;
        boolean send = false;
        String command;

        @Override
        public void run() {
            try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true);) {
                while (listening) {
                    send(out);
                }
            } catch (IOException e) {
                System.err.println("Couldn't get I/O for the connection to "
                        + ip);
                System.exit(1);
            }
        }

        public synchronized void send(PrintWriter out) {
            System.out.println("send()...");
            while (!send) {
                try {
                    wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(ConsoleControl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            out.println(command);
            send = false;
            System.out.println("...send()");
        }

        public synchronized void notifySend(String command) {
            System.out.println("notifySend..." + command);
            send = true;
            this.command = command;
            notifyAll();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ConsoleControl control = new ConsoleControl();
        ConsoleGUI gui = new ConsoleGUI(control);
        control.setGui(gui);
        control.run();
    }
}
