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
package phat.server.actuators;

import phat.server.microphone.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import phat.devices.actuators.VibratorActuator;

import phat.server.TCPSensorServer;
import sim.android.hardware.service.vibrator.SimVibrateAction;
import sim.android.hardware.service.vibrator.SimVibrateCancelAction;
import sim.android.hardware.service.vibrator.SimVibratePatternAction;

public class UDPVibratorServer implements TCPSensorServer {

    protected DatagramSocket serverSocket;
    private Thread serverThread;
    private boolean endServer = false;
    VibratorActuator vibrator;

    public UDPVibratorServer(InetAddress ip, int port, VibratorActuator vibrator) throws IOException {
        this.vibrator = vibrator;
        serverSocket = new DatagramSocket(port, ip);
    }

    @Override
    public String getIp() {
        System.out.println("ServerSocket.getIp() = "+serverSocket.getLocalAddress());
        return serverSocket.getLocalAddress().getHostAddress();
    }

    @Override
    public int getPort() {
        return serverSocket.getLocalPort();
    }
    
    byte[] buffer = new byte[200];

    @Override
    public synchronized void start() {
        serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (!endServer) {
                        try {
                            DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                            serverSocket.receive(request);
                            proccessRequest(request);
                        } catch (SocketException ex) {
                            if (!endServer) {
                                Logger.getLogger(TCPAudioMicroServer.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                    serverSocket.close();
                } catch (IOException ex) {
                    Logger.getLogger(TCPAudioMicroServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        serverThread.start();
    }

    private synchronized void proccessRequest(DatagramPacket request) {
        String action = new String(request.getData());
        action = action.trim();
        System.out.println("\n\nNew Request: ["+action+"]");
        System.out.println("dataLenght = "+action.length());
        System.out.println("\n\n");
        if (SimVibrateAction.isAnSpec(action)) {
            SimVibrateAction sva = SimVibrateAction.fromString(action);
            vibrator.vibrate(sva.getMilliseconds());
        } else if (SimVibratePatternAction.isAnSpec(action)) {
            SimVibratePatternAction sva = SimVibratePatternAction.fromString(action);
            vibrator.vibrate(sva.getPattern(), sva.getRepeat());
        } else if (SimVibrateCancelAction.isAnSpec(action)) {
            SimVibrateCancelAction sva = SimVibrateCancelAction.fromString(action);
            vibrator.cancel();
        }
    }

    @Override
    public synchronized void stop() {
        endServer = true;
    }
}
