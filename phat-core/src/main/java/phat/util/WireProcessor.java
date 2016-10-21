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
import com.jme3.post.SceneProcessor;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.texture.FrameBuffer;

/**
 * To display the wireframe of the entire scene instead on one material at a time, 
 * first create the following Scene Processor
 *
 * @author Jorge
 */
public class WireProcessor implements SceneProcessor {    

    RenderManager renderManager;
    Material wireMaterial;

    public WireProcessor(AssetManager assetManager) {
        wireMaterial = new Material(assetManager, "/Common/MatDefs/Misc/Unshaded.j3md");
        wireMaterial.setColor("Color", ColorRGBA.Blue);
        wireMaterial.getAdditionalRenderState().setWireframe(true);
    }

    public void initialize(RenderManager rm, ViewPort vp) {
        renderManager = rm;
    }

    public void reshape(ViewPort vp, int w, int h) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isInitialized() {
        return renderManager != null;
    }

    public void preFrame(float tpf) {        
    }

    public void postQueue(RenderQueue rq) {
        renderManager.setForcedMaterial(wireMaterial);
    }

    public void postFrame(FrameBuffer out) {
        renderManager.setForcedMaterial(null);
    }

    public void cleanup() {
        renderManager.setForcedMaterial(null);
    }

}