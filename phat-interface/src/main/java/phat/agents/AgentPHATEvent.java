package phat.agents;

import java.io.Serializable;

import com.jme3.math.Vector3f;

import phat.agents.events.PHATEvent;
import phat.body.BodyUtils.BodyPosture;
import phat.world.PHATCalendar;
import phat.world.RemotePHATEvent;


public class AgentPHATEvent extends RemotePHATEvent implements Serializable{


	private BodyPosture bodyPosture;
	private String nextActionName;

	public BodyPosture getBodyPosture() {
		return bodyPosture;
	}

	public String getNextActionName() {
		return nextActionName;
	}

	public AgentPHATEvent(String id, Vector3f location, PHATCalendar time,
			BodyPosture bodyPosture, String nextActionName){
		super(id,location,time);
		this.bodyPosture=bodyPosture;
		this.nextActionName=nextActionName;
	}

	public boolean similar(RemotePHATEvent object){
		if (object!=null && object instanceof AgentPHATEvent){
			AgentPHATEvent other=(AgentPHATEvent)object;
			return other.bodyPosture.equals(bodyPosture) 
					&& other.nextActionName.equals(nextActionName) && 
					super.similar(object);
		} else 
			return false;

	}
	
	public String toString(){
		return getId()+","+getTime()+","+getLocation()+","+getBodyPosture()+","+getNextActionName();
	}

	public boolean equals(Object object){
		if (object instanceof AgentPHATEvent){
			AgentPHATEvent other=(AgentPHATEvent)object;
			return other!=null && other.bodyPosture.equals(bodyPosture) 
					&& other.nextActionName.equals(nextActionName) && 
					super.equals(object);
		}
		return false;
	}

}
