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
package phat.agents;

import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.Vector;

import com.jme3.math.Vector3f;

import java.util.ArrayList;
import java.util.List;

import phat.PHATInterface;
import phat.agents.automaton.Automaton;
import phat.agents.automaton.AutomatonListener;
import phat.agents.events.PHATEvent;
import phat.agents.events.PHATEventManager;
import phat.body.BodiesAppState;
import phat.body.BodyUtils;
import phat.body.BodyUtils.BodyPosture;
import phat.commands.PHATCommand;
import phat.world.MonitorEventQueue;
import phat.world.PHATCalendar;

public abstract class Agent implements PHATAgentTick {

	protected Automaton automaton;
	boolean init = false;
	AgentsAppState agentsAppState;
	private Hashtable<String, Vector3f> listened = new Hashtable<String, Vector3f>();
	private static Vector<Agent> instances = new Vector<Agent>();
	private String bodyId;
	PHATEventManager eventManager;

	MonitorEventQueue eventListener=null;

	abstract protected void initAutomaton();

	public static void shout(String word, Vector3f location) {
		for (Agent instance : instances) {
			instance.listened(word, location);
		}
	}

	private void registerListenerIntoAutomaton(){
		if (getAutomaton()!=null && getListener()!=null){
			
			getAutomaton().addListener(new AutomatonListener() {

				private AgentPHATEvent lastEvent=null;
				@Override
				public void preInit(Automaton automaton) {
					// TODO Auto-generated method stub

				}

				@Override
				public void postInit(Automaton automaton) {
					

				}

				@Override
				public void nextAutomaton(Automaton previousAutomaton,
						Automaton nextAutomaton) {
					
					AgentPHATEvent currentEvent=null;
					if (automaton.getLeafAutomaton()!=null){
						currentEvent=
								new AgentPHATEvent(getId(), 
										getLocation(), 
										getTime(), getBodyPosture(),
										automaton.getLeafAutomaton().getName());
						

					} else {
						currentEvent=
								new AgentPHATEvent(getId(), 
										getLocation(), 
										getTime(), getBodyPosture(),
										"undertermined");

					}
					System.out.println("Registrandoooooo2 "+currentEvent);
					if (lastEvent==null ||(lastEvent!=null && !lastEvent.similar(currentEvent))){
						lastEvent=currentEvent;
						try {
							System.out.println("Registrandoooooo1");
							eventListener.notifyEvent(currentEvent);
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}	


					}
					nextAutomaton.addListener(this);



				}

				@Override
				public void automatonResumed(Automaton resumedAutomaton) {
					// TODO Auto-generated method stub

				}

				@Override
				public void automatonInterrupted(Automaton automaton) {
					// TODO Auto-generated method stub

				}

				@Override
				public void automatonFinished(Automaton automaton, boolean isSuccessful) {
					// TODO Auto-generated method stub

				}
			});
		}

	}

	public void registerListener(MonitorEventQueue meq) {
		eventListener=meq;		
		registerListenerIntoAutomaton();
	}    

	public MonitorEventQueue getListener() {
		return eventListener;
	}  

	public String getId() {
		return bodyId;
	}

	public Agent(String bodyId) {
		this.bodyId = bodyId;
		instances.add(this);
		eventManager = new PHATEventManager(this);
	}

	protected Vector3f haveIHeard(String word) {
		Vector<String> candidates = new Vector<String>();
		for (String c : listened.keySet()) {
			if (c.toLowerCase().indexOf(word.toLowerCase()) >= 0) {
				candidates.add(c);
			}
		}
		if (candidates.isEmpty()) {
			return null;
		}
		return listened.get(candidates.firstElement());
	}

	public void listened(String word, Vector3f source) {
		this.listened.put(word, source);
	}

	public String getCurrentAction() {
		if (automaton != null) {
			return automaton.getCurrentAction();
		}
		return "";
	}

	public Automaton getAutomaton() {
		return automaton;
	}

	public void setAutomaton(Automaton automaton) {
		this.automaton = automaton;
		this.registerListenerIntoAutomaton();
	}

	@Override
	public void update(PHATInterface phatInterface) {
		if (!init) {
			initAutomaton();
			init = true;
		}
		if(eventManager.areEvents()) {
			eventManager.process(phatInterface);
		}
		if (automaton != null) {
			//System.out.println(bodyId+": "+automaton.getCurrentAction());
			automaton.nextState(phatInterface);

		}


	}

	public Vector3f getLocation() {
		return agentsAppState.getBodiesAppState().getLocation(bodyId);
	}

	public void runCommand(PHATCommand command) {
		agentsAppState.getBodiesAppState().runCommand(command);
	}

	public PHATCalendar getTime() {
		return agentsAppState.getBodiesAppState().getTime();
	}

	public void setAgentsAppState(AgentsAppState agentsAppState) {
		this.agentsAppState = agentsAppState;
	}

	public AgentsAppState getAgentsAppState() {
		return agentsAppState;
	}

	public PHATEventManager getEventManager() {
		return eventManager;
	}

	public boolean isInTheWorld() {
		return agentsAppState.getBodiesAppState().isBodyInTheWorld(bodyId);
	}

	public BodiesAppState getBodiesAppState() {
		return agentsAppState.getBodiesAppState();
	}

	public BodyPosture getBodyPosture() {
		return BodyUtils.getBodyPosture(getBodiesAppState().getBody(bodyId));
	}
}