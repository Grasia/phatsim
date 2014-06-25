/*
 * Copyright (C) 2014 pablo <pabcampi@ucm.es>
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
package phat.body.sensing.vision;

import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import phat.scene.control.PHATKeepObjectAtOffset;
import phat.util.SpatialFactory;
import phat.util.SpatialUtils;

/**
 * It is a VisibleObjectsListener that shows and hides the label of the
 * objects that are visibles by a human.
 * 
 * @see VisibleObjectManager
 * @see VisibleObjectsListener
 * 
 * @author pablo <pabcampi@ucm.es>
 */
public class AttachLabelToVisibleObjectsControl extends AbstractControl implements VisibleObjectsListener {

    Node debugNode;
    Vector3f center = new Vector3f();
    Vector3f offset = new Vector3f(0f, 1f, 0f);
    
    VisionControl visionControl;
    
    public AttachLabelToVisibleObjectsControl() {
        
    }

    @Override
    public String getId() {
        return "AttachLabelToVisibleObjects";
    }
    
    @Override
    public void visible(VisibleObjInfo objInfo, VisibleObjectManager vom) {
        System.out.println("Visible = "+objInfo.getId());
        attachName(objInfo.getSpatial());
    }

    @Override
    public void noVisible(VisibleObjInfo objInfo, VisibleObjectManager vom) {
        System.out.println("NoVisible = "+objInfo.getId());
        dettachName(objInfo.getSpatial());
    }

    private void attachName(Spatial s) {
        String id = s.getUserData("ID");
        String name = id + "BitmapText";
        Node textNode = (Node) debugNode.getChild(name);
        if (textNode == null) {
            textNode = new Node(name);
            PHATKeepObjectAtOffset offsetControl = new PHATKeepObjectAtOffset(s);
            offsetControl.setOffset(offset);
            textNode.addControl(offsetControl);
            Spatial text = SpatialFactory.attachAName(textNode, id);
            text.setLocalScale(0.4f);
            debugNode.attachChild(textNode);
        }
    }

    private void dettachName(Spatial s) {
        String name = s.getUserData("ID") + "BitmapText";
        Spatial child = debugNode.getChild(name);
        if (child != null) {
            child.removeFromParent();
        }
    }

    private VisionControl getVisionControl(Spatial spatial) {
        if(visionControl == null) {
            visionControl = spatial.getControl(VisionControl.class);
        }
        return visionControl;
    }
    
    @Override
    public void setSpatial(Spatial spatial) {
        if(spatial != null) {
            Node rootNode = SpatialUtils.getRootNode(spatial);
            debugNode = (Node) rootNode.getChild("DebugNode");
            if(debugNode == null) {
                System.out.println("RootNode = "+rootNode);
                debugNode = new Node("DebugNode");
                rootNode.attachChild(debugNode);
            }
            
            if(getVisionControl(spatial) != null) {
                System.out.println("visionControl.getVisibleObjectManager().addListener(this)");
                visionControl.getVisibleObjectManager().addListener(this);
            }
        } else {
            if(getVisionControl(spatial) != null) {
                visionControl.getVisibleObjectManager().removeListener(getId());
            }
        }
        super.setSpatial(spatial);
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if(enabled) {
            if(getVisionControl(spatial) != null) {
                visionControl.getVisibleObjectManager().addListener(this);
            }
        } else {
            if(getVisionControl(spatial) != null) {
                visionControl.getVisibleObjectManager().removeListener(getId());
            }
        }
    }
    
    @Override
    protected void controlUpdate(float f) {
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}
