/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.util;

import com.jme3.scene.Spatial;

/**
 *
 * @author pablo
 */
public interface PHATSceneGraphVisitor {
    public boolean visit(Spatial spat);
    public Spatial getSpatial();
}
