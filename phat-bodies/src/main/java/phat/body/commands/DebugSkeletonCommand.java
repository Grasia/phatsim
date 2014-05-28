/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
