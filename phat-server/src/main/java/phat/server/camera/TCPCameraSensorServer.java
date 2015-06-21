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
package phat.server.camera;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import phat.sensors.Sensor;
import phat.sensors.SensorData;
import phat.sensors.SensorListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import phat.sensors.camera.CameraSensor;
import phat.sensors.camera.CameraSensorData;
import phat.server.TCPSensorServer;
import phat.util.PHATImageUtils;
import sim.android.hardware.service.CameraImageCapture;

public class TCPCameraSensorServer implements SensorListener, TCPSensorServer {

    protected ServerSocket serverSocket;
    protected ObjectOutputStream oos;
    private String ip;
    private int port;
    private Thread serverThread;
    private Socket socket;
    private boolean endServer = false;
    int imgSending = 0;
    static final int IMG_BUFFER = 20;
    final Object mutex0 = new Object();
    final Object mutex1 = new Object();
    final Object mutex2 = new Object();
    CameraSensor cameraSensor;

    public TCPCameraSensorServer(InetAddress ip, int port, CameraSensor cameraSensor) throws IOException {
        this.ip = ip.getHostAddress();
        this.port = port;
        this.cameraSensor = cameraSensor;
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
                        socket.setSendBufferSize(800 * 480 * 4 * IMG_BUFFER);
                        System.out.println("*Nuevo Cliente: " + socket);
                        upClient(socket);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(TCPCameraSensorServer.class.getName()).log(Level.SEVERE, null, ex);
                    socket = null;
                    oos = null;
                }
            }
        });
        serverThread.start();
    }

    private synchronized void upClient(Socket socket) {
        this.socket = socket;
        cameraSensor.add(this);
        try {
            this.oos = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(TCPCameraSensorServer.class.getName()).log(Level.SEVERE, null, ex);
            oos = null;
            this.socket = null;
        }
    }

    @Override
    public void stop() {
        cameraSensor.remove(this);
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
    /*byte[] dataSend = new byte[800 * 480 * 4];

     private void copy(BufferedImage image) {
     int height = image.getWidth();
     int width = image.getWidth();
        
     for (int y = 0; y < height; y++) {
     for (int x = 0; x < width; x++) {
     int inPtr = (y * width + x) * 4;
     dataSend[inPtr + 0] = 0;
     dataSend[inPtr + 3] = (byte) 255;
     dataSend[inPtr + 2] = 0;
     dataSend[inPtr + 1] = 0;
     }
     }
     }*/
    float rate = 1f;
    float lastTime = 0;

    private boolean isTimeToSend(CameraSensorData csd) {
        lastTime += csd.getFps();
        if (lastTime > rate) {
            //counter = time - rate;
            lastTime = 0f;
            return true;
        }
        return false;
    }
    int counter = 0;

    private void saveFile(byte[] data) {
        FileOutputStream fos;
        try {
            fos = new FileOutputStream("capture-" + (counter++) + ".bmp");
            fos.write(data);
            fos.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TCPCameraSensorServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TCPCameraSensorServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void update(Sensor source, SensorData sd) {
        if (socket == null || oos == null) {
            return;
        }
        if (source instanceof CameraSensor && sd instanceof CameraSensorData) {
            CameraSensor cs = (CameraSensor) source;
            CameraSensorData csd = (CameraSensorData) sd;

            synchronized(mutex0) {
                if(imgSending >= 2) {
                    return;
                }
                imgSending++;
            }
            //data = PHATImageUtils.convertABGRToYUV220SP(csd.getImage());
            // este metodo es lento
            //data = PHATImageUtils.bufferedImageToFormat(csd.getImage(), "PNG");
            //byte[] buf = PHATImageUtils.bufferedImageToBMPByteArray(csd.getImage(), outBuffers.get(index));
            //byte[] buf = PHATImageUtils.convertABGRToRGB565(csd.getImage(), outBuffers.get(index));
            byte[] buf;
            CameraImageCapture cic;
            synchronized (mutex1) {
                buf = PHATImageUtils.bufferedImageToFormat(csd.getImage(), "PNG");
                cic = new CameraImageCapture(
                        0L, buf, csd.getWidth(), csd.getHeigh(), csd.getImage().getType());
            }
            synchronized (mutex2) {
                send(cic);
            }
            synchronized(mutex0) {
                imgSending--;
            }
            /*if (socket != null && socket.isConnected() && oos != null) {
             try {
             oos.writeObject(cic);
             oos.flush();
             oos.reset();
             } catch (IOException e1) {
             Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, e1);
             socket = null;
             oos = null;
             }
             }*/
            /*} catch (IOException ex) {
             Logger.getLogger(TCPCameraSensorServer.class.getName()).log(Level.SEVERE, null, ex);
             }*/
        }
    }

    private void send(CameraImageCapture cic) {
        try {
            oos.writeObject(cic);
            oos.flush();
            oos.reset();
        } catch (IOException e1) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, e1);
            socket = null;
            oos = null;
        }
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    @Override
    public void cleanUp() {
        stop();
    }
}
