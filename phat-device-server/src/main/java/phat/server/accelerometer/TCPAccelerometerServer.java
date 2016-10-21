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
package phat.server.accelerometer;

import phat.server.microphone.*;
import phat.sensors.Sensor;
import phat.sensors.SensorData;
import phat.sensors.SensorListener;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import phat.sensors.accelerometer.AccelerationData;
import phat.sensors.accelerometer.AccelerometerControl;
import phat.server.TCPSensorServer;
import sim.android.hardware.service.SimSensorEvent;

public class TCPAccelerometerServer implements SensorListener, TCPSensorServer {

	protected ServerSocket serverSocket;
	protected Vector<OutputStream> oos=new  Vector<OutputStream>();
	private InetAddress ip;
	private int port;
	private Thread serverThread;
	private Socket socket;
	private boolean endServer = false;
	AccelerometerControl accSensor;

	public TCPAccelerometerServer(InetAddress ip, int port, AccelerometerControl accSensor) throws IOException {
		this.ip = ip;		
		this.port = port;
		this.accSensor = accSensor;
		serverSocket = new ServerSocket(port, 0, ip);
		System.err.println(ip);
	}

	@Override
	public String getIp() {
		return ip.getHostAddress();
	}

	@Override
	public int getPort() {
		return port;
	}

	@Override
	public synchronized void start() {
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
							if(!endServer) {
								Logger.getLogger(TCPAudioMicroServer.class.getName()).log(Level.SEVERE, null, ex);
							}
						}
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
		accSensor.add(this);
		try {
			this.oos.add(socket.getOutputStream());
		} catch (IOException ex) {
			Logger.getLogger(TCPAudioMicroServer.class.getName()).log(Level.SEVERE, null, ex);
			oos = null;
			this.socket = null;
		}
	}

	@Override
	public synchronized void stop() {
		accSensor.remove(this);
		try {
			endServer = true;
			serverSocket.close();

			if (oos != null) {

				for (OutputStream os: oos){
					try {
						os.close();
					} catch (IOException ioe){

					}
				}
				oos.clear();
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
	public synchronized void update(Sensor source, SensorData sd) {
		if (socket == null || oos.isEmpty()) {
			return;
		}
		if (sd instanceof AccelerationData) {
			AccelerationData accData = (AccelerationData) sd;
			if (socket != null && socket.isConnected() && oos != null) {
				float[] data = new float[3];
				data[0] = accData.getX();
				data[1] = accData.getY();
				data[2] = accData.getZ();
				SimSensorEvent sse = new SimSensorEvent(
						SimSensorEvent.TYPE_ACCELEROMETER,
						data,
						0,
						Math.round(1f / accData.getInterval()));
				
				Vector<OutputStream> toRemove=new  Vector<OutputStream>();
				for (OutputStream os:oos) {
					try {
						os.write((sse.toString()+"\n").getBytes());
						os.flush();
						
					} catch (Exception e){
						toRemove.add(os);
					}
				}
				oos.removeAll(toRemove);
			}
		}
	}

	@Override
	public void cleanUp() {
		stop();
	}
}
