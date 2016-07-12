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
package phat.client;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import phat.mobile.servicemanager.client.ServiceManagerRemote;
import phat.mobile.servicemanager.services.Service;

/**
 *
 * @author pablo
 */
public class PHATClientConnection {

    boolean running = false;
    Socket socket;
    String deviceName;
    String serviceName;
    Service sensorService;
    ServiceManagerRemote smr;

    public PHATClientConnection(String serverIp, int serverPort, String deviceName, String serviceName) {
        this.deviceName = deviceName;
        this.serviceName = serviceName;

        smr = new ServiceManagerRemote(deviceName, serverIp, serverPort);
    }

    public PHATClientConnection(ServiceManagerRemote smr, String serviceName) {
        this.smr = smr;
        this.serviceName = serviceName;
    }
    
    public boolean connect() throws IOException {
        if (sensorService != null) {
            return true;
        }
        System.out.println("Asking for service " + serviceName);
        sensorService = smr.getService(null, serviceName);
        System.out.println("sensorService = " + sensorService);
        if (sensorService != null) {
            System.out.println("Connection to " + sensorService.getIp() + ":" + sensorService.getPort());
            if (socket == null) {
                socket = new Socket(sensorService.getIp(), sensorService.getPort());
            }
            return true;
        }
        return false;
    }

    public void close() {

        if (socket != null) {
            try {
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(PHATClientConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public Socket getSocket() {
        return socket;
    }

}
