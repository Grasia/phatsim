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

import java.util.ArrayList;
import java.util.List;
import phat.agents.Agent;
import phat.agents.automaton.Automaton;

/**
 *
 * @author pablo
 */
public class SelectorFilter extends Filter {

    boolean byType = false;
    List<String> taskTypes = new ArrayList<>();
    List<String> taskIds = new ArrayList<>();

    @Override
    public boolean checkCondition(Agent agent, Automaton automaton) {
        if (super.checkCondition(agent, automaton)) {
            if (byType) {
                String ref = automaton.getMetadata("SOCIAALML_ENTITY_TYPE");
                if (taskTypes.isEmpty() || (ref != null && taskTypes.contains(ref))) {
                    return true;
                }
            } else {
                String ref = automaton.getMetadata("SOCIAALML_ENTITY_ID");
                if (taskIds.isEmpty() || (ref != null && taskIds.contains(ref))) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Automaton apply(Agent agent, Automaton automaton) {
        return automaton;
    }

    public void addType(String taskType) {
        taskTypes.add(taskType);
    }

    public void addId(String taskId) {
        taskIds.add(taskId);
    }

    public boolean isByType() {
        return byType;
    }

    public SelectorFilter setByType(boolean byType) {
        this.byType = byType;
        return this;
    }
}
