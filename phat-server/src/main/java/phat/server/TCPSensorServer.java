/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.server;

/**
 *
 * @author Pablo
 */
public interface TCPSensorServer {
    public int getPort();
    public String getIp();
    public void start();
    public void stop();
}
