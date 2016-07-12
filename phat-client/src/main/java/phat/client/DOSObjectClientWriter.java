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
import java.io.ObjectOutputStream;

/**
 *
 * @author pablo
 */
public class DOSObjectClientWriter {
    
    private final PHATClientConnection phatClientConnection;
    private ObjectOutputStream objectOutputStream;
    
    public DOSObjectClientWriter(PHATClientConnection phatClientConnection) {
        this.phatClientConnection = phatClientConnection;
    }
    
    public void connect() throws IOException {
        phatClientConnection.connect();
        objectOutputStream = new ObjectOutputStream(phatClientConnection.getSocket().getOutputStream());
    }
    
    public void write(Object object) throws IOException {
        objectOutputStream.writeObject(object);
    }
    
    public void close() throws IOException {
        objectOutputStream.close();
        phatClientConnection.close();
    }
}
