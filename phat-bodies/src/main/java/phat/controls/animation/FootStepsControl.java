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

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import phat.agents.actors.ActorFactory;
import phat.audio.AudioFactory;
import phat.audio.PHATAudioSource;

/**
 *
 * @author pablo
 */
public class FootStepsControl extends AbstractControl {

    private static String footSteps1 = "Sound/HumanEffects/FootSteps/footsteps1.ogg";
    private PHATAudioSource audioSource;
    private BasicCharacterAnimControl basicAnimControl;
    private final AssetManager assetManager;
    private int step = -1;

    public FootStepsControl(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);

        if (getSpatial() != null) {   
            
            System.out.println("finding basicAnimControl...");
            
            basicAnimControl = ActorFactory.findControl(getSpatial(), BasicCharacterAnimControl.class);
            
            System.out.println("basicAnimControl!");

            audioSource = createAudioFootSteps();
            
            System.out.println("createAudioFootSteps!");

            if (getSpatial() instanceof Node) {
                ((Node) getSpatial()).attachChild(audioSource);
            }
        }
    }

    private PHATAudioSource createAudioFootSteps() {
        PHATAudioSource as = AudioFactory.getInstance().makeAudioSource("FootSteps", footSteps1, Vector3f.ZERO);
        as.setLooping(false);
        as.setPositional(true);
        as.setDirectional(false);
        as.setVolume(0.2f);
        as.setMaxDistance(Float.MAX_VALUE);   
        as.setRefDistance(0.5f);

        //as.setShowRange(true);
        
        return as;
    }

    @Override
    protected void controlUpdate(float f) {
        if (basicAnimControl.getCurrentAnimationName().equals("WalkForward")) {
            if (step == -1) {
                step = 1;
            }
            switch (step) {
                case 1:
                    if (basicAnimControl.getAnimTime() < 1.12f && basicAnimControl.getAnimTime() > 0.25f) {
                        audioSource.playInstance();
                        step = 2;
                    }
                    break;
                case 2:
                    if (basicAnimControl.getAnimTime() > 1.12f) {
                        audioSource.playInstance();
                        step = 1;
                    }
                    break;
            }
        }/* else if (!basicAnimControl.getCurrentAnimationName().equals("WalkForward")) {
                if(audioNode1.getStatus() != AudioNode.Status.Playing) {
                    audioNode1.stop();
                }
                if(audioNode2.getStatus() != AudioNode.Status.Playing) {
                    audioNode2.stop();
                }
            step = -1;
        }*/
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    @Override
    public Control cloneForSpatial(Spatial sptl) {
        return new FootStepsControl(assetManager);
    }
}
