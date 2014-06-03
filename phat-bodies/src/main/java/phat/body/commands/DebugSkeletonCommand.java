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
package phat.body.commands;

import com.jme3.animation.SkeletonControl;
import com.jme3.app.Application;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.jme3.scene.debug.SkeletonDebugger;
import java.util.logging.Level;
import phat.body.BodiesAppState;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.util.SpatialFactory;

/**
 *
 * @author sala26
 */
public class DebugSkeletonCommand extends PHATCommand {

    private String bodyId;
    private Boolean show;
    private AssetManager assetManager;
    SkeletonDebugger skeletonDebug;

    public DebugSkeletonCommand(String bodyId, Boolean show, PHATCommandListener listener) {
        super(listener);
        this.bodyId = bodyId;
        this.show = show;
        logger.log(Level.INFO, "New Command: {0}", new Object[]{this});
    }

    public DebugSkeletonCommand(String bodyId, Boolean show) {
        this(bodyId, show, null);
    }

    @Override
    public void runCommand(Application app) {
        BodiesAppState bodiesAppState = app.getStateManager().getState(BodiesAppState.class);
        assetManager = app.getAssetManager();

        Node body = bodiesAppState.getAvailableBodies().get(bodyId);
        if (body != null && body.getParent() != null) {
            if (show) {
                showSkeleton(body);
            } else {
                hideSkeleton(body);
            }
        }
        setState(State.Success);
    }

    @Override
    public void interruptCommand(Application app) {
        setState(State.Fail);
    }

    private void hideSkeleton(Node body) {
        if (skeletonDebug != null) {
            skeletonDebug.removeFromParent();
        }
    }

    private void showSkeleton(Node body) {
        if (skeletonDebug == null) {
            SkeletonControl sc = body.getControl(SkeletonControl.class);
            skeletonDebug =
                    new SkeletonDebugger("skeleton", sc.getSkeleton());
            Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mat.setColor("Color", ColorRGBA.Green);
            mat.getAdditionalRenderState().setDepthTest(false);
            skeletonDebug.setMaterial(mat);
        }
        body.attachChild(skeletonDebug);
    }
}
