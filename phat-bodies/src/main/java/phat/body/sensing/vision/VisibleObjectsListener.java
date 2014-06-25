/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.body.sensing.vision;

/**
 * Interface to be notified about visible or not visible objects.
 * 
 * @see VisibleObjectManager
 * 
 * @author pablo <pabcampi@ucm.es>
 */
public interface VisibleObjectsListener {
    public String getId();
    public void visible(VisibleObjInfo objInfo, VisibleObjectManager vom);
    public void noVisible(VisibleObjInfo objInfo, VisibleObjectManager vom);
}
