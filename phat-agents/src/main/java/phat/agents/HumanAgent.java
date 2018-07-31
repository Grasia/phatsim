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

import com.jme3.math.Vector3f;
import phat.PHATInterface;
import phat.agents.automaton.Automaton;
import phat.agents.automaton.AutomatonListener;
import phat.agents.automaton.MoveToBodyLocAutomaton;
import phat.agents.automaton.WaitForCloseToBodyAutomaton;
import phat.agents.events.PHATEventManager;
import phat.agents.filters.DiseaseManager;
import phat.body.BodiesAppState;
import phat.body.BodyUtils;
import phat.commands.PHATCommand;
import phat.world.MonitorEventQueue;
import phat.world.MonitorEventQueueImp;

/**
 *
 * @author pablo
 */
public class HumanAgent extends Agent {

    DiseaseManager diseaseManager;
    MonitorEventQueueImp eventListener = null;
    
    public HumanAgent(String bodyId) {
        super(bodyId);
    }

    @Override
    public void agentUpdate(PHATInterface phatInterface) {
        super.update(phatInterface);
        
        if (diseaseManager != null) {
            diseaseManager.updateSymptoms(phatInterface);
        }
        
        if (eventListener != null) {
            eventListener.setSimTime(phatInterface.getSimTime().getTimeInMillis());
        }
    }
    
    @Override
    protected void initAutomaton() {
    }
    
    public BodiesAppState getBodiesAppState() {
        return agentsAppState.getBodiesAppState();
    }

    public BodyUtils.BodyPosture getBodyPosture() {
        return BodyUtils.getBodyPosture(getBodiesAppState().getBody(bodyId));
    }
    
    public DiseaseManager getDiseaseManager() {
        return diseaseManager;
    }

    public void setDiseaseManager(DiseaseManager diseaseManager) {
        this.diseaseManager = diseaseManager;
    }
    
    @Override
    public boolean isInTheWorld() {
        return agentsAppState.getBodiesAppState().isBodyInTheWorld(bodyId);
    }
    
    @Override
    public boolean isInAHouse(String idHouse) {
        return agentsAppState.getBodiesAppState().isBodyInAHouse(bodyId);
    }
    
    @Override
    public void runCommand(PHATCommand command) {
        agentsAppState.getBodiesAppState().runCommand(command);
    }
    
    @Override
    public Vector3f getLocation() {
        return agentsAppState.getBodiesAppState().getLocation(bodyId);
    }
    
    public void setAutomaton(Automaton automaton) {
        this.automaton = automaton;
        this.registerListenerIntoAutomaton();
        notifyAgentListener();
    }
    
    public MonitorEventQueue getListener() {
        return eventListener;
    }
    
    public void registerListener(MonitorEventQueueImp meq) {
        eventListener = meq;
        registerListenerIntoAutomaton();
    }
    
    private void registerListenerIntoAutomaton() {
        if (getAutomaton() != null && getListener() != null) {

            getAutomaton().addListener(new AutomatonListener() {
                private AgentPHATEvent lastEvent = null;

                @Override
                public void stateChanged(Automaton automaton, Automaton.STATE state) {
                    if (state == Automaton.STATE.STARTED) {
                        AgentPHATEvent currentEvent = null;
                        String aided = null;
                        Automaton result = automaton.containsStateOfKind(MoveToBodyLocAutomaton.class);

                        if (result != null) {
                            aided = ((MoveToBodyLocAutomaton) result).getDestinyBodyName();
                        }

                        Automaton thereIsSuccess = automaton.getRootParent().containsStateWithPrefix("success_");
                        Automaton thereIsFailure = automaton.getRootParent().containsStateWithPrefix("failure_");




                        String waitingForAssistance = null;

                        if (automaton instanceof WaitForCloseToBodyAutomaton) {
                            waitingForAssistance = ((WaitForCloseToBodyAutomaton) automaton).getDestinyBodyName();
                        }

                        if (automaton.getLeafAutomaton() != null) {
                            currentEvent =
                                    new AgentPHATEvent(getId(),
                                    getLocation(),
                                    getTime(), getBodyPosture(),
                                    automaton.getLeafAutomaton().getName());
                        } else {
                            currentEvent =
                                    new AgentPHATEvent(getId(),
                                    getLocation(),
                                    getTime(), getBodyPosture(),
                                    "undertermined");


                        }
                        currentEvent.setAided(aided);
                        currentEvent.setElapsedTime(getElapsedTimeSeconds());
                        if (automaton.getMetadata("SOCIAALML_ENTITY_TYPE") != null && !automaton.getMetadata("SOCIAALML_ENTITY_TYPE").equals("")) {
                            currentEvent.setActionType(automaton.getMetadata("SOCIAALML_ENTITY_TYPE"));
                        } else {
                            currentEvent.setActionType(automaton.getName());
                        }

                        currentEvent.setScope(automaton.getName());
                        if (thereIsSuccess != null) {
                            currentEvent.setSuccess(true);
                        }
                        if (thereIsFailure != null) {
                            currentEvent.setFailure(true);
                        }

                        if (automaton.getName().toLowerCase().startsWith("success_")) {
                            currentEvent.setSuccessAction(true);
                        }
                        if (automaton.getName().toLowerCase().startsWith("failure_")) {
                            currentEvent.setFailureAction(true);
                        }

                        System.out.println("Registrandoooooo2 " + currentEvent);
                        if (lastEvent == null || (lastEvent != null && !lastEvent.similar(currentEvent))) {
                            lastEvent = currentEvent;

                          
                            eventListener.notifyEvent(currentEvent);

                        }
                    }
                }
            });
        }

    }

}
