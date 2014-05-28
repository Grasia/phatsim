/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.server.accelerometer;

import phat.server.microphone.*;
import phat.sensors.Sensor;
import phat.sensors.SensorData;
import phat.sensors.SensorListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import phat.sensors.accelerometer.AccelerationData;
import phat.server.TCPSensorServer;
import sim.android.hardware.service.SimSensorEvent;

public class TCPAccelerometerServer implements SensorListener, TCPSensorServer {

    protected ServerSocket serverSocket;
    protected ObjectOutputStream oos;
    private String ip;
    private int port;
    private Thread serverThread;
    private Socket socket;
    private boolean endServer = false;

    public TCPAccelerometerServer(InetAddress ip, int port) throws IOException {
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
        if (sd instanceof AccelerationData) {
            AccelerationData accData = (AccelerationData) sd;
            if (socket != null && socket.isConnected() && oos != null) {
                try {
                    float[] data = new float[3];
                    data[0] = accData.getX();
                    data[1] = accData.getY();
                    data[2] = accData.getZ();
                    SimSensorEvent sse = new SimSensorEvent(
                            SimSensorEvent.TYPE_ACCELEROMETER,
                            data, 
                            0,
                            Math.round(1f/accData.getInterval()));
                    oos.writeObject(sse);
                    oos.flush();
                    oos.reset();
                } catch (IOException e1) {
                    Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, e1);
                    socket = null;
                    oos = null;
                }
            }
        }
    }

    @Override
    public void cleanUp() {
        stop();
    }
}
