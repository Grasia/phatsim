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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pablo
 */
public class DOSObjectClientReader implements Runnable {

    private boolean running = false;
    private ObjectInputStream objectInputStream;
    private final NewObjectReceivedListener newObjectReceivedListener;
    private final PHATClientConnection phatClientConnection;
    private Thread thread;
    
    public DOSObjectClientReader(PHATClientConnection phatClientConnection, NewObjectReceivedListener newObjectReceivedListener) {
        this.phatClientConnection = phatClientConnection;
        this.newObjectReceivedListener = newObjectReceivedListener;
    }

    public void connect() throws IOException {
        phatClientConnection.connect();
        objectInputStream = new ObjectInputStream(phatClientConnection.getSocket().getInputStream());
    }

    @Override
    public void run() {
        while (running) {
            Object o;
            try {
                o = objectInputStream.readObject();
                this.newObjectReceivedListener.newObjectReceived(o);
            } catch (IOException ex) {
                Logger.getLogger(DOSObjectClientReader.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(DOSObjectClientReader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        try {
            close();
        } catch (IOException ex) {
            Logger.getLogger(DOSObjectClientReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void close() throws IOException {
        objectInputStream.close();
        phatClientConnection.close();
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
}
