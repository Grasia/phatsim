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
package phat.examples.extractor;

import com.jme3.math.ColorRGBA;
import com.jme3.scene.shape.Sphere;
import phat.controls.animation.FootStepsControlTestApp;
import phat.devices.extractor.Extractor;
import phat.devices.extractor.ExtractorControl;
import phat.util.SpatialFactory;

/**
 *
 * @author pablo
 */
public class FootStepsAndExtractorTestApp extends FootStepsControlTestApp {
    
    public static void main(String[] args) {
        FootStepsAndExtractorTestApp app = new FootStepsAndExtractorTestApp();
        app.setDisplayFps(false);
        //app.setShowSettings(false);
        app.setPauseOnLostFocus(false);
        app.setDisplayStatView(false);

        app.start();
    }
    
    @Override
    protected void createAudio() {
        super.createAudio();
        
        SpatialFactory.init(assetManager, rootNode);
        
        createExtractor(-20, 0, -20);
        createExtractor(-20, 0, 20);
        createExtractor(20, 0, -20);
        createExtractor(20, 0, 20);
    }
    
    private void createExtractor(float x, float y, float z) {
        Extractor extractor = new Extractor();
        extractor.getExtractorControl().switchTo(ExtractorControl.State.HIGH);
        extractor.attachChild(SpatialFactory.createShape("ExtractorGeo", new Sphere(15, 15, 1), ColorRGBA.Blue));
        extractor.setLocalTranslation(x, y, z);
        rootNode.attachChild(extractor);
    }
}
