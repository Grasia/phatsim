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
package phat.gui.logging.control;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.RowFilter;
import javax.swing.JCheckBoxMenuItem;

/**
 *
 * @author pablo
 */
public class CheckBoxMenuItemRowFilter extends RowFilter<Object, Object> {

    Map<Integer, List<JCheckBoxMenuItem>> elements = new HashMap<>();

    @Override
    public boolean include(Entry entry) {
        for (int i = 0; i < entry.getValueCount(); i++) {
            List<JCheckBoxMenuItem> cBoxes = elements.get(i);
            if (cBoxes == null) {
                continue;
            }
            if (noneSelected(cBoxes)) {
                continue;
            }

            if (entry.getValue(i) instanceof String) {
                String value = (String) entry.getValue(i);
                if (!matches(cBoxes, value)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean noneSelected(List<JCheckBoxMenuItem> cBoxes) {
        for (JCheckBoxMenuItem check : cBoxes) {
            if (check.isSelected()) {
                return false;
            }
        }
        return true;
    }

    private boolean matches(List<JCheckBoxMenuItem> cBoxes, String value) {
        for (JCheckBoxMenuItem check : cBoxes) {
            if (check.isSelected() && check.getText().equals(value)) {
                return true;
            }
        }
        return false;
    }

    public void setCheckBoxItemList(int cNum, List<JCheckBoxMenuItem> checkBoxs) {
        elements.put(cNum, checkBoxs);
    }
}
