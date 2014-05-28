/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.devices.extractor;

import com.jme3.scene.Node;

/**
 *
 * @author pablo
 */
public class Extractor extends Node {
    ExtractorControl extractorControl;
    
    public Extractor() {
        super();
        
        System.out.println("Extractor()...");
        System.out.println("ExtractorControl()");
        extractorControl = new ExtractorControl();
        System.out.println("addControl(extractorControl)");
        addControl(extractorControl);
        System.out.println("...Extractor()");
    }
    
    public ExtractorControl getExtractorControl() {        
        return extractorControl;
    }
}
