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
package phat.gui.logging;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import phat.agents.Agent;
import phat.agents.AgentListener;
import phat.agents.AgentsAppState;
import phat.agents.automaton.Automaton;
import phat.agents.automaton.AutomatonListener;
import phat.app.PHATApplication;
import phat.world.PHATCalendar;

/**
 *
 * @author pablo
 */
public class LoggingViewerAppState extends AbstractAppState implements AutomatonListener, AgentListener {

    PHATApplication app;
    AgentsAppState agentsAppState;
    LogRecordTableModel tableModel;
    PHATCalendar simStartTime;
    PHATCalendar simTime;
    JFrame frame;
    
    Map<String, Logger> loggers = new HashMap<>();
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);

        this.app = (PHATApplication) app;

        agentsAppState = app.getStateManager().getState(AgentsAppState.class);

        simTime = agentsAppState.getPHAInterface().getSimTime();
        simStartTime = new PHATCalendar(simTime);
        
        tableModel = new LogRecordTableModel();

        LogTableHandler tableHandler = new LogTableHandler(tableModel);
        tableHandler.setLevel(Level.INFO);
        tableHandler.setFilter(null);
        
        System.out.println("\n\n\n***************************");
        System.out.println(agentsAppState.getAgentIds());
        
        for (String id : agentsAppState.getAgentIds()) {
            Agent a = agentsAppState.getAgent(id);
            if(a.getAutomaton() != null) {
                a.getAutomaton().addListener(this);
            }
            a.addListener(this);
            Logger logger = Logger.getLogger(id);
            loggers.put(id, logger);
            logger.addHandler(tableHandler);
        }
        System.out.println("***************************\n\n\n");  

        createAndShowGUI();
    }

    private void createAndShowGUI() {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                //Create and set up the window.
                frame = new JFrame("PHAT Log viewer");
                //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                //Create and set up the content pane.
                LogViewerPanel newContentPane = new LogViewerPanel(tableModel);
                newContentPane.setOpaque(true); //content panes must be opaque
                frame.setContentPane(newContentPane);

                //Display the window.
                frame.pack();
            }
        });
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);

    }

    public void show() {
        frame.setVisible(true);
    }
    
    public boolean isShown() {
        return frame.isVisible();
    }
    
    public void hide() {
        frame.setVisible(false);
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
        
        if(frame != null) {
            frame.setVisible(false);
            frame.dispose();
        }
    }

    @Override
    public void preInit(Automaton automaton) {
    }

    @Override
    public void postInit(Automaton automaton) {
        log(automaton, "START");
    }

    private void log(Automaton automaton, String state) {
        String message = automaton.getMetadata("SOCIAALML_DESCRIPTION");
        String taskID = automaton.getMetadata("SOCIAALML_ENTITY_ID");
        String taskType = automaton.getMetadata("SOCIAALML_ENTITY_TYPE");
                
        if(taskID == null || taskID.equals("")) {
            return;
        }
        
        String time = ""+(simStartTime.spentTimeTo(simTime));
        
        Logger logger = loggers.get(automaton.getAgent().getId());
        
        Object[] params = {time, state, taskID, taskType, automaton};
        logger.log(Level.INFO, message, params);
    }
    
    @Override
    public void nextAutomaton(Automaton previousAutomaton, Automaton nextAutomaton) {
        nextAutomaton.addListener(this);
    }

    @Override
    public void automatonFinished(Automaton automaton, boolean isSuccessful) {
        log(automaton, "FINISH");
    }

    @Override
    public void automatonInterrupted(Automaton automaton) {
        log(automaton, "INTERRUPT");
    }

    @Override
    public void automatonResumed(Automaton resumedAutomaton) {
        log(resumedAutomaton, "RESUME");
    }

    @Override
    public void agentChanged(Agent agent) {
        if(agent.getAutomaton() != null) {
            agent.getAutomaton().addListener(this);
        }
    }
}
