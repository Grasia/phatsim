/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.gui.logging.control;

import java.util.HashMap;
import java.util.Map;
import javax.swing.RowFilter;
import java.util.regex.Pattern;

/**
 *
 * @author pablo
 */
public class PatternRowFilter<M,N> extends RowFilter<Object, Object> {

    Map<Integer, Pattern> patterns = new HashMap<>();

    @Override
    public boolean include(Entry entry) {
        System.out.println("PatternRowFilter.include()");
        for (int i = 0; i < entry.getValueCount(); i++) {
            if (entry.getValue(i) instanceof String) {
                String state = (String) entry.getValue(i);
                Pattern pattern = patterns.get(i);
                if (pattern != null && !pattern.matcher(state).find()) {
                    return false;
                }
            }
        }
        return true;
    }

    public void setPattern(int cNum, Pattern pattern) {
        patterns.put(cNum, pattern);
    }
}
