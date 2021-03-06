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
package phat.util;

import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingSphere;
import com.jme3.bounding.BoundingVolume;
import com.jme3.bullet.control.AbstractPhysicsControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author pablo
 */
public class SpatialUtils {

    public static Vector3f getCenterBoinding(Spatial spatial) {
        spatial.updateModelBound();
        BoundingVolume wb = spatial.getWorldBound();
        if (wb == null) {
            return spatial.getWorldTranslation();
        } else {
            return wb.getCenter();
        }
    }

    public static Vector3f getMaxBounding(Spatial spatial) {
        spatial.updateModelBound();
        BoundingVolume wb = spatial.getWorldBound();

        if (wb instanceof BoundingBox) {
            BoundingBox bb = (BoundingBox) wb;
            Vector3f max = new Vector3f();
            return bb.getMax(max);
        } else if (wb instanceof BoundingSphere) {
            BoundingSphere bs = (BoundingSphere) wb;
            float radius = bs.getRadius();
            return new Vector3f(radius, radius, radius);
        }
        return spatial.getWorldTranslation();
    }

    public static Vector3f getMinBounding(Spatial spatial) {
        spatial.updateModelBound();
        BoundingVolume wb = spatial.getWorldBound();

        if (wb instanceof BoundingBox) {
            BoundingBox bb = (BoundingBox) wb;
            Vector3f min = new Vector3f();
            return bb.getMin(min);
        } else if (wb instanceof BoundingSphere) {
            BoundingSphere bs = (BoundingSphere) wb;
            float radius = bs.getRadius();
            return new Vector3f(-radius, -radius, -radius);
        }
        return spatial.getWorldTranslation();
    }

    public static boolean contains(Spatial container, Spatial entity) {
        container.updateModelBound();
        Vector3f min = SpatialUtils.getMinBounding(container);
        Vector3f max = SpatialUtils.getMaxBounding(container);
        Vector3f center = SpatialUtils.getCenterBoinding(entity);
        if (min.x <= center.x && min.y <= center.y && min.z <= center.z
                && max.x >= center.x && max.y >= center.y && max.z >= center.z) {
            return true;
        }
        return false;
    }

    public static boolean breadthFirstTraversal(Spatial spatial, PHATSceneGraphVisitor visitor) {
        List<Spatial> notVisited = new ArrayList<Spatial>();

        notVisited.add(spatial);

        while (!notVisited.isEmpty()) {
            Spatial s = notVisited.get(0);
            notVisited.remove(0);
            if (visitor.visit(s)) {
                return true;
            }
            if (s instanceof Node) {
                Node n = (Node) s;
                notVisited.addAll(notVisited.size(), n.getChildren());
            }
        }
        return false;
    }

    public static Node getRootNode(Spatial spatial) {
        if (spatial.getParent() == null) {
            return null;
        }
        Node result = spatial.getParent();
        while (result.getParent() != null) {
            result = result.getParent();
        }
        return result;
    }

    public static Spatial getSpatialById(Spatial rootNode, final String entityId) {
        final Node result = null;

        PHATSceneGraphVisitor visitor = new PHATSceneGraphVisitor() {
            Spatial result = null;

            @Override
            public boolean visit(Spatial spat) {
                String id = spat.getUserData("ID");
                if (id != null && id.equals(entityId)) {
                    result = spat;
                    return true;
                }
                return false;
            }

            @Override
            public Spatial getSpatial() {
                return result;
            }
        };

        if (SpatialUtils.breadthFirstTraversal(rootNode, visitor)) {
            return visitor.getSpatial();
        }

        return result;
    }
    
    public static Map<String,Spatial> getAllSpatialWithId(Spatial rootNode, final Map<String,Spatial> store) {
        SceneGraphVisitor visitor = new SceneGraphVisitor() {

            @Override
            public void visit(Spatial spat) {
                String id = spat.getUserData("ID");
                if (id != null) {
                    store.put(id, spat);
                }
            }
        };

        rootNode.breadthFirstTraversal(visitor);

        return store;
    }

    public static Spatial getParentSpatialWithRole(Spatial spatial, String role) {
        Spatial cSpatial = spatial;
        String cRole;
        
        while(cSpatial != null) {
            cRole = cSpatial.getUserData("ROLE");
            if(cRole != null && cRole.equals(role)) {
                return cSpatial;
            }
            cSpatial = cSpatial.getParent();
        }
        return null;
    }
    
    public static List<Spatial> getSpatialsByRole(Spatial rootNode, final String targetRol) {
        final List<Spatial> result = new ArrayList<Spatial>();

        SceneGraphVisitor visitor = new SceneGraphVisitor() {
            @Override
            public void visit(Spatial spat) {
                String rol = spat.getUserData("ROLE");
                if (rol != null && rol.equals(targetRol)) {
                    result.add(spat);
                }
            }
        };

        rootNode.breadthFirstTraversal(visitor);

        return result;
    }
    
    public static Spatial getNearest(Spatial spatial, final String targetRol) {
        Spatial result = null;
        float minDist = Float.MAX_VALUE;
        for(Spatial s: getSpatialsByRole(getRootNode(spatial), targetRol)) {
             float distance = s.getWorldTranslation().distance(spatial.getWorldTranslation());
             if(distance < minDist) {
                 minDist = distance;
                 result = s;
             }
        }
        return result;
    }
    
    public static void printControls(Spatial spatial) {
        System.out.println("Controls of "+spatial.getName());
        for (int i = 0; i < spatial.getNumControls(); i++) {
            Control c = spatial.getControl(i);
            System.out.print("\t-" + c.getClass().getSimpleName());
            if (c instanceof AbstractControl) {
                System.out.println(" " + ((AbstractControl) c).isEnabled());
            } else if (c instanceof AbstractPhysicsControl) {
                System.out.println(" " + ((AbstractPhysicsControl) c).isEnabled());
            } else {
                System.out.println("");
            }
        }
        System.out.println("");
    }
    
    public static void printParents(Spatial spatial) {
        String tab = "\t";
        System.out.println(spatial+":");
        Spatial parent = spatial.getParent();
        while(parent != null) {
            System.out.println(tab+parent.getName());
            parent = parent.getParent();
        }
    }
    public static void printChildrens(Spatial spatial) {
        printChindrens(spatial, 0);
    }
    
    private static void printChindrens(Spatial spatial, int depth) {
        for(int i = 0; i < depth; i++) {
            System.out.print('\t');
        }
        System.out.println("->"+spatial.getName());
        if(spatial instanceof Node) {
            Node parent = (Node) spatial;
            for(Spatial child: parent.getChildren()) {
                printChindrens(child, depth+1);
            }
        }
    }
}
