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
package phat.body.control.navigation;

import com.jme3.ai.navmesh.Path;
import com.jme3.ai.navmesh.Path.Waypoint;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.debug.Arrow;
import phat.util.SpatialFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Useful class for showing a path graphically using arrows.
 * The parameters are the path and the node which will be the parent of 
 * the generated graphical path.
 * 
 * @author pablo
 */
public class PathViewer {
    private static final Logger logger = Logger.getLogger(PathViewer.class.getName());
    
    private Node graphicalPath;
    private Path path;
    private Node root;
    private Vector3f offset = new Vector3f();
    
    private List<Waypoint> optimizedPath;
    
    
    public PathViewer(Path path, Node root, Vector3f offset) {
        this.path = path;
        this.root = root;
        this.offset = offset;
    }
    
    public void showPath() {
        if(graphicalPath == null)
            createGraphicalPath();
        if(graphicalPath.getParent() == null)
            root.attachChild(graphicalPath);
    }
    
    private void createGraphicalPath() {
        optimizedPath = new ArrayList<>();
        graphicalPath = new Node();
        Waypoint source = path.getFirst();
        Waypoint target = path.getFurthestVisibleWayPoint(source);
        optimizedPath.add(source);
        optimizedPath.add(target);
        for (int index = 0; source != path.getLast(); index++) {
            Vector3f v1 = source.getPosition();
            Vector3f v2 = target.getPosition();
            Arrow arrow = new Arrow(v2.subtract(v1));
            arrow.setLineWidth(5); // make arrow thicker                            
            Geometry geometry = SpatialFactory.createShape("WayPoint-"+index, arrow, ColorRGBA.Black);
            geometry.setLocalTranslation(v1.add(0f,0.1f,0f).add(offset));            
            graphicalPath.attachChild(geometry);
            source = target;
            target = path.getFurthestVisibleWayPoint(source);
            optimizedPath.add(target);
        }
        if (!path.getWaypoints().isEmpty()) {
            logger.log(Level.INFO, "Optimized path = {0}",
                    new Object[]{optimizedPath});
        }
    }
    
    public void hidePath() {
        root.detachChild(graphicalPath);
    }
}
