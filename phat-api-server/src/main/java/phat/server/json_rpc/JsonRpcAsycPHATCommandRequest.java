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
package phat.server.json_rpc;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;

/**
 *
 * @author pablo
 */
public class JsonRpcAsycPHATCommandRequest implements PHATCommandListener{
    ResponseSender sender;
    JSONRPC2Request request;
    PHATCommand command;

    public JsonRpcAsycPHATCommandRequest(ResponseSender dispacher, JSONRPC2Request request, PHATCommand command) {
        this.sender = dispacher;
        this.request = request;
        this.command = command;
    }

    @Override
    public void commandStateChanged(PHATCommand command) {
        System.out.println("commandStateChanged ===== "+command);
        if(command.getState().equals(PHATCommand.State.Success) ||
            command.getState().equals(PHATCommand.State.Fail) ||
                command.getState().equals(PHATCommand.State.Interrupted)) {
            Object result = command.getResult();
            JSONRPC2Response respOut;
            if(result != null) {
                respOut = new JSONRPC2Response(result, request.getID());
            } else {
                respOut = new JSONRPC2Response(command.getState().name(), request.getID());
            }
            
            sender.sendResponse(respOut);
        }
    }
    
}
