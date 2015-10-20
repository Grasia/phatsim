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
package phat.commands;

import com.jme3.app.Application;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import java.util.logging.Level;

import phat.commands.PHATCommand.State;
import phat.scene.control.PHATKeepObjectAtOffset;
import phat.util.SpatialFactory;
import phat.util.SpatialUtils;

/**
 *
 * @author pablo
 */
public class ShowLabelOfObjectById extends PHATCommand {

    Node debugNode;
    private String objectId;
    private String debugId;
    private Boolean show;
    private ColorRGBA colour = ColorRGBA.Black;
    private float scale = 0.2f;
    private Vector3f offset = new Vector3f(0f, 0.1f, 0f);

    public ShowLabelOfObjectById(String objectId, Boolean show, PHATCommandListener listener) {
        super(listener);
        this.objectId = objectId;
        this.show = show;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }

    public ShowLabelOfObjectById(String bodyId, Boolean show) {
        this(bodyId, show, null);
    }

    @Override
    public void runCommand(Application app) {

        Spatial object = SpatialUtils.getSpatialById(SpatialFactory.getRootNode(), objectId);

        if (object != null) {
            debugId = objectId + "BitmapText";
            Node rootNode = SpatialFactory.getRootNode();
            debugNode = (Node) rootNode.getChild("DebugNode");
            if (debugNode == null) {
                debugNode = new Node("DebugNode");
                rootNode.attachChild(debugNode);
            }
            if (show) {
                attachName(object);
            } else {
                dettachName(object);
            }
        } else {
            setState(State.Fail);
        }
        setState(State.Success);
    }

    private Node attachName(Node target, Spatial source, String name) {
        Node textNode = (Node) target.getChild(name);
        if (textNode == null) {
            textNode = new Node(name);
            PHATKeepObjectAtOffset offsetControl = new PHATKeepObjectAtOffset(source);
            //calculateOffset(source);
            offsetControl.setOffset(offset);
            textNode.addControl(offsetControl);
            BitmapText text = SpatialFactory.attachAName(textNode, name);
            text.setColor(colour);
            text.setLocalScale(scale);
            Spatial cube = SpatialFactory.createCube(new Vector3f(0.02f,0.02f,0.02f), ColorRGBA.Red);
            cube.setLocalTranslation(offset.negate().addLocal(0f, 0.01f, 0f));
            textNode.attachChild(cube);
            target.attachChild(textNode);
        }
        return textNode;
    }

    private void attachName(Spatial s) {
        Node result = attachName(debugNode, s, objectId);
        if (s instanceof Node) {
            Spatial places = ((Node) s).getChild("Places");
            if (places != null) {
                Node p = (Node) places;
                if (p.getChildren() != null) {
                    for (Spatial pos : p.getChildren()) {
                        attachName(debugNode, pos, objectId+":"+pos.getName());
                    }
                }
            }
        }
    }

    private void calculateOffset(Spatial s) {
        Vector3f targetPosition = SpatialUtils.getCenterBoinding(s);
        Vector3f max = SpatialUtils.getMaxBounding(s);
        
        System.out.println(s.getName()+":"+targetPosition+":"+max);

        targetPosition.setY(max.y);

        offset.addLocal(targetPosition.subtract(s.getWorldTranslation()));
        System.out.println("Offset -> "+offset);
    }

    private void dettachName(Spatial s) {
        Spatial child = debugNode.getChild(debugId);
        if (child != null) {
            child.removeFromParent();
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

    public ShowLabelOfObjectById setOffset(Vector3f offset) {
        this.offset.set(offset);
        return this;
    }

    public ColorRGBA getColour() {
        return colour;
    }

    public ShowLabelOfObjectById setColour(ColorRGBA colour) {
        this.colour = colour;
        return this;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + objectId + ",show=" + show + ")";
    }
}
