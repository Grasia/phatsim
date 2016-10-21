/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.server.json_rpc;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;

/**
 *
 * @author pablo
 */
public interface ResponseSender {
    public void sendResponse(JSONRPC2Response respOut);
}
