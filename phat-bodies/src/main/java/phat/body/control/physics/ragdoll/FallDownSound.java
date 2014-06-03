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
package phat.body.control.physics.ragdoll;

import com.jme3.animation.Bone;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.RagdollCollisionListener;
import com.jme3.bullet.control.KinematicRagdollControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;
import phat.agents.actors.Actor;
import phat.agents.actors.ActorFactory;
import phat.audio.AudioFactory;
import phat.audio.PHATAudioSource;

/**
 *
 * @author Pablo
 */
public class FallDownSound extends AbstractControl implements RagdollCollisionListener {

    private ConcurrentLinkedQueue<Runnable> collisionActions = new ConcurrentLinkedQueue<Runnable>();
    Actor actor;
    PHATAudioSource noiseSource1;
    PHATAudioSource noiseSource2;
    PHATAudioSource noiseSource3;
    String noise1 = "Sound/HumanEffects/FallingDown/fallingDown1.ogg";
    String noise2 = "Sound/HumanEffects/FallingDown/fallingDown2.ogg";
    String noise3 = "Sound/HumanEffects/FallingDown/fallingDown3.ogg";

    boolean head = false;
    boolean spine = false;
    
    public FallDownSound(Actor actor) {
        this.actor = actor;

        KinematicRagdollControl krc = ActorFactory.findControl(actor.getNode(), KinematicRagdollControl.class);
        krc.addCollisionListener(this);

        noiseSource1 = createAudio(noise1);
        noiseSource2 = createAudio(noise2);
        noiseSource3 = createAudio(noise3);

        actor.getNode().attachChild(noiseSource1);
        actor.getNode().attachChild(noiseSource2);
        actor.getNode().attachChild(noiseSource3);
        
        actor.getNode().addControl(this);
    }

    @Override
    public void collide(Bone bone, PhysicsCollisionObject pco, PhysicsCollisionEvent pce) {
        Runnable collisionAction = null;
        if (!head && bone.getName().equals("Head")) {
            head = true;
            collisionAction = new Runnable() {
                @Override
                public void run() {
                    noiseSource1.playInstance();
                }
            };
        } else if (!spine && bone.getName().equals("Spine1")) {
            spine = true;
            collisionAction = new Runnable() {
                @Override
                public void run() {
                    noiseSource2.playInstance();
                }
            };
        }
        if (collisionAction != null) {
            collisionActions.add(collisionAction);
        }
    }

    private PHATAudioSource createAudio(String resource) {
        PHATAudioSource as = AudioFactory.getInstance().makeAudioSource("FootSteps", resource, Vector3f.ZERO);
        as.setLooping(false);
        as.setPositional(true);
        as.setDirectional(false);
        as.setVolume(0.5f);
        as.setMaxDistance(Float.MAX_VALUE);   
        as.setRefDistance(0.5f);

        //as.setShowRange(true);

        return as;
    }

    @Override
    protected void controlUpdate(float tpf) {
        Vector<Runnable> currentActions = new Vector<Runnable>(collisionActions);
        collisionActions.removeAll(currentActions);
        for (Runnable run : currentActions) {
            run.run();
        };
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        
    }

    @Override
    public Control cloneForSpatial(Spatial sptl) {
        return new FallDownSound(actor);
    }
}