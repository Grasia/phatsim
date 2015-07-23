package phat;

import java.rmi.Remote;
import java.rmi.RemoteException;

import phat.world.PHATCalendar;

public interface RemotePHATInterface extends Remote {
	 public void resumePHAT()  throws RemoteException;
	 public void pausePHAT()  throws RemoteException;
	 public PHATCalendar getSimTime()  throws RemoteException;
	 public long getElapsedSimTimeSeconds()  throws RemoteException;
}
