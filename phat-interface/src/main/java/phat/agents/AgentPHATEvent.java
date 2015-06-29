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
	private String aided;
	private boolean success=false;
	private boolean failure=false;
	private String scope="";
	private String actionType="";

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public AgentPHATEvent(String id, Vector3f location, PHATCalendar time,
			BodyPosture bodyPosture, String nextActionName){
		super(id,location,time);
		this.bodyPosture=bodyPosture;
		this.nextActionName=nextActionName;
	}
	
	public boolean isSuccess(){
		return success;
	}
	
	public boolean isFailure(){
		return success;
	}
	
	public void setScope(String scope){
		this.scope= scope;
	}
	
	public String getScope(){
		return scope;
	}
	
	

	public BodyPosture getBodyPosture() {
		return bodyPosture;
	}

	public String getNextActionName() {
		return nextActionName;
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
		return "AgentPHATEvent:"+getId()+","+getTime()+","+getLocation()+","+getBodyPosture()+","+getNextActionName();
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

	public void setAided(String aided) {
		this.aided=aided;
	}
	
	public String getAided(){
		return aided;
	}

	public void setFailure(boolean b) {
		failure=b;
		
	}

}
