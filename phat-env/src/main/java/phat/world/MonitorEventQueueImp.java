package phat.world;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Vector;

public class MonitorEventQueueImp implements MonitorEventQueue {
	
	java.util.concurrent.ConcurrentLinkedQueue<RemotePHATEvent> queue=new java.util.concurrent.ConcurrentLinkedQueue<RemotePHATEvent>();
	Registry registry =null;
	
	public void startServer(String name) throws RemoteException, AlreadyBoundException, NotBoundException{
		
		int port=60200; 
		System.setProperty("java.rmi.server.useCodebaseOnly","false");
		if (System.getProperty("phat.monitorport")!=null){
			port=Integer.parseInt(System.getProperty("phat.monitorport"));
		}
		if (registry==null){
			try {
				System.err.println("otro");
				registry = LocateRegistry.getRegistry(port);
				registry.list();// to force the connection and ensure there is something at the other side
				// getRegistry is not failing when resolving the registry
				System.err.println("terminado");
			} catch (Exception  e) {
				System.err.println("creado");
				registry = java.rmi.registry.LocateRegistry.createRegistry(port); // Creates and exports a Registry instance	
				
			}		
		}
		try {
			Thread.currentThread().sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		MonitorEventQueue stub = (MonitorEventQueue) UnicastRemoteObject.exportObject(this, 0); // Exports remote object		
		registry.bind(name, stub); // Binds a remote reference
		registry.lookup(name);
		
	}

	@Override
	public synchronized void notifyEvent(RemotePHATEvent event) {	
		System.out.println(event);
		queue.add(event);		
	}
	
	@Override
	public synchronized Vector<RemotePHATEvent> retrieveAllEvents(){
		Vector<RemotePHATEvent> events=new Vector<RemotePHATEvent>();
		events.addAll(events);
		queue.clear();
		return events;
		
	}


}
