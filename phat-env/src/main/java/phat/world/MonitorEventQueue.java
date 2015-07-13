package phat.world;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Vector;

import com.jme3.math.Vector3f;

public interface MonitorEventQueue extends Remote {
	public final  String DefaultName = "simulation";
	void notifyEvent(RemotePHATEvent event)  throws RemoteException;;
	Vector<RemotePHATEvent> retrieveAllEvents()  throws RemoteException;;
	long getSimTime()  throws RemoteException;


}
