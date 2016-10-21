/*
 * Copyright (C) 2016 Pablo Campillo-Sanchez <pabcampi@ucm.es>
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

import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author pablo
 */
public class CommandParserImpl implements CommandParser {

    int idRequest = 0;

    @Override
    public JSONRPC2Request getJsonRequest(String commandLine) {
        JSONRPC2Request req;

        String[] words = commandLine.split(" ");
        if (words.length == 0) {
            // ERROR: WRITE SOMETHING
            return null;
        }

        String command = words[0];

        if (words.length > 1) {
            if (commandLine.contains("=")) {
                Map<String, Object> namedParams = new HashMap<>();
                for (int i = 1; i < words.length; i++) {
                    String[] pair = words[i].split("=");
                    namedParams.put(pair[0], pair[1]);
                }
                req = new JSONRPC2Request(command, namedParams, String.valueOf(idRequest));
            } else {
                List<Object> positionalParams = new ArrayList<>();
                for(int i = 1; i < words.length; i++) {
                    positionalParams.add(words[i]);
                }
                req = new JSONRPC2Request(command, positionalParams, String.valueOf(idRequest));
            }
        } else {
            req = new JSONRPC2Request(command, String.valueOf(idRequest));
        }
        idRequest++;
        return req;
    }
}
