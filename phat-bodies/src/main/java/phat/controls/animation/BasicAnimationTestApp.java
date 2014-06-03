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
