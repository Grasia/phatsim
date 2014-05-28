/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
