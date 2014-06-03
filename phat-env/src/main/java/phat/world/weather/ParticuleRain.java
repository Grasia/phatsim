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
package phat.world.weather;

import com.jme3.app.SimpleApplication;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.effect.shapes.EmitterSphereShape;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue;

public class ParticuleRain extends SimpleApplication {

    private ParticleEmitter points;
    private float gravity = 500f;
    private float radius = 100f;
    private float height = 50f;
    private int particlesPerSec = 800;
    private int weather = 1;

    public static void main(String[] args) {
        ParticuleRain app = new ParticuleRain();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        points = new ParticleEmitter(
                "rainPoints", ParticleMesh.Type.Triangle, particlesPerSec * weather);
        points.setShape(new EmitterSphereShape(Vector3f.ZERO, radius));
        points.setLocalTranslation(new Vector3f(0f, height, 0f));
        points.getParticleInfluencer().setInitialVelocity(new Vector3f(0.0f, -1.0f, 0.0f));
        points.getParticleInfluencer().setVelocityVariation(0.1f);
        points.setImagesX(1);
        points.setImagesY(1);
        points.setGravity(0, gravity * weather, 0);
        points.setLowLife(2);
        points.setHighLife(5);
        points.setStartSize(2f);
        points.setEndSize(1f);
        points.setStartColor(new ColorRGBA(0.0f, 0.0f, 1.0f, 0.8f));
        points.setEndColor(new ColorRGBA(0.8f, 0.8f, 1.0f, 0.6f));
        points.setFacingVelocity(false);
        points.setParticlesPerSec(particlesPerSec * weather);
        points.setRotateSpeed(0.0f);
        points.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        Material mat = new Material(assetManager,
                "Common/MatDefs/Misc/Particle.j3md");
        // “raindrop.png” is just “spark.png”, rotated by 90 degrees.
        mat.setTexture(
                "Texture", assetManager.loadTexture(
                "Effects/raindrop.png"));
        points.setMaterial(mat);
        points.setQueueBucket(RenderQueue.Bucket.Transparent);

        rootNode.attachChild(points);

        // Default speed is too slow
        flyCam.setMoveSpeed(20f);
        flyCam.setZoomSpeed(5f);
    }

    @Override
    public void simpleUpdate(float tpf) {
        // TODO
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}