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
package phat.server.json_rpc;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Error;
import com.thetransactioncompany.jsonrpc2.JSONRPC2ParseException;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import phat.commands.PHATCommand;

/**
 *
 * @author pablo
 */
public class PHATSocketDispacherThread extends Thread implements ResponseSender {

    JsonRpcAppState jsonRpcAppState;
    Socket socket;
    PrintWriter out;

    boolean running = true;

    Map<String, JsonRpcAsycPHATCommandRequest> requests = new HashMap<>();

    public PHATSocketDispacherThread(JsonRpcAppState jsonRpcAppState, Socket socket) {
        super("PHATSocketDispacherThread");
        this.jsonRpcAppState = jsonRpcAppState;
        this.socket = socket;
    }

    private PrintWriter getOutputStream() throws IOException {
        if (out == null) {
            out = new PrintWriter(socket.getOutputStream(), true);
        }
        return out;
    }

    @Override
    public void run() {
        try (
                PrintWriter out = getOutputStream();
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                socket.getInputStream()/*, "UTF-8"*/));) {
            String inputLine, outputLine;

            while (running && socket.isConnected()) {
                String jsonString = getJSONObject(in);

                if (jsonString == null) {
                    running = false;
                    in.close();
                    socket.close();
                    continue;
                }
                // Parse request string
                JSONRPC2Request reqIn = null;

                try {
                    reqIn = JSONRPC2Request.parse(jsonString);

                    // How to extract the request data
                    System.out.println("Parsed request with properties :");
                    System.out.println("\tmethod     : " + reqIn.getMethod());
                    System.out.println("\tparameters : " + reqIn.getNamedParams());
                    System.out.println("\tid         : " + reqIn.getID() + "\n\n");

                    process(reqIn);
                } catch (JSONRPC2ParseException e) {
                    System.out.println(e.getMessage());
                    // Handle exception...
                }
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void process(JSONRPC2Request reqIn) {
        System.out.println("jsonRpcAppState = " + jsonRpcAppState);
        System.out.println("reqIn = " + reqIn);
        System.out.println("commandFactory" + jsonRpcAppState.getCommandFactory());
        if (jsonRpcAppState.getCommandFactory().isHandable(reqIn)) {
            System.out.println("phatCommandFactory...");
            PHATCommand command = jsonRpcAppState.getCommandFactory().createCommand(reqIn);
            if (command != null) {
                System.out.println("New Command =====> "+command);
                JsonRpcAsycPHATCommandRequest aReq = new JsonRpcAsycPHATCommandRequest(this, reqIn, command);
                command.setListener(aReq);
                add(String.valueOf(reqIn.getID()), aReq);

                jsonRpcAppState.runCommand(command);
            } else {
                sendResponse(new JSONRPC2Response("Method \""+reqIn.getMethod()+"\" not found!", String.valueOf(reqIn.getID())));
            }
        }
    }

    public void add(String idReq, JsonRpcAsycPHATCommandRequest req) {
        requests.put(idReq, req);
    }

    public void remove(String idReq) {
        requests.remove(idReq);
    }

    final Object mutex = new Object();

    @Override
    public void sendResponse(JSONRPC2Response respOut) {
        String jsonResponse = respOut.toJSONString();
        System.out.println("beforeMutext");
        synchronized (mutex) {
            System.out.println("write1");
            if (out != null) {
                System.out.println("write2 = " + jsonResponse);
                out.println(jsonResponse);
                System.out.println("write finished!");
            }
            remove(String.valueOf(getId()));
        }
    }

    private String getJSONObject(BufferedReader in) throws IOException {
        String inputLine, result = null;
        System.out.println("ReadingLine...");
        inputLine = in.readLine();
        System.out.println("Line=" + inputLine);
        return inputLine;
    }
}
