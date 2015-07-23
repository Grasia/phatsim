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
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import phat.mobile.servicemanager.client.ServiceManagerRemote;
import phat.mobile.servicemanager.services.Service;

/**
 *
 * @author pablo
 */
public abstract class DOSClientTemplate implements Runnable {

    boolean running = false;
    Socket s;
    ObjectInputStream oin;
    private Thread thread;
    String deviceName;
    String serviceName;
    Service sensorService;
    ServiceManagerRemote smr;

    public DOSClientTemplate(String serverIp, int serverPort, String deviceName, String serviceName) {
        this.deviceName = deviceName;
        this.serviceName = serviceName;

        smr = new ServiceManagerRemote(deviceName, serverIp, serverPort);
    }

    private boolean connect() {
        if (sensorService != null) {
            return true;
        }
        System.out.println("Asking for service " + serviceName);
        sensorService = smr.getService(null, serviceName);
        System.out.println("sensorService = " + sensorService);
        if (sensorService != null) {
            System.out.println("Connection to " + sensorService.getIp() + ":" + sensorService.getPort());
            try {
                if (s == null) {
                    s = new Socket(sensorService.getIp(), sensorService.getPort());
                }
                if (oin == null) {
                    oin = new ObjectInputStream(s.getInputStream());
                }
                return true;
            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                s = null;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                oin = null;
            }
        }
        return false;
    }

    public void run() {
        while (running) {
            if (s == null || oin == null) {
                while (!connect()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(DOSClientTemplate.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

            System.out.println("Connected!!!");
            if (s != null && s.isConnected() && oin != null) {
                try {
                    Object o = oin.readObject();
                    if (o != null) {
                        newDataReceived(o);
                    }
                } catch (ClassNotFoundException e1) {
                    s = null;
                    oin = null;
                    break;
                } catch (IOException e1) {
                    s = null;
                    oin = null;
                    break;
                }
            } else {
                break;
            }
        }
    }

    public void start() {
        if (thread == null) {
            running = true;
            thread = new Thread(this);
            thread.start();
        }
    }

    public void stop() {
        running = false;
        if (thread != null) {
            thread.interrupt();
        }
    }

    protected abstract void newDataReceived(Object data);
}
