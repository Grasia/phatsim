/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.server.microphone;

import phat.sensors.Sensor;
import phat.sensors.SensorData;
import phat.sensors.SensorListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import phat.mobile.servicemanager.server.ServiceManagerServer;
import phat.sensors.microphone.MicrophoneData;
import phat.server.TCPSensorServer;
import sim.android.media.service.AudioStreamDataPacket;

public class TCPAudioMicroServer implements SensorListener, TCPSensorServer {

    protected ServerSocket serverSocket;
    protected ObjectOutputStream oos;
    private String ip;
    private int port;
    private Thread serverThread;
    private Socket socket;
    private boolean endServer = false;

    public TCPAudioMicroServer(InetAddress ip, int port) throws IOException {
        this.ip = ip.getHostAddress();
        this.port = port;
        serverSocket = new ServerSocket(port, 0, ip);        
    }

    @Override
    public String getIp() {
        return ip;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public void start() {
        serverThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    while (!endServer) {
                        Socket socket = serverSocket.accept();
                        System.out.println("Nuevo Cliente: "+socket);
                        upClient(socket);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(TCPAudioMicroServer.class.getName()).log(Level.SEVERE, null, ex);
                    socket = null;
                    oos = null;
                }
            }
        });
        serverThread.start();
    }

    private synchronized void upClient(Socket socket) {
        this.socket = socket;
        try {
            this.oos = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(TCPAudioMicroServer.class.getName()).log(Level.SEVERE, null, ex);
            oos = null;
            this.socket = null;
        }
    }

    public boolean send(byte[] buffer, int offset, int numSamples, int numReadings) {
        if (socket != null && socket.isConnected() && oos != null) {
            try {
                int totalSize = numSamples * numReadings;
                byte[] data = new byte[totalSize];
                System.arraycopy(buffer, offset, data, 0, totalSize);
                AudioStreamDataPacket asdp = new AudioStreamDataPacket(data, numSamples, numReadings);
                oos.writeObject(asdp);
                oos.flush();
                oos.reset();
            } catch (IOException e1) {
                Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, e1);
                socket = null;
                oos = null;
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public void stop() {
        try {
            endServer = true;
            serverThread.interrupt();
            
            if (oos != null) {
                oos.close();
                oos = null;
            }
            if (socket != null) {
                socket.close();
                socket = null;
            }
        } catch (IOException e1) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, e1);
        }
    }

    @Override
    public void update(Sensor source, SensorData sd) {
        if (socket == null || oos == null) {
            return;
        }
        if (sd instanceof MicrophoneData) {
            MicrophoneData microphoneData = (MicrophoneData) sd;
            if (socket != null && socket.isConnected() && oos != null) {
                try {
                    int totalSize = microphoneData.getData().length;
                    int numSamples = microphoneData.getAudioFormat().getSampleSizeInBits();
                    byte[] data = new byte[totalSize];
                    System.arraycopy(microphoneData.getData(), 0, data, 0, totalSize);
                    AudioStreamDataPacket asdp = new AudioStreamDataPacket(data, numSamples, totalSize/numSamples);
                    oos.writeObject(asdp);
                    oos.flush();
                    oos.reset();
                } catch (IOException e1) {
                    Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, e1);
                    socket = null;
                    oos = null;
                    return;
                }
            }
        }
    }

    @Override
    public void cleanUp() {
        stop();
    }
}
