/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
