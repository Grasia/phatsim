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
package phat.server.speaker;

import com.jme3.app.Application;
import com.jme3.audio.AudioBuffer;
import com.jme3.audio.AudioKey;
import com.jme3.audio.AudioNode;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.util.BufferUtils;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import phat.server.ServerAppState;
import phat.server.TCPSensorServer;
import phat.server.commands.PHATServerCommand;
import phat.util.SpatialFactory;
import phat.util.controls.RemoveSpatialTimerControl;
import sim.android.media.service.AudioStreamDataPacket;

public class TCPAudioSpeakerServer implements TCPSensorServer {

    protected ServerSocket serverSocket;
    protected AudioInputStream audioInputStream;
    private String ip;
    private int port;
    private Thread serverThread;
    private Socket socket;
    private boolean endServer = false;
    private Node device;
    private AudioNode audioNode;
    private AudioBuffer audioBuffer;
    private static final float FRECUENCY = 16000f;
    private static final float BITS_PER_SAMPLE = 16f;
    private final ServerAppState serverAppState;

    byte[] buffer;

    private ByteBuffer readToBuffer(InputStream inputStream) throws IOException, ClassNotFoundException, UnsupportedAudioFileException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        System.out.println("Available = " + audioInputStream.available());

        int lastLenght = 0;
        int read;
        int bufferSize = 1024;
        byte[] buf = new byte[bufferSize];
        while ((read = audioInputStream.read(buf, 0, bufferSize)) != -1) {
            baos.write(buf, 0, read);
        }
        baos.flush();

        byte[] audioData = baos.toByteArray();
        System.out.println("total received data = " + audioData.length);
        ByteBuffer data = BufferUtils.createByteBuffer(audioData.length);
        data.put(audioData, 0, audioData.length).flip();

        baos.close();

        return data;
    }

    public TCPAudioSpeakerServer(ServerAppState serverAppState, InetAddress ip, int port, Node device) throws IOException {
        this.serverAppState = serverAppState;
        this.ip = ip.getHostAddress();
        this.port = port;
        this.device = device;
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
                        try {
                            Socket socket = serverSocket.accept();
                            System.out.println("Nuevo Cliente: " + socket);
                            upClient(socket);
                        } catch (SocketException ex) {
                            if (!endServer) {
                                Logger.getLogger(TCPAudioSpeakerServer.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(TCPAudioSpeakerServer.class.getName()).log(Level.SEVERE, null, ex);
                    socket = null;
                    audioInputStream = null;
                }
            }
        });
        serverThread.start();
    }

    private synchronized void upClient(Socket socket) {
        this.socket = socket;
        try {
            BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());

            System.out.println("BufferedInputStream.avaliable = " + bis.available());

            this.audioInputStream = AudioSystem.getAudioInputStream(bis);
            if (audioNode == null) {
                audioBuffer = new AudioBuffer();
                audioNode = new AudioNode(audioBuffer, new AudioKey("Speaker-" + device.getName(), true, true));
                audioNode.setName("Speaker-" + device.getName());
            } else {
                audioBuffer.resetObject();
            }
            audioBuffer.setupFormat(1, 16, 16000);
            audioBuffer.updateData(readToBuffer(audioInputStream));
            serverAppState.runCommand(new AddSpatialCommand(audioNode, device));
        } catch (IOException ex) {
            Logger.getLogger(TCPAudioSpeakerServer.class.getName()).log(Level.SEVERE, null, ex);
            this.audioInputStream = null;
            this.socket = null;
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(TCPAudioSpeakerServer.class.getName()).log(Level.SEVERE, null, ex);
            this.audioInputStream = null;
            this.socket = null;
        } catch (UnsupportedAudioFileException ex) {
            Logger.getLogger(TCPAudioSpeakerServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            System.out.println("Close objectInputStream and socket!");
            if (audioInputStream != null) {
                audioInputStream.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(TCPAudioSpeakerServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    Node sphereNode;

    class AddSpatialCommand extends PHATServerCommand {

        private final AudioNode audioNode;
        private final Node root;

        public AddSpatialCommand(AudioNode audioNode, Node root) {
            super(null);
            this.audioNode = audioNode;
            this.root = root;
        }

        @Override
        public void runCommand(Application app) {
            if (audioNode.getParent() == null) {
                System.out.println("attach " + audioNode.getName() + " to " + root);
                root.attachChild(audioNode);
            }

            if (sphereNode == null) {
                sphereNode = new Node();
                Geometry geo = SpatialFactory.createSphere(0.2f, ColorRGBA.Blue.clone().set(
                        ColorRGBA.Blue.r, ColorRGBA.Blue.r, ColorRGBA.Blue.r, 0.5f), true);
                sphereNode.attachChild(geo);
                sphereNode.addControl(new RemoveSpatialTimerControl(2f));
            }

            RemoveSpatialTimerControl rstc = sphereNode.getControl(RemoveSpatialTimerControl.class);
            rstc.reset();
            root.attachChild(sphereNode);

            System.out.println("Play audio!!");
            //audioBuffer.resetObject();
            audioNode.setVolume(0.5f);
            audioNode.play();
        }

        @Override
        public void interruptCommand(Application app) {
        }
    }

    @Override
    public void stop() {
        try {
            endServer = true;
            serverSocket.close();

            if (audioInputStream != null) {
                audioInputStream.close();
                audioInputStream = null;
            }
            if (socket != null) {
                socket.close();
                socket = null;
            }
        } catch (IOException e1) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, e1);
        }
    }
}
