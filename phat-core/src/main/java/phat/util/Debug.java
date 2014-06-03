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

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.debug.Grid;

/**
 * A util class for debugging. It creates a grid to have a reference of
 * distances in the scenario.
 *
 * @author Jorge
 */
public class Debug {

    public static void enableDebugGrid(float size, AssetManager assetManager, Node rootNode) {
        attachCoordinateAxes(Vector3f.ZERO, size, assetManager, rootNode);
        attachGrid(Vector3f.ZERO, size * 2, ColorRGBA.Cyan, assetManager, rootNode);
    }

    //Code based on http://jmonkeyengine.org/wiki/doku.php/jme3:advanced:debugging
    public static void attachLocalCoordinateAxes(Node node, float size, AssetManager assetManager, Node rootNode) {
        
        Node n = new Node();
        n.setLocalTranslation(node.getWorldTranslation());
        rootNode.attachChild(n);
        
        Arrow arrow = new Arrow(node.getWorldRotation().mult(Vector3f.UNIT_X).normalize().mult(size));
        arrow.setLineWidth(4); // make arrow thicker
        putShape(arrow, ColorRGBA.Red, assetManager, n);

        arrow = new Arrow(node.getWorldRotation().mult(Vector3f.UNIT_Y).normalize().mult(size));
        arrow.setLineWidth(4); // make arrow thicker
        putShape(arrow, ColorRGBA.Green, assetManager, n);

        arrow = new Arrow(node.getWorldRotation().mult(Vector3f.UNIT_Z).normalize().mult(size));
        arrow.setLineWidth(4); // make arrow thicker
        putShape(arrow, ColorRGBA.Blue, assetManager, n);
    }

    //Code based on http://jmonkeyengine.org/wiki/doku.php/jme3:advanced:debugging
    public static void attachCoordinateAxes(Vector3f pos, float size, AssetManager assetManager, Node rootNode) {
        Arrow arrow = new Arrow(Vector3f.UNIT_X.mult(size));
        arrow.setLineWidth(4); // make arrow thicker
        putShape(arrow, ColorRGBA.Red, assetManager, rootNode).setLocalTranslation(pos);

        arrow = new Arrow(Vector3f.UNIT_Y.mult(size));
        arrow.setLineWidth(4); // make arrow thicker
        putShape(arrow, ColorRGBA.Blue, assetManager, rootNode).setLocalTranslation(pos);

        arrow = new Arrow(Vector3f.UNIT_Z.mult(size));
        arrow.setLineWidth(4); // make arrow thicker
        putShape(arrow, ColorRGBA.Green, assetManager, rootNode).setLocalTranslation(pos);
    }

    // code based on http://jmonkeyengine.org/wiki/doku.php/jme3:advanced:debugging
    private static Geometry putShape(Mesh shape, ColorRGBA color,
            AssetManager assetManager, Node rootNode) {
        Geometry g = new Geometry("coordinate axis", shape);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(true);
        mat.setColor("Color", color);
        g.setMaterial(mat);
        rootNode.attachChild(g);
        return g;
    }

    // code based on http://jmonkeyengine.org/wiki/doku.php/jme3:advanced:debugging
    private static void attachGrid(Vector3f pos, float size, ColorRGBA color,
            AssetManager assetManager, Node rootNode) {
        Geometry g = new Geometry("wireframe grid", new Grid((int) size, (int) size, 1f));
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(true);
        mat.setColor("Color", color);
        g.setMaterial(mat);
        g.center().move(pos);
        rootNode.attachChild(g);
    }
}
