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
package phat.agents.filters.types;

import java.lang.reflect.InvocationTargetException;

import phat.agents.Agent;
import phat.agents.automaton.Automaton;
import phat.agents.automaton.SeqTaskAutomaton;

/**
 *
 * @author pablo
 */
public class ReplaceTaskFilter extends Filter {

    Class<? extends Automaton> task;

    public enum TYPE {

        REPLACE, BEFORE, AFTER
    }
    TYPE type = TYPE.REPLACE;

    @Override
    public Automaton apply(Agent agent, Automaton automaton) {
        Automaton filterTask = null;
        try {
            filterTask = task.getConstructor(Agent.class, String.class).newInstance(agent, task.getClass().getSimpleName());
            setMetadata(filterTask);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }
        switch (type) {
            case REPLACE:
                return filterTask;
            case BEFORE:
                return createBeforeParent(agent, filterTask, automaton);
            case AFTER:
                return createAfterParent(agent, filterTask, automaton);
        }
        return filterTask;
    }

    private Automaton createBeforeParent(Agent agent, final Automaton filteredTask, final Automaton newTask) {
        SeqTaskAutomaton sta = new SeqTaskAutomaton(agent, "ReplaceTaskFilter-"+type.name()) {
            @Override
            public void initTasks() {
                addTransition(newTask, false);
                addTransition(filteredTask, false);
        
                setMetadata("SOCIAALML_ENTITY_ID", "ReplaceTaskFilter-"+type.name());
                setMetadata("SOCIAALML_ENTITY_TYPE", "SequentialTaskDiagram");
            }
        };
                
        return sta;
    }
    
    private Automaton createAfterParent(Agent agent, final Automaton filteredTask, final Automaton newTask) {
        SeqTaskAutomaton sta = new SeqTaskAutomaton(agent, "ReplaceTaskFilter-"+type.name()) {
            @Override
             public void initTasks() {
                addTransition(filteredTask, false);
                addTransition(newTask, false);
                
                setMetadata("SOCIAALML_ENTITY_ID", "ReplaceTaskFilter-"+type.name());
                setMetadata("SOCIAALML_ENTITY_TYPE", "SequentialTaskDiagram");
            }
        };
        
        return sta;
    }
    
    private void setMetadata(Automaton a) {
        a.setMetadata("SOCIAALML_ENTITY_ID", "ReplaceTaskFilter-"+type.name());
        a.setMetadata("SOCIAALML_ENTITY_TYPE", "SequentialTaskDiagram");
    }
    
    public ReplaceTaskFilter setTask(Class<? extends Automaton> task) {
        this.task = task;
        return this;
    }

    public TYPE getType() {
        return type;
    }

    public ReplaceTaskFilter setType(TYPE type) {
        this.type = type;
        return this;
    }
}
