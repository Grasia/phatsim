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
package phat.codeproc.pd;

import ingenias.exception.NotFound;
import ingenias.exception.NullEntity;
import ingenias.generator.browser.Browser;
import ingenias.generator.browser.Graph;
import ingenias.generator.browser.GraphEntity;
import ingenias.generator.datatemplate.Sequences;
import phat.codeproc.Utils;

/**
 *
 * @author pablo
 */
public class FilterDiagramGenerator {

    static final String FILTER_DIAGRAM = "FilterDiagram";
    static final String ALLOWED_TASK_REL = "AllowedTask";
    static final String NEXT_FILTER_REL = "NextFilter";
    static final String ALTERNATIVE_REL = "FAlternative";
    static final String PRECONDITION_REL = "FPrecondition";
    static final String SELECTOR_FILTER_TYPE = "FTaskSelectorFilter";
    static final String DELAY_FILTER_TYPE = "FDelayFilter";
    static final String PLACE_FILTER_TYPE = "FModifyPlaceFilter";
    static final String REPLACE_TASK_FILTER_TYPE = "FReplaceTaskFilter";
    static final String TARGET_OBJ_FILTER_TYPE = "FChangeTargetObjFilter";
    static final String CHANGE_TOOL_FILTER_TYPE = "FChangeToolFilter";
    static final String UNABLE_FILTER_TYPE = "FUnableFilter";
    Browser browser;

    public FilterDiagramGenerator(Browser browser) {
        this.browser = browser;
    }

    public void generateFilters(Sequences seq) throws NullEntity, NotFound {
        for (Graph filterGraph : Utils.getGraphsByType(FILTER_DIAGRAM, browser)) {
            System.out.println(filterGraph.getID());
            for (GraphEntity firstFilter : Utils.getFirstEntities(filterGraph)) {
                System.out.println("\tFirstFilter = " + firstFilter.getID());
                processFilterSequence(firstFilter);
            }
        }
    }

    private void processFilterSequence(GraphEntity firstFilter) {
        if (firstFilter.getType().equals(SELECTOR_FILTER_TYPE)) {
        } else if (firstFilter.getType().equals(DELAY_FILTER_TYPE)) {
        } else if (firstFilter.getType().equals(PLACE_FILTER_TYPE)) {
        } else if (firstFilter.getType().equals(REPLACE_TASK_FILTER_TYPE)) {
        } else if (firstFilter.getType().equals(TARGET_OBJ_FILTER_TYPE)) {
        } else if (firstFilter.getType().equals(CHANGE_TOOL_FILTER_TYPE)) {
        } else if (firstFilter.getType().equals(UNABLE_FILTER_TYPE)) {
        }
        GraphEntity nextFilter = Utils.getTargetEntity(firstFilter, NEXT_FILTER_REL, firstFilter.getRelationships());
        if(nextFilter != null) {
            System.out.println("\tnextFilter = " + nextFilter.getID());
            processFilterSequence(nextFilter);
        }
    }
}
