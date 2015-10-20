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
package phat.structures.houses.commands.test;

import com.jme3.app.Application;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.HashMap;
import java.util.Map;

import java.util.logging.Level;
import phat.commands.PHATCommand;

import phat.commands.PHATCommand.State;
import phat.commands.PHATCommandListener;
import phat.commands.ShowLabelOfObjectById;
import phat.scene.control.PHATKeepObjectAtOffset;
import phat.structures.houses.HouseAppState;
import phat.util.SpatialFactory;
import phat.util.SpatialUtils;

/**
 *
 * @author pablo
 */
public class ShowAllLabelsOfScenario extends PHATCommand {

    Node debugNode;
    private Boolean show;
    private float scale = 0.4f;
    private Vector3f offset = new Vector3f(0f, 0.2f, 0f);

    public ShowAllLabelsOfScenario(Boolean show, PHATCommandListener listener) {
        super(listener);
        this.show = show;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }

    public ShowAllLabelsOfScenario(Boolean show) {
        this(show, null);
    }

    @Override
    public void runCommand(Application app) {

        Map<String, Spatial> objects = new HashMap<String, Spatial>();

        SpatialUtils.getAllSpatialWithId(SpatialFactory.getRootNode(), objects);

        System.out.println("Objects with ids:");
        for (String id : objects.keySet()) {
            System.out.println("\t* " + id);
            app.getStateManager().getState(HouseAppState.class).runCommand(new ShowLabelOfObjectById(id, true));
        }
    }

    @Override
    public void interruptCommand(Application app) {
        show = false;
        runCommand(app);
    }

    public Vector3f getOffset() {
        return offset;
    }

    public void setOffset(Vector3f offset) {
        this.offset.set(offset);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(show=" + show + ")";
    }
}
