/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.body.control.animation;

import phat.body.control.animation.BasicCharacterAnimControl.AnimName;

/**
 *
 * @author pablo
 */
public interface AnimFinishedListener {
    public void animFinished(AnimName animationName);
}
