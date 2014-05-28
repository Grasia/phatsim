/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
