package phat.world;

import java.io.Serializable;



import com.jme3.math.Vector3f;

public class RemotePHATEvent implements Serializable{

	private String id;
	private Vector3f location;
	public String getId() {
		return id;
	}

	public Vector3f getLocation() {
		return location;
	}

	public PHATCalendar getTime() {
		return time;
	}

	private PHATCalendar time;

	public RemotePHATEvent(String id, Vector3f location, PHATCalendar time) {
		this.id=id;
		this.location=location;
		this.time=time;
	}
	
	public boolean similar(RemotePHATEvent object){
		
			
			return object!=null && object.id.equals(id) ;
		
	}
	
	public boolean equals(Object object){
		if (object instanceof RemotePHATEvent){
			RemotePHATEvent other=(RemotePHATEvent)object;
			return other!=null && other.id.equals(id) 
					&& other.location.equals(location) && other.time.equals(time);
		}
		return false;
	}


}
