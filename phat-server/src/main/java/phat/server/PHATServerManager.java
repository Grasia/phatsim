/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.server;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import phat.mobile.servicemanager.CommonVars;
import phat.mobile.servicemanager.server.ServiceManagerServer;
import phat.mobile.servicemanager.services.Service;
import phat.mobile.servicemanager.services.ServiceImpl;
import phat.sensors.accelerometer.AccelerometerControl;
import phat.sensors.camera.CameraSensor;
import phat.server.microphone.TCPAudioMicroServer;
import phat.sensors.microphone.MicrophoneControl;
import phat.server.accelerometer.TCPAccelerometerServer;
import phat.server.camera.TCPCameraSensorServer;

/**
 *
 * @author Pablo
 */
public class PHATServerManager {

    InetAddress inetAddress = getAddress();
    Map<String, List<TCPSensorServer>> tcpSensorServers = new HashMap<String, List<TCPSensorServer>>();

    public TCPAudioMicroServer createAndStartAudioMicroServer(String id, MicrophoneControl mc) {
        TCPAudioMicroServer ams = null;
        int port = ServiceManagerServer.getInstance().getNextPort();
        try {
            System.out.println("IP:PORT -> " + inetAddress + ":" + port);
            ams = new TCPAudioMicroServer(inetAddress, port);
        } catch (IOException ex) {
            Logger.getLogger(PHATServerManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

        mc.add(ams);

        registerService(id, ams, Service.MICROPHONE);

        ams.start();

        add(id, ams);

        return ams;
    }
    
    public TCPCameraSensorServer createAndStartCameraServer(String id, CameraSensor cameraSensor) {
        TCPCameraSensorServer ams = null;
        int port = ServiceManagerServer.getInstance().getNextPort();
        try {
            System.out.println("IP:PORT -> " + inetAddress + ":" + port);
            ams = new TCPCameraSensorServer(inetAddress, port);
        } catch (IOException ex) {
            Logger.getLogger(PHATServerManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

        cameraSensor.add(ams);

        registerService(id, ams, Service.CAMERA);

        ams.start();

        add(id, ams);

        return ams;
    }

    public TCPAccelerometerServer createAndStartAccelerometerServer(String id, AccelerometerControl accSensor) {
        TCPAccelerometerServer ams = null;
        int port = ServiceManagerServer.getInstance().getNextPort();
        try {
            System.out.println("IP:PORT -> " + inetAddress + ":" + port);
            ams = new TCPAccelerometerServer(inetAddress, port);
        } catch (IOException ex) {
            Logger.getLogger(PHATServerManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

        accSensor.add(ams);

        registerService(id, ams, Service.ACCELEROMETER);

        ams.start();

        add(id, ams);

        return ams;
    }
    
    public void stop() {
        for (List<TCPSensorServer> list : tcpSensorServers.values()) {
            for (TCPSensorServer server : list) {
                server.stop();
            }
        }
        ServiceManagerServer.getInstance().stop();
    }

    private void add(String id, TCPSensorServer server) {
        List<TCPSensorServer> list = tcpSensorServers.get(id);
        if (list == null) {
            list = new ArrayList<TCPSensorServer>();
            tcpSensorServers.put(id, list);
        }
        list.add(server);
    }

    private void registerService(String serviceSetId, TCPSensorServer server, String type) {
        Service service = new ServiceImpl(type, server.getIp(), server.getPort());
        System.out.println("New Service: " + service);
        ServiceManagerServer sms = ServiceManagerServer.getInstance();
        sms.getServiceManager().registerService(serviceSetId, service);
    }

    public String getIP() {
        return inetAddress.getHostAddress();
    }

    public int getPort() {
        return CommonVars.SERVICE_MANAGER_SERVER_PORT;
    }

    private InetAddress getAddress() {
        try {
            for (Enumeration e = NetworkInterface.getNetworkInterfaces();
                    e.hasMoreElements();) {

                NetworkInterface ni = (NetworkInterface) e.nextElement();
                if (ni.getName().contains("eth") || ni.getName().contains("wlan"))  {
                    for (Enumeration ee = ni.getInetAddresses(); ee.hasMoreElements();) {
                        InetAddress ip = (InetAddress) ee.nextElement();
                        if(ip instanceof Inet4Address && ip.getAddress() != null) {
                            return ip;
                        }
                        //System.out.println("Ip's: " + ip.getHostAddress());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error");
        }
        return null;
    }
}
