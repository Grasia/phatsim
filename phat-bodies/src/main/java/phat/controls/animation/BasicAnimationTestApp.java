/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.controls.animation;

import phat.agents.actors.parkinson.HandTremblingControl;
import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.jme3.animation.Skeleton;
import com.jme3.animation.SkeletonControl;
import com.jme3.scene.Node;
import com.jme3.scene.control.Control;
import java.nio.channels.Channel;
import phat.agents.actors.ActorFactory;
import phat.agents.actors.parkinson.HeadTremblingControl;
import phat.util.Debug;
import phat.util.SimpleScenario;

/**
 *
 * @author pablo
 */
public class BasicAnimationTestApp extends SimpleScenario {

    public static void main(String[] args) {
        BasicAnimationTestApp app = new BasicAnimationTestApp();
        //app.setDisplayFps(false);
        app.setShowSettings(false);
        app.setPauseOnLostFocus(false);
        
        //recordVideoAndAudio(app);
        
        app.start();
    }
    
    @Override
    public void createTerrain() {
        Debug.enableDebugGrid(10f, assetManager, rootNode);
    }

    
    
    @Override
    public void createOtherObjects() {
        ActorFactory.init(rootNode, assetManager, bulletAppState);
        Node model = ActorFactory.createActorModel("Model", "Models/People/Elder/Elder.j3o", 1f);
        rootNode.attachChild(model);
        
       
        //initAnimation(model);
        
        initGestures(model);        
                        
        for(int i = 0; i < model.getNumControls(); i++) {
            Control c = model.getControl(i);
            System.out.println("------>"+c.getClass().getSimpleName());
        }
    }

    private void initGestures(Node model) {
        HandTremblingControl htc = new HandTremblingControl(HandTremblingControl.Hand.LEFT_HAND);
        model.addControl(htc);  
        
        HeadTremblingControl headtc = new HeadTremblingControl();
        model.addControl(headtc);
    }
    
    int cycleCounter = 0;
    private void initAnimation(Node model) {
        AnimControl ac = ActorFactory.findControl(model, AnimControl.class);
        
        for(String animationNames: ac.getAnimationNames()) {
            System.out.println(animationNames);
        }
        
        AnimChannel anim = ac.createChannel();
        anim.setAnim("WalkForward");
        anim.setLoopMode(LoopMode.Loop);
        
        ac.addListener(new AnimEventListener() {
            @Override
            public void onAnimCycleDone(AnimControl ac, AnimChannel ac1, String animName) {
                if(cycleCounter == 5) {
                    if(animName.equals("WalkForward")) {
                        AnimChannel animChanel = ac.getChannel(0);
                        animChanel.setAnim("RunForward", 1f);
                    } else {
                        AnimChannel animChanel = ac.getChannel(0);
                        animChanel.setAnim("WalkForward", 1f);
                    }
                    cycleCounter = 0;
                } else {                 
                    cycleCounter++;
                }
            }

            @Override
            public void onAnimChange(AnimControl ac, AnimChannel ac1, String string) {
            }
        });
    }
    
}
